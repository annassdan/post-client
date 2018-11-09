package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

@SuppressWarnings("unused")
public interface TNCDeepslopeRepo
        extends PagingAndSortingRepository<TNCDeepslope, Long>, JpaSpecificationExecutor<TNCDeepslope> {

    Page<TNCDeepslope> findAllByPostStatus(Pageable pageable, String postStatus);

    long countByPostStatus(String postStatus);


//    @Query("SELECT data FROM TNCDeepslope data WHERE " +
//            "data.postStatus = :postStatus ")
//    Page<TNCDeepslope> getAllDataByPostStatus(Pageable pageable, String postStatus);

}
