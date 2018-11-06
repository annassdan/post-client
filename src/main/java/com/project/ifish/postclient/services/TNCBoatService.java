package com.project.ifish.postclient.services;

import com.project.ifish.postclient.models.attnc.TNCBoat;
import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import com.project.ifish.postclient.repositories.TNCBoatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class TNCBoatService {

    @Autowired
    private TNCBoatRepo tncBoatRepo;

    public TNCBoat save(TNCBoat boat) {
        return tncBoatRepo.save(boat);
    }

    public List<TNCBoat> getAll(Pageable pageable) {
        return tncBoatRepo.findAll(pageable).getContent();
    }

    public List<TNCBoat> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncBoatRepo.findAll(pageable).getContent();
    }

    public List<TNCBoat> getAllByPostStatus(Pageable pageable, String status) {
        return tncBoatRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public List<TNCBoat> getAllByPostStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncBoatRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public long countAll() {
        return tncBoatRepo.count();
    }

    public long countAllByPostStatus(String status) {
        return tncBoatRepo.countByPostStatus(status);
    }

    public TNCBoat getOne(Long id) {
        Optional<TNCBoat> boat = tncBoatRepo.findById(id);
        return boat.orElse(null);
    }

}
