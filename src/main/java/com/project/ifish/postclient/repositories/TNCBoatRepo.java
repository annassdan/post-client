package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCBoat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


@SuppressWarnings("unused")
public interface TNCBoatRepo
        extends PagingAndSortingRepository<TNCBoat, Long>, JpaSpecificationExecutor<TNCBoat> {

//    @Query("SELECT data FROM TNCBoat data WHERE " +
//            "postStatus = :postStatus " +
//            "GROUP BY data.oid " +
//            "ORDER BY data.oid ASC")
//    Page<TNCBoat> getDataByPostStatus(Pageable pageable, @Param("postStatus") String postStatus);

    @Query("SELECT data FROM TNCBoat data " +
            "GROUP BY data.oid " +
            "ORDER BY data.oid ASC")
    Page<TNCBoat> getDataByPostStatus(Pageable pageable);

    @Query("SELECT COUNT(data) FROM TNCBoat data WHERE " +
            "UPPER(postStatus) = UPPER(:postStatus) ")
    long tryingCountByPostStatus(@Param("postStatus") String postStatus);

}
