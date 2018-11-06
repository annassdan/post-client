package com.project.ifish.postclient.models.atbrpl;

import com.project.ifish.postclient.utils.ObjectManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class BRPLDeepslope {

    private String uuid;
    private String dibuatPadaTanggal; // date
    private String terakhirDiubahPadaTanggal; // date
    private String dibuatAtauTerakhirDiubahOleh;

    private Integer approach;
    private String landingDate; // date
    private String landingLocation;
    private String wpp1;
    private String wpp2;
    private String wpp3;
    private ObjectManyToOne dataBoat; // BRPLBoat
    private String fishingGear;
    private String broughtBy;
    private String otherFishingGround;
    private String supplier;
    private String fisheryType;
    private String entryDate; // date
    private String postingDate; // date
    private Long postingUser;
    private String docStatus;
    private String firstCodrsPictureDate; // date
    private String dataStatus;
    private String notes;
    private Long fishingLedger;
    private Integer totalCatch;
    List<BRPLSizing> dataSizing;

}
