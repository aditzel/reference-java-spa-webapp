package com.allanditzel.dashboard.persistence;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Allan Ditzel
 * @since 1.0
 */
@Repository
public interface StormpathUserMappingRepository extends CrudRepository<StormpathUserMapping, String> {
    StormpathUserMapping findByUsernameIgnoreCase(String username);

    StormpathUserMapping findByStormpathUrlIgnoreCase(String stormpathHref);
}
