package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

@SuppressWarnings("unused")
public interface TNCSpeciesRepo
        extends PagingAndSortingRepository<TNCSpecies, Long>, JpaSpecificationExecutor<TNCSpecies> {
//
//    @Query("SELECT data FROM TNCSpecies data WHERE " +
//            "postStatus = :postStatus " +
//            "GROUP BY data.oid " +
//            "ORDER BY data.oid ASC")
//    Page<TNCSpecies> getDataByPostStatus(Pageable pageable, @Param("postStatus") String postStatus);


    @Query("SELECT data FROM TNCSpecies data " +
            "GROUP BY data.oid " +
            "ORDER BY data.oid ASC")
    Page<TNCSpecies> getDataByPostStatus(Pageable pageable);

    long countByPostStatus(String postStatus);

}
