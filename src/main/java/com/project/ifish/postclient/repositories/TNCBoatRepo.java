package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCBoat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


@SuppressWarnings("unused")
public interface TNCBoatRepo
        extends PagingAndSortingRepository<TNCBoat, Long>, JpaSpecificationExecutor<TNCBoat> {

    Page<TNCBoat> findAllByPostStatus(Pageable pageable, String postStatus);

    long countByPostStatus(String postStatus);

}
