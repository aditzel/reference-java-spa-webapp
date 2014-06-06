package com.allanditzel.dashboard.persistence;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Allan on 6/4/2014.
 */
@Repository
public interface StormpathUserMappingRepository extends CrudRepository<StormpathUserMapping, String> {
    StormpathUserMapping findByUsernameIgnoreCase(String username);

    StormpathUserMapping findByStormpathUrlIgnoreCase(String stormpathHref);
}
