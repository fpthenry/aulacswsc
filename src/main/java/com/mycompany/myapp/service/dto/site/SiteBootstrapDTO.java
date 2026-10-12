package com.mycompany.myapp.service.dto.site;

import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import java.util.List;

/**
 * Dữ liệu website tải một lần khi mở trang: cài đặt, cơ sở, danh mục và dịch vụ đang hoạt động.
 */
public record SiteBootstrapDTO(
    SiteSettingDTO setting,
    List<BranchDTO> branches,
    List<ServiceCategoryDTO> categories,
    List<SpaServiceDTO> services
) {}
