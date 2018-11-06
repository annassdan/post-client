package com.project.ifish.postclient.rests;

import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import com.project.ifish.postclient.services.TNCDeepslopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/deepslope",
        produces = "application/json")
@SuppressWarnings("unused")
public class TNCDeepslopeRest {

    @Autowired
    TNCDeepslopeService tncDeepslopeService;

    @GetMapping(params = {"page", "size"})
    private ResponseEntity<?> serveToGetAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return new ResponseEntity<>(tncDeepslopeService.getAll(page, size), HttpStatus.OK);
    }



}
