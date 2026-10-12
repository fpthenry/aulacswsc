package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ContactMessage;
import com.mycompany.myapp.domain.enumeration.BlogCategory;
import com.mycompany.myapp.repository.BlogPostRepository;
import com.mycompany.myapp.repository.BranchRepository;
import com.mycompany.myapp.repository.ContactMessageRepository;
import com.mycompany.myapp.repository.GalleryImageRepository;
import com.mycompany.myapp.repository.ReviewRepository;
import com.mycompany.myapp.repository.ServiceCategoryRepository;
import com.mycompany.myapp.repository.SiteSettingRepository;
import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.dto.BlogPostDTO;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.dto.GalleryImageDTO;
import com.mycompany.myapp.service.dto.ReviewDTO;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.dto.site.ContactRequestDTO;
import com.mycompany.myapp.service.dto.site.SiteBootstrapDTO;
import com.mycompany.myapp.service.mapper.BlogPostMapper;
import com.mycompany.myapp.service.mapper.BranchMapper;
import com.mycompany.myapp.service.mapper.GalleryImageMapper;
import com.mycompany.myapp.service.mapper.ReviewMapper;
import com.mycompany.myapp.service.mapper.ServiceCategoryMapper;
import com.mycompany.myapp.service.mapper.SiteSettingMapper;
import com.mycompany.myapp.service.mapper.SpaServiceMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Đọc nội dung cho website công khai — chỉ trả dữ liệu đang bật / đã xuất bản / đã duyệt.
 */
@Service
@Transactional(readOnly = true)
public class PublicSiteService {

    private final SiteSettingRepository siteSettingRepository;
    private final BranchRepository branchRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final SpaServiceRepository spaServiceRepository;
    private final GalleryImageRepository galleryImageRepository;
    private final BlogPostRepository blogPostRepository;
    private final ReviewRepository reviewRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final SiteSettingMapper siteSettingMapper;
    private final BranchMapper branchMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final SpaServiceMapper spaServiceMapper;
    private final GalleryImageMapper galleryImageMapper;
    private final BlogPostMapper blogPostMapper;
    private final ReviewMapper reviewMapper;

    public PublicSiteService(
        SiteSettingRepository siteSettingRepository,
        BranchRepository branchRepository,
        ServiceCategoryRepository serviceCategoryRepository,
        SpaServiceRepository spaServiceRepository,
        GalleryImageRepository galleryImageRepository,
        BlogPostRepository blogPostRepository,
        ReviewRepository reviewRepository,
        ContactMessageRepository contactMessageRepository,
        SiteSettingMapper siteSettingMapper,
        BranchMapper branchMapper,
        ServiceCategoryMapper serviceCategoryMapper,
        SpaServiceMapper spaServiceMapper,
        GalleryImageMapper galleryImageMapper,
        BlogPostMapper blogPostMapper,
        ReviewMapper reviewMapper
    ) {
        this.siteSettingRepository = siteSettingRepository;
        this.branchRepository = branchRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.spaServiceRepository = spaServiceRepository;
        this.galleryImageRepository = galleryImageRepository;
        this.blogPostRepository = blogPostRepository;
        this.reviewRepository = reviewRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.siteSettingMapper = siteSettingMapper;
        this.branchMapper = branchMapper;
        this.serviceCategoryMapper = serviceCategoryMapper;
        this.spaServiceMapper = spaServiceMapper;
        this.galleryImageMapper = galleryImageMapper;
        this.blogPostMapper = blogPostMapper;
        this.reviewMapper = reviewMapper;
    }

    public SiteBootstrapDTO bootstrap() {
        SiteSettingDTO setting = siteSettingRepository
            .findFirstByOrderByIdAsc()
            .map(siteSettingMapper::toDto)
            .orElseGet(SiteSettingDTO::new);
        List<BranchDTO> branches = branchRepository
            .findAllByActiveTrueOrderByDisplayOrderAscIdAsc()
            .stream()
            .map(branchMapper::toDto)
            .toList();
        List<ServiceCategoryDTO> categories = serviceCategoryRepository
            .findAllByActiveTrueOrderByDisplayOrderAscIdAsc()
            .stream()
            .map(serviceCategoryMapper::toDto)
            .toList();
        List<SpaServiceDTO> services = spaServiceRepository
            .findAllActiveWithCategory()
            .stream()
            .map(spaServiceMapper::toDto)
            .map(s -> {
                s.getStaffs().clear(); // không đưa danh sách nhân viên ra website
                return s;
            })
            .toList();
        return new SiteBootstrapDTO(setting, branches, categories, services);
    }

    public List<GalleryImageDTO> gallery() {
        return galleryImageRepository.findAllByActiveTrueOrderByDisplayOrderAscIdDesc().stream().map(galleryImageMapper::toDto).toList();
    }

    public Page<ReviewDTO> reviews(Pageable pageable) {
        return reviewRepository
            .findAllByApprovedTrue(pageable)
            .map(reviewMapper::toDto)
            .map(r -> {
                r.setCustomer(null); // không lộ id hồ sơ khách
                return r;
            });
    }

    public Page<BlogPostDTO> blogPosts(BlogCategory category, Pageable pageable) {
        Page<BlogPostDTO> page =
            (category == null
                    ? blogPostRepository.findAllByPublishedTrue(pageable)
                    : blogPostRepository.findAllByPublishedTrueAndCategory(category, pageable)).map(blogPostMapper::toDto);
        // danh sách không cần nội dung đầy đủ
        page.forEach(p -> {
            p.setContentVi(null);
            p.setContentEn(null);
        });
        return page;
    }

    public Optional<BlogPostDTO> blogPost(String slug) {
        return blogPostRepository.findOneBySlugAndPublishedTrue(slug).map(blogPostMapper::toDto);
    }

    public List<BlogPostDTO> allPublishedPosts() {
        return blogPostRepository.findAllByPublishedTrue().stream().map(blogPostMapper::toDto).toList();
    }

    @Transactional
    public void saveContact(ContactRequestDTO request) {
        ContactMessage message = new ContactMessage();
        message.setFullName(request.fullName().trim());
        message.setPhone(request.phone());
        message.setEmail(request.email());
        message.setMessage(request.message().trim());
        message.setHandled(false);
        message.setCreatedDate(Instant.now());
        contactMessageRepository.save(message);
    }
}
