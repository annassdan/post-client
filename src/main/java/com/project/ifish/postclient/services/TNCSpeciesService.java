package com.project.ifish.postclient.services;

import com.project.ifish.postclient.models.attnc.TNCSpecies;
import com.project.ifish.postclient.repositories.TNCSpeciesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class TNCSpeciesService {

    @Autowired
    private TNCSpeciesRepo tncSpeciesRepo;


    public TNCSpecies save(TNCSpecies species) {
        return tncSpeciesRepo.save(species);
    }


    public List<TNCSpecies> getAll(Pageable pageable) {
        return tncSpeciesRepo.findAll(pageable).getContent();
    }

    public List<TNCSpecies> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncSpeciesRepo.findAll(pageable).getContent();
    }


    public List<TNCSpecies> getAllByPostStatus(Pageable pageable, String status) {
        return tncSpeciesRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public List<TNCSpecies> getAllByPostStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tncSpeciesRepo.findAllByPostStatus(pageable, status).getContent();
    }

    public long countAll() {
        return tncSpeciesRepo.count();
    }

    public long countAllByPostStatus(String status) {
        return tncSpeciesRepo.countByPostStatus(status);
    }

    public TNCSpecies getOne(Long id) {
        Optional<TNCSpecies> species = tncSpeciesRepo.findById(id);
        return species.orElse(null);
    }

}
