package com.project.ifish.postclient.models.atbrpl;

import com.project.ifish.postclient.utils.ObjectManyToOne;
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
    private String dibuatPadaTanggal; // date
    private String terakhirDiubahPadaTanggal; // date
    private String dibuatAtauTerakhirDiubahOleh;

    private ObjectManyToOne dataSpecies; // BRPLSpecies
    private Long offloadingId;
    private Integer dataQuality;
    private Double cm;
    private String codrsPictureDate; // date
    private String codrsPictureName;
    private String lengthType;

}
