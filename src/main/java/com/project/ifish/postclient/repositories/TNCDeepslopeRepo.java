package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface TNCDeepslopeRepo
        extends PagingAndSortingRepository<TNCDeepslope, Long>, JpaSpecificationExecutor<TNCDeepslope> {

//    @Query("SELECT data FROM TNCDeepslope data WHERE " +
//            "postStatus = :postStatus AND " +
//            "boatId != :boatId " +
//            "GROUP BY data.oid " +
//            "ORDER BY data.oid ASC, data.landingDate ASC")
//    Page<TNCDeepslope> getDataByPostStatusAndBoatNotZero(Pageable pageable,
//                                                         @Param("postStatus") String postStatus,
//                                                         @Param("boatId") Long boatId);
//
    @Query("SELECT data FROM TNCDeepslope data WHERE " +
            "boatId != :boatId " +
            "GROUP BY data.oid " +
            "ORDER BY data.oid ASC, data.landingDate ASC")
    Page<TNCDeepslope> getDataByPostStatusAndBoatNotZero(Pageable pageable,
                                                         @Param("boatId") Long boatId);

    @Query("SELECT COUNT(data) FROM TNCDeepslope data WHERE " +
            "UPPER(postStatus) = UPPER(:postStatus) AND " +
            "boatId != :boatId ")
    long tryingCountByPostStatusAndBoatIdNot(@Param("postStatus") String postStatus,
                                             @Param("boatId") Long boatId);






}
