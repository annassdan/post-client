package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCSizing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

@SuppressWarnings("unused")
public interface TNCSizingRepo
        extends PagingAndSortingRepository<TNCSizing, Long>, JpaSpecificationExecutor<TNCSizing> {

    Page<TNCSizing> findAllByPostStatus(Pageable pageable, String postStatus);

    long countByPostStatus(String postStatus);

    long countByLandingId(Long landingId);

    Page<TNCSizing> findAllByLandingId(Pageable pageable, Long landingId);

}
