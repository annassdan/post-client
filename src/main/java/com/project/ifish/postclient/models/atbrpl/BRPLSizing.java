package com.project.ifish.postclient.models.atbrpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class BRPLSizing {

    private String uuid;
    private Date dibuatPadaTanggal;
    private Date terakhirDiubahPadaTanggal;
    private String dibuatAtauTerakhirDiubahOleh;

    private BRPLSpecies dataSpecies;
    private Double cm;
    private Date codrsPictureDate;
    private String codrsPictureName;
    private String lengthType;

}
