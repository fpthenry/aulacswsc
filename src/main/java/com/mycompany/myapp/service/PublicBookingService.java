package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.BookingItem;
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.enumeration.BookingSource;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import com.mycompany.myapp.domain.enumeration.PaymentStatus;
import com.mycompany.myapp.repository.BookingItemRepository;
import com.mycompany.myapp.repository.BookingRepository;
import com.mycompany.myapp.repository.BranchRepository;
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.dto.site.BookingConfirmationDTO;
import com.mycompany.myapp.service.dto.site.BookingRequestDTO;
import com.mycompany.myapp.service.dto.site.TimeSlotDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Đặt lịch từ website công khai.
 *
 * <ul>
 *   <li>Khung giờ 15 phút từ {@code Branch.openTime} đến {@code Branch.lastBookingTime}.</li>
 *   <li>Một khung còn trống khi trong suốt thời lượng dịch vụ, tổng số khách đang phục vụ + số khách mới
 *       không vượt {@code Branch.capacity}. Lịch CANCELLED / NO_SHOW không chiếm chỗ.</li>
 *   <li>Giá tính ở server: (giá khuyến mãi nếu có, ngược lại giá gốc) × số người — không tin giá client gửi.</li>
 *   <li>Khách được nhận diện theo số điện thoại: chưa có thì tạo hồ sơ {@link Customer} mới.</li>
 * </ul>
 */
