package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.BlogPost;
import com.mycompany.myapp.repository.BlogPostRepository;
import com.mycompany.myapp.service.BlogPostService;
import com.mycompany.myapp.service.SlugService;
import com.mycompany.myapp.service.dto.BlogPostDTO;
import com.mycompany.myapp.service.mapper.BlogPostMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BlogPost}.
 */
@Service
@Transactional
public class BlogPostServiceImpl implements BlogPostService {

    private static final Logger LOG = LoggerFactory.getLogger(BlogPostServiceImpl.class);

    private final BlogPostRepository blogPostRepository;

    private final BlogPostMapper blogPostMapper;

    private final SlugService slugService;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository, BlogPostMapper blogPostMapper, SlugService slugService) {
        this.blogPostRepository = blogPostRepository;
        this.blogPostMapper = blogPostMapper;
        this.slugService = slugService;
    }

    @Override
    public BlogPostDTO save(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to save BlogPost : {}", blogPostDTO);
        // slug luôn do server sinh: tiêu đề không dấu + ngày (không nhận slug từ form)
        blogPostDTO.setSlug(
            slugService.create(
                blogPostDTO.getTitleVi(),
                SlugService.dateOf(blogPostDTO.getPublishedAt()),
                280,
                blogPostRepository::existsBySlug,
                "bai-viet"
            )
        );
        BlogPost blogPost = blogPostMapper.toEntity(blogPostDTO);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(blogPost);
    }

    @Override
    public BlogPostDTO update(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to update BlogPost : {}", blogPostDTO);
        // giữ nguyên slug đã có để link cũ không hỏng
        blogPostRepository.findById(blogPostDTO.getId()).ifPresent(existing -> blogPostDTO.setSlug(existing.getSlug()));
        BlogPost blogPost = blogPostMapper.toEntity(blogPostDTO);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(blogPost);
    }

    @Override
    public Optional<BlogPostDTO> partialUpdate(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to partially update BlogPost : {}", blogPostDTO);
        blogPostDTO.setSlug(null); // slug không đổi được qua PATCH (null = giữ nguyên)

        return blogPostRepository
            .findById(blogPostDTO.getId())
            .map(existingBlogPost -> {
                blogPostMapper.partialUpdate(existingBlogPost, blogPostDTO);

                return existingBlogPost;
            })
            .map(blogPostRepository::save)
            .map(blogPostMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BlogPostDTO> findOne(Long id) {
        LOG.debug("Request to get BlogPost : {}", id);
        return blogPostRepository.findById(id).map(blogPostMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BlogPost : {}", id);
        blogPostRepository.deleteById(id);
    }
}
