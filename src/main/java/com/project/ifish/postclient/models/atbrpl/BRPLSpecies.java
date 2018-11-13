package com.project.ifish.postclient.models.atbrpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class BRPLSpecies {

    private String uuid;
    private String dibuatPadaTanggal; // Date
    private String terakhirDiubahPadaTanggal; // Date
    private String dibuatAtauTerakhirDiubahOleh;

    private Double largestSpecimenCm;
    private boolean family;
    private String fishPhylum;
    private String fishClass;
    private String fishOrder;
    private String fishFamily;
    private String fishGenus;
    private String fishSpecies;
    private String namaUmum;
    private String namaIndonesia;
    private String namaLatin;
    private Integer lmat;
    private Integer lopt;
    private Integer linf;
    private Integer lmax;
    private Integer lmatm;
    private Integer speciesIdNumber;
    private Double reportedTradeLimitWeight;
    private Double varA;
    private Double varB;
    private String lengthBasis;
    private Double convertedTradeLimitL;
    private Double plottedTradeLimitTl;
    private Double conversionFactorTl2fl;


}
