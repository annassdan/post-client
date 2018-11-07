package com.project.ifish.postclient.services;

import com.project.ifish.postclient.models.attnc.TNCSizing;
import com.project.ifish.postclient.repositories.TNCSizingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class TNCSizingService {

    @Autowired
    private TNCSizingRepo tncSizingRepo;

    public TNCSizing save(TNCSizing sizing) {
        return tncSizingRepo.save(sizing);
    }

    public boolean saves(List<TNCSizing> sizings) {
        tncSizingRepo.saveAll(sizings);
        return true;
    }


    public List<TNCSizing> getAll(Pageable pageable) {
        return tncSizingRepo.findAll(pageable).getContent();
    }

    public List<TNCSizing> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncSizingRepo.findAll(pageable).getContent();
    }

    public long countAll() {
        return tncSizingRepo.count();
    }

    public List<TNCSizing> getAllByPostStatus(Pageable pageable, String status) {
        return tncSizingRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public List<TNCSizing> getAllByPostStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncSizingRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public long countAllByPostStatus(String status) {
        return tncSizingRepo.countByPostStatus(status);
    }

    //

    public List<TNCSizing> getAllByLandingIdAndPostStatus(Pageable pageable, Long landingId, String status) {
        return tncSizingRepo.findAllByLandingIdAndPostStatus(pageable, landingId, status).getContent();
    }

    public List<TNCSizing> getAllByLandingIdAndPostStatus(Long landingID, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncSizingRepo.findAllByLandingIdAndPostStatus(pageable, landingID, status).getContent();
    }

    public long countAllByLandingIdAndPostStatus(Long landingId, String status) {
        return tncSizingRepo.countByLandingIdAndPostStatus(landingId, status);
    }


    public TNCSizing getOne(Long id) {
        Optional<TNCSizing> sizing = tncSizingRepo.findById(id);
        return sizing.orElse(null);
    }

}
