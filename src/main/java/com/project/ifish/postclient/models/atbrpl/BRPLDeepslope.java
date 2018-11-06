package com.project.ifish.postclient.models.atbrpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class BRPLDeepslope {

    private String uuid;
    private Date dibuatPadaTanggal;
    private Date terakhirDiubahPadaTanggal;
    private String dibuatAtauTerakhirDiubahOleh;

    private Integer approach;
    private Date landingDate;
    private String landingLocation;
    private String wpp1;
    private String wpp2;
    private String wpp3;
    private BRPLBoat dataBoat;
    private String fishingGear;
    private String broughtBy;
    private String otherFishingGround;
    private String supplier;
    private String fisheryType;
    private Date entryDate;
    private Date postingDate;
    private String postingUser;
    private String docStatus;
    private Date firstCodrsPictureDate;
    private String dataStatus;
    private String notes;
    private String fishingLedger;
    private Integer totalCatch;
    List<BRPLSizing> dataSizing;

}
