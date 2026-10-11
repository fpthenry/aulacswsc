package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Staff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface StaffRepositoryWithBagRelationships {
    Optional<Staff> fetchBagRelationships(Optional<Staff> staff);

    List<Staff> fetchBagRelationships(List<Staff> staff);

    Page<Staff> fetchBagRelationships(Page<Staff> staff);
}
