package com.project.ifish.postclient.repositories;

import com.project.ifish.postclient.models.attnc.TNCSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

@SuppressWarnings("unused")
public interface TNCSpeciesRepo
        extends PagingAndSortingRepository<TNCSpecies, Long>, JpaSpecificationExecutor<TNCSpecies> {

    Page<TNCSpecies> findAllByPostStatus(Pageable pageable, String postStatus);

    long countByPostStatus(String postStatus);

}
