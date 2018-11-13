package com.project.ifish.postclient.models.atbrpl;


import com.project.ifish.postclient.PostClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class BRPLBoat implements PostClient {

    private String uuid;
    private String dibuatPadaTanggal; // Date
    private String terakhirDiubahPadaTanggal; // Date
    private String dibuatAtauTerakhirDiubahOleh;

    private String programSite;
    private String programType;
    private Long pictureOriginal;
    private Long pictureCensored;
    private String boatCode;
    private String boatName;
    private String fishingGear;
    private Integer codrsContract;
    private String captain;
    private String captainOrigin;
    private String owner;
    private String ownerOrigin;
    private String ownerDistrict;
    private String ownerProvince;
    private String registrationPort;
    private Integer yearBuilt;
    private Double lengthOfBoat;
    private Double widthOfBoat;
    private Double heightOfBoat;
    private Double capacityPalkaM3;
    private Double gtEstimate;
    private Double gtDeclared;
    private String sizeCategory;
    private String engineSpec;
    private Integer numberOfEngine;
    private Integer numberOfCrew;
    private String wppPermit1;
    private String wppPermit2;
    private String wppPermit3;
    private String landingPort1;
    private String landingPort2;
    private String transDestination1;
    private String transDestination2;
    private String company1;
    private String company2;
    private String company3;
    private String fishingArea1;
    private String fishingArea2;
    private String fishingArea3;
    private Integer avgTripPerYear;
    private Double avgCatchPerTripKg;
    private Double avgCatchPerYearKg;
    private String uuidData; // di kolom tabel ifish adalah "uuid"
    private Integer counter;

//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonSerialize(using = CustomDateSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN_MINIMAL)
    private String codrsStartDate; // Date

//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonSerialize(using = CustomDateSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN_MINIMAL)
    private String codrsEndDate; // Date
    private Integer engineHp1;
    private Integer engineHp2;
    private Integer engineHp3;

}
