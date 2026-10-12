package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.BookingItem;
import com.mycompany.myapp.domain.enumeration.BookingSource;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import com.mycompany.myapp.repository.BookingItemRepository;
import com.mycompany.myapp.repository.BookingRepository;
import com.mycompany.myapp.repository.ContactMessageRepository;
import com.mycompany.myapp.repository.ReviewRepository;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.BadgesDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.BookingRowDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.DashboardDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.DayCountDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.StatusUpdateDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.TopServiceDTO;
import com.mycompany.myapp.service.dto.site.BookingConfirmationDTO;
import com.mycompany.myapp.service.dto.site.BookingRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Nghiệp vụ trang quản trị: số liệu tổng quan, bảng lịch hẹn theo ngày, đổi trạng thái một chạm, tạo nhanh lịch hẹn.
 */
@Service
@Transactional
public class BackofficeService {

    private static final ZoneId ZONE = PublicBookingService.ZONE;
    /** Trạng thái không tính là "đang có lịch" (không chiếm chỗ, không tính khách). */
    private static final Set<BookingStatus> INACTIVE = EnumSet.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW);

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ReviewRepository reviewRepository;
    private final PublicBookingService publicBookingService;

    public BackofficeService(
        BookingRepository bookingRepository,
        BookingItemRepository bookingItemRepository,
        ContactMessageRepository contactMessageRepository,
        ReviewRepository reviewRepository,
        PublicBookingService publicBookingService
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.reviewRepository = reviewRepository;
        this.publicBookingService = publicBookingService;
    }

    // ===================== Tổng quan =====================

    @Transactional(readOnly = true)
    public BadgesDTO badges() {
        return new BadgesDTO(
            bookingRepository.countByStatus(BookingStatus.PENDING),
            contactMessageRepository.countByHandledIsNullOrHandledFalse(),
            reviewRepository.countByApprovedIsNullOrApprovedFalse()
        );
    }

    @Transactional(readOnly = true)
    public DashboardDTO dashboard() {
        LocalDate today = LocalDate.now(ZONE);

        List<Booking> todayList = bookingRepository.findAllInRangeWithDetails(startOf(today), startOf(today.plusDays(1)));
        List<BookingRowDTO> todaySchedule = toRows(todayList);
        List<Booking> todayActive = todayList.stream().filter(b -> !INACTIVE.contains(b.getStatus())).toList();

        LocalDate monthStart = today.withDayOfMonth(1);
        List<Booking> month = bookingRepository.findAllInRangeWithDetails(startOf(monthStart), startOf(monthStart.plusMonths(1)));
        List<Booking> monthCompleted = month.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).toList();

        // 7 ngày tới (tính cả hôm nay)
        List<Booking> week = bookingRepository.findAllInRangeWithDetails(startOf(today), startOf(today.plusDays(7)));
        Map<LocalDate, List<Booking>> byDay = week
            .stream()
            .filter(b -> !INACTIVE.contains(b.getStatus()))
            .collect(Collectors.groupingBy(b -> LocalDate.ofInstant(b.getStartTime(), ZONE)));
        List<DayCountDTO> next7Days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.plusDays(i);
            List<Booking> list = byDay.getOrDefault(d, List.of());
            next7Days.add(new DayCountDTO(d, list.size(), guests(list)));
        }

        // Dịch vụ được đặt nhiều nhất trong tháng (không tính lịch hủy / không đến)
        List<Long> monthActiveIds = month.stream().filter(b -> !INACTIVE.contains(b.getStatus())).map(Booking::getId).toList();
        Map<Long, int[]> counter = new LinkedHashMap<>();
        Map<Long, String> names = new LinkedHashMap<>();
        if (!monthActiveIds.isEmpty()) {
            for (BookingItem item : bookingItemRepository.findAllByBookingIdsWithDetails(monthActiveIds)) {
                if (item.getService() != null) {
                    counter.computeIfAbsent(item.getService().getId(), k -> new int[1])[0] += item.getQuantity() == null
                        ? 1
                        : item.getQuantity();
                    names.put(item.getService().getId(), item.getService().getNameVi());
                }
            }
        }
        List<TopServiceDTO> topServices = counter
            .entrySet()
            .stream()
            .sorted((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]))
            .limit(5)
            .map(e -> new TopServiceDTO(e.getKey(), names.get(e.getKey()), e.getValue()[0]))
            .toList();

        BadgesDTO badges = badges();
        return new DashboardDTO(
            todayActive.size(),
            guests(todayActive),
            sum(todayList.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).toList()),
            sum(monthCompleted),
            monthCompleted.size(),
            badges.pendingBookings(),
            badges.unhandledMessages(),
            badges.pendingReviews(),
            todaySchedule,
            next7Days,
            topServices
        );
    }

    // ===================== Bảng lịch hẹn =====================

    /**
     * Lịch hẹn của một ngày (hoặc nhiều ngày khi {@code days} > 1), lọc theo cơ sở, trạng thái, từ khóa (mã / tên / SĐT).
     */
    @Transactional(readOnly = true)
    public List<BookingRowDTO> bookings(LocalDate date, int days, Long branchId, BookingStatus status, String q) {
        List<Booking> list = bookingRepository.findAllInRangeWithDetails(startOf(date), startOf(date.plusDays(Math.max(1, days))));
        String term = StringUtils.hasText(q) ? q.trim().toLowerCase(Locale.ROOT) : null;
        String digits = term == null ? null : term.replaceAll("\\D", "");
        List<Booking> filtered = list
            .stream()
            .filter(b -> branchId == null || (b.getBranch() != null && branchId.equals(b.getBranch().getId())))
            .filter(b -> status == null || status == b.getStatus())
            .filter(b -> term == null || matches(b, term, digits))
            .toList();
        return toRows(filtered);
    }

    public BookingRowDTO updateStatus(Long id, StatusUpdateDTO update) {
        Booking booking = bookingRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", "booking", "idnotfound"));
        booking.setStatus(update.status());
        if (update.paymentStatus() != null) {
            booking.setPaymentStatus(update.paymentStatus());
        }
        if (update.paymentMethod() != null) {
            booking.setPaymentMethod(update.paymentMethod());
        }
        return toRows(List.of(bookingRepository.save(booking))).get(0);
    }

    /** Lễ tân tạo nhanh lịch hẹn (khách gọi điện / đến trực tiếp): xác nhận luôn. */
    public BookingConfirmationDTO quickCreate(BookingRequestDTO request, BookingSource source) {
        BookingSource src = source == null ? BookingSource.PHONE : source;
        return publicBookingService.create(request, src, BookingStatus.CONFIRMED, true);
    }

    // ===================== Hàm hỗ trợ =====================

    private static Instant startOf(LocalDate d) {
        return d.atStartOfDay(ZONE).toInstant();
    }

    private static int guests(List<Booking> list) {
        return list.stream().mapToInt(b -> b.getNumberOfPeople() == null ? 1 : b.getNumberOfPeople()).sum();
    }

    private static BigDecimal sum(List<Booking> list) {
        return list.stream().map(Booking::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static boolean matches(Booking b, String term, String digits) {
        if (b.getCode() != null && b.getCode().toLowerCase(Locale.ROOT).contains(term)) {
            return true;
        }
        if (b.getCustomer() == null) {
            return false;
        }
        String name = Optional.ofNullable(b.getCustomer().getFullName()).orElse("").toLowerCase(Locale.ROOT);
        String phone = Optional.ofNullable(b.getCustomer().getPhone()).orElse("").replaceAll("\\D", "");
        return name.contains(term) || (!digits.isEmpty() && phone.contains(digits));
    }

    private List<BookingRowDTO> toRows(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return List.of();
        }
        Map<Long, List<BookingItem>> items = bookingItemRepository
            .findAllByBookingIdsWithDetails(bookings.stream().map(Booking::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(i -> i.getBooking().getId()));
        return bookings
            .stream()
            .sorted(Comparator.comparing(Booking::getStartTime))
            .map(b -> {
                List<BookingItem> its = items.getOrDefault(b.getId(), List.of());
                return new BookingRowDTO(
                    b.getId(),
                    b.getCode(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getNumberOfPeople() == null ? 1 : b.getNumberOfPeople(),
                    b.getStatus(),
                    b.getSource(),
                    b.getPaymentStatus(),
                    b.getPaymentMethod(),
                    b.getTotalAmount(),
                    b.getNote(),
                    b.getCustomer() == null ? null : b.getCustomer().getId(),
                    b.getCustomer() == null ? null : b.getCustomer().getFullName(),
                    b.getCustomer() == null ? null : b.getCustomer().getPhone(),
                    b.getBranch() == null ? null : b.getBranch().getId(),
                    b.getBranch() == null ? null : b.getBranch().getNameVi(),
                    its.stream().map(BookingItem::getService).filter(Objects::nonNull).map(s -> s.getNameVi()).toList(),
                    its.stream().map(BookingItem::getStaff).filter(Objects::nonNull).map(s -> s.getFullName()).distinct().toList()
                );
            })
            .toList();
    }
}