@Service
@Transactional
public class PublicBookingService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicBookingService.class);

    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    public static final int SLOT_MINUTES = 15;
    /** Phải đặt trước ít nhất 30 phút. */
    public static final int LEAD_MINUTES = 30;
    /** Cho đặt trước tối đa 60 ngày. */
    public static final int MAX_DAYS_AHEAD = 60;
    /** Lịch do admin tạo không có giờ kết thúc thì coi như kéo dài 60 phút. */
    private static final int DEFAULT_DURATION_MINUTES = 60;
    private static final Set<BookingStatus> NOT_OCCUPYING = EnumSet.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW);
    private static final String ENTITY_NAME = "booking";
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final DateTimeFormatter CODE_DATE = DateTimeFormatter.ofPattern("yyMMdd");

    private final SecureRandom random = new SecureRandom();

    private final BranchRepository branchRepository;
    private final SpaServiceRepository spaServiceRepository;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final CustomerRepository customerRepository;

    public PublicBookingService(
        BranchRepository branchRepository,
        SpaServiceRepository spaServiceRepository,
        BookingRepository bookingRepository,
        BookingItemRepository bookingItemRepository,
        CustomerRepository customerRepository
    ) {
        this.branchRepository = branchRepository;
        this.spaServiceRepository = spaServiceRepository;
        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.customerRepository = customerRepository;
    }

    // ===================== Khung giờ =====================

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> availability(Long branchId, LocalDate date, int durationMinutes, int people) {
        Branch branch = findBranch(branchId);
        validateDate(date);
        int duration = Math.max(SLOT_MINUTES, durationMinutes);
        List<Booking> existing = bookingsAround(branch, date);
        ZonedDateTime earliest = ZonedDateTime.now(ZONE).plusMinutes(LEAD_MINUTES);

        List<TimeSlotDTO> slots = new ArrayList<>();
        for (LocalTime time : slotTimes(branch)) {
            ZonedDateTime start = date.atTime(time).atZone(ZONE);
            String status;
            if (start.isBefore(earliest)) {
                status = TimeSlotDTO.PAST;
            } else if (fits(existing, start.toInstant(), duration, people, branch.getCapacity())) {
                status = TimeSlotDTO.AVAILABLE;
            } else {
                status = TimeSlotDTO.FULL;
            }
            slots.add(new TimeSlotDTO(time.toString(), status));
        }
        return slots;
    }

    // ===================== Đặt lịch =====================

    /**
     * synchronized: kiểm tra chỗ trống và lưu là một bước, tránh 2 khách cùng giữ chỗ cuối cùng (1 máy chủ).
     */
    public synchronized BookingConfirmationDTO create(BookingRequestDTO request) {
        Branch branch = request.branchId() == null && request.quick()
            ? branchRepository
                .findAllByActiveTrueOrderByDisplayOrderAscIdAsc()
                .stream()
                .findFirst()
                .orElseThrow(() -> error("No active branch", "branchnotfound"))
            : findBranch(request.branchId());

        String phone = normalizePhone(request.phone()).orElseThrow(() -> error("Invalid Vietnamese phone number", "invalidphone"));
        String fullName = StringUtils.hasText(request.fullName()) ? request.fullName().trim() : null;
        if (fullName == null && !request.quick()) {
            throw error("Full name is required", "namerequired");
        }
        int people = request.numberOfPeople() == null ? 1 : request.numberOfPeople();

        // Dịch vụ: giữ thứ tự khách chọn, bỏ trùng
        Map<Long, SpaService> byId = new LinkedHashMap<>();
        spaServiceRepository.findAllByIdInAndActiveTrue(request.serviceIds()).forEach(s -> byId.put(s.getId(), s));
        List<SpaService> services = request.serviceIds().stream().distinct().map(byId::get).toList();
        if (services.isEmpty() || services.contains(null)) {
            throw error("Service not found or inactive", "servicenotfound");
        }
        int duration = services.stream().mapToInt(SpaService::getDurationMinutes).sum();

        // Thời gian
        validateDate(request.date());
        LocalTime time = LocalTime.parse(request.time());
        if (!slotTimes(branch).contains(time)) {
            throw error("Time is outside opening hours", "invalidtime");
        }
        ZonedDateTime start = request.date().atTime(time).atZone(ZONE);
        if (start.isBefore(ZonedDateTime.now(ZONE).plusMinutes(LEAD_MINUTES))) {
            throw error("Time has passed", "pasttime");
        }
        if (!fits(bookingsAround(branch, request.date()), start.toInstant(), duration, people, branch.getCapacity())) {
            throw error("This time slot is full", "slotfull");
        }

        Customer customer = findOrCreateCustomer(phone, fullName, request.email());

        Booking booking = new Booking();
        booking.setCode(newCode());
        booking.setBranch(branch);
        booking.setCustomer(customer);
        booking.setStartTime(start.toInstant());
        booking.setEndTime(start.plusMinutes(duration).toInstant());
        booking.setNumberOfPeople(people);
        booking.setStatus(BookingStatus.PENDING);
        booking.setSource(request.quick() ? BookingSource.QUICK : BookingSource.WEBSITE);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setNote(StringUtils.hasText(request.note()) ? request.note().trim() : null);
        booking.setCreatedDate(Instant.now());

        BigDecimal total = BigDecimal.ZERO;
        List<BookingItem> items = new ArrayList<>();
        for (SpaService service : services) {
            BigDecimal unit = unitPrice(service);
            BookingItem item = new BookingItem();
            item.setService(service);
            item.setPrice(unit);
            item.setQuantity(people);
            item.setDurationMinutes(service.getDurationMinutes());
            items.add(item);
            total = total.add(unit.multiply(BigDecimal.valueOf(people)));
        }
        booking.setTotalAmount(total);
        booking = bookingRepository.save(booking);
        for (BookingItem item : items) {
            item.setBooking(booking);
        }
        bookingItemRepository.saveAll(items);
        LOG.info("Website booking created: {} at branch {} for {} people", booking.getCode(), branch.getId(), people);

        return toConfirmation(booking, items);
    }

    // ===================== Tra cứu =====================

    @Transactional(readOnly = true)
    public Optional<BookingConfirmationDTO> lookup(String code, String rawPhone) {
        Optional<String> phone = normalizePhone(rawPhone);
        if (phone.isEmpty()) {
            return Optional.empty();
        }
        return bookingRepository
            .findOneByCode(code.trim().toUpperCase())
            .filter(b -> b.getCustomer() != null && phone.equals(normalizePhone(b.getCustomer().getPhone())))
            .map(b -> toConfirmation(b, bookingItemRepository.findAllByBookingIdWithService(b.getId())));
    }

    // ===================== Hàm hỗ trợ =====================

    /**
     * Chuẩn hóa số điện thoại di động Việt Nam về dạng 0xxxxxxxxx (10 số).
     * Chấp nhận dấu cách, chấm, gạch và tiền tố +84 / 84.
     */
    public static Optional<String> normalizePhone(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String digits = raw.replaceAll("[\\s.\\-()]", "");
        if (digits.startsWith("+84")) {
            digits = "0" + digits.substring(3);
        } else if (digits.startsWith("84") && digits.length() == 11) {
            digits = "0" + digits.substring(2);
        }
        return digits.matches("^0[35789]\\d{8}$") ? Optional.of(digits) : Optional.empty();
    }

    static BigDecimal unitPrice(SpaService service) {
        return service.getSalePrice() != null ? service.getSalePrice() : service.getPrice();
    }

    private Branch findBranch(Long branchId) {
        if (branchId == null) {
            throw error("Branch is required", "branchnotfound");
        }
        return branchRepository.findOneByIdAndActiveTrue(branchId).orElseThrow(() -> error("Branch not found", "branchnotfound"));
    }

    private void validateDate(LocalDate date) {
        LocalDate today = LocalDate.now(ZONE);
        if (date.isBefore(today)) {
            throw error("Date has passed", "pastdate");
        }
        if (date.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
            throw error("Date is too far ahead", "datetoofar");
        }
    }

    /** Các mốc giờ hợp lệ của cơ sở: openTime, +15', ... đến lastBookingTime. */
    private List<LocalTime> slotTimes(Branch branch) {
        LocalTime open = LocalTime.parse(branch.getOpenTime());
        LocalTime last = LocalTime.parse(branch.getLastBookingTime());
        List<LocalTime> times = new ArrayList<>();
        for (LocalTime t = open; !t.isAfter(last); t = t.plusMinutes(SLOT_MINUTES)) {
            times.add(t);
            if (t.plusMinutes(SLOT_MINUTES).isBefore(t)) {
                break; // qua nửa đêm
            }
        }
        return times;
    }

    private List<Booking> bookingsAround(Branch branch, LocalDate date) {
        Instant from = date.atStartOfDay(ZONE).minusHours(12).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZONE).toInstant();
        return bookingRepository.findAllByBranchIdAndStartTimeBetweenAndStatusNotIn(branch.getId(), from, to, NOT_OCCUPYING);
    }

    /** Kiểm tra từng mốc 15 phút trong khoảng [start, start + duration). */
    private boolean fits(List<Booking> existing, Instant start, int durationMinutes, int people, Integer capacity) {
        int max = capacity == null ? 1 : capacity;
        for (int m = 0; m < durationMinutes; m += SLOT_MINUTES) {
            Instant tick = start.plus(Duration.ofMinutes(m));
            int occupied = 0;
            for (Booking b : existing) {
                Instant end = b.getEndTime() != null ? b.getEndTime() : b.getStartTime().plus(Duration.ofMinutes(DEFAULT_DURATION_MINUTES));
                if (!tick.isBefore(b.getStartTime()) && tick.isBefore(end)) {
                    occupied += b.getNumberOfPeople() == null ? 1 : b.getNumberOfPeople();
                }
            }
            if (occupied + people > max) {
                return false;
            }
        }
        return true;
    }

    private Customer findOrCreateCustomer(String phone, String fullName, String email) {
        return customerRepository
            .findOneByPhone(phone)
            .map(c -> {
                if (!StringUtils.hasText(c.getEmail()) && StringUtils.hasText(email)) {
                    c.setEmail(email.trim().toLowerCase());
                }
                return c;
            })
            .orElseGet(() -> {
                Customer c = new Customer();
                c.setPhone(phone);
                c.setFullName(fullName != null ? fullName : phone);
                c.setEmail(StringUtils.hasText(email) ? email.trim().toLowerCase() : null);
                c.setCreatedDate(Instant.now());
                return customerRepository.save(c);
            });
    }

    /** Mã dạng AL + yyMMdd + 4 ký tự (bỏ 0/O/1/I dễ nhầm), vd AL261011K7QM. */
    private String newCode() {
        String prefix = "AL" + LocalDate.now(ZONE).format(CODE_DATE);
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = 0; i < 4; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            String code = sb.toString();
            if (!bookingRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique booking code");
    }

    private BookingConfirmationDTO toConfirmation(Booking booking, List<BookingItem> items) {
        Branch b = booking.getBranch();
        BookingConfirmationDTO.BranchInfo branch = new BookingConfirmationDTO.BranchInfo(
            b.getId(),
            b.getNameVi(),
            b.getNameEn(),
            b.getAddressVi(),
            b.getAddressEn(),
            b.getPhone(),
            b.getMapDirectionUrl()
        );
        List<BookingConfirmationDTO.ServiceLine> lines = items
            .stream()
            .map(i ->
                new BookingConfirmationDTO.ServiceLine(
                    i.getService().getId(),
                    i.getService().getNameVi(),
                    i.getService().getNameEn(),
                    i.getPrice(),
                    i.getQuantity(),
                    i.getDurationMinutes()
                )
            )
            .toList();
        int duration = items.stream().mapToInt(i -> i.getDurationMinutes() == null ? 0 : i.getDurationMinutes()).sum();
        Customer c = booking.getCustomer();
        return new BookingConfirmationDTO(
            booking.getCode(),
            booking.getStatus().name(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getNumberOfPeople() == null ? 1 : booking.getNumberOfPeople(),
            duration,
            booking.getTotalAmount(),
            c.getFullName(),
            c.getPhone(),
            c.getEmail(),
            booking.getNote(),
            branch,
            lines
        );
    }

    private static BadRequestAlertException error(String message, String key) {
        return new BadRequestAlertException(message, ENTITY_NAME, key);
    }
}
