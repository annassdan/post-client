package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCSizing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

@SuppressWarnings("unused")
public interface TNCSizingRepo
        extends PagingAndSortingRepository<TNCSizing, Long>, JpaSpecificationExecutor<TNCSizing> {



    @Query("SELECT data FROM TNCSizing data WHERE " +
            "landingId = :landingId AND " +
            "UPPERCASE(postStatus) = UPPERCASE(:postStatus) AND " +
            "fishId != :fishId " +
            "GROUP BY data.oid " +
            "ORDER BY data.oid ASC")
    Page<TNCSizing> getDataByLandingIdAndPostStatusAndFishIdNotZero(Pageable pageable,
                                                                                       @Param("landingId") Long landingId,
                                                                                       @Param("postStatus") String postStatus,
                                                                                       @Param("fishId") Long fishId);

    @Query("SELECT COUNT(data) FROM TNCSizing data WHERE " +
            "landingId = :landingId AND " +
            "UPPER(postStatus) = UPPER(:postStatus) AND " +
            "fishId != :fishId ")
    long tryingCountByLandingIdAndPostStatusAndFishIdNot(@Param("landingId") Long landingId,
                                                         @Param("postStatus")  String postStatus,
                                                         @Param("fishId") Long fishId);

}
