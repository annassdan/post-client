package com.project.ifish.postclient.services;

import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import com.project.ifish.postclient.repositories.TNCDeepslopeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class TNCDeepslopeService {

    @Autowired
    private TNCDeepslopeRepo tncDeepslopeRepo;

    public List<TNCDeepslope> getAll(Pageable pageable) {
        return tncDeepslopeRepo.findAll(pageable).getContent();
    }

    public List<TNCDeepslope> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncDeepslopeRepo.findAll(pageable).getContent();
    }

    public List<TNCDeepslope> getAllByPostStatus(Pageable pageable, String status) {
        return tncDeepslopeRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public List<TNCDeepslope> getAllByPostStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncDeepslopeRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public long countAll() {
        return tncDeepslopeRepo.count();
    }

    public long countAllByPostStatus(String status) {
        return tncDeepslopeRepo.countByPostStatus(status);
    }

    public TNCDeepslope getOne(Long id) {
        Optional<TNCDeepslope> deepslope = tncDeepslopeRepo.findById(id);
        return deepslope.orElse(null);
    }


}
