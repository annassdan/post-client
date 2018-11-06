package com.project.ifish.postclient.models.attnc;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.configurations.CustomDateTimeSerializer;
import com.project.ifish.postclient.utils.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
@Table(name = "ifish_boat")
public class TNCBoat implements PostClient {

    @Id
    @Column(name = "oid")
    @NotNull
    private Long oid;

    @Column(name = "program_site", length = 128)
    @ColumnDefault("''")
    private String programSite;

    @Column(name = "program_type", length = 32)
    @ColumnDefault("''")
    private String programType;

    @Column(name = "picture_original")
    private Long pictureOriginal;

    @Column(name = "picture_censored")
    private Long pictureCensored;

    @Column(name = "boat_code", length = 24)
    @ColumnDefault("''")
    private String boatCode;

    @Column(name = "boat_name", length = 64)
    @ColumnDefault("''")
    private String boatName;

    @Column(name = "fishing_gear", length = 32)
    @ColumnDefault("''")
    private String fishingGear;

    @Column(name = "codrs_contract")
    @ColumnDefault("0")
    private Integer codrsContract;

    @Column(name = "captain", length = 64)
    @ColumnDefault("''")
    private String captain;

    @Column(name = "captain_origin", columnDefinition = "TEXT")
    private String captainOrigin;

    @Column(name = "owner", length = 64)
    @ColumnDefault("''")
    private String owner;

    @Column(name = "owner_origin", columnDefinition = "TEXT")
    private String ownerOrigin;

    @Column(name = "owner_district", columnDefinition = "TEXT")
    private String ownerDistrict;

    @Column(name = "owner_province", columnDefinition = "TEXT")
    private String ownerProvince;

    @Column(name = "registration_port", length = 64)
    @ColumnDefault("''")
    private String registrationPort;

    @Column(name = "year_built")
    @ColumnDefault("0")
    private Integer yearBuilt;

    @Column(name = "length_of_boat", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double lengthOfBoat;

    @Column(name = "width_of_boat", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double widthOfBoat;

    @Column(name = "height_of_boat", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double heightOfBoat;

    @Column(name = "capacity_palka_m3", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double capacityPalkaM3;

    @Column(name = "gt_estimate", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double gtEstimate;

    @Column(name = "gt_declared", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double gtDeclared;

    @Column(name = "size_category", length = 32)
    @ColumnDefault("''")
    private String sizeCategory;

    @Column(name = "engine_spec", columnDefinition = "TEXT default ''")
    private String engineSpec;

    @Column(name = "number_of_engine")
    @ColumnDefault("0")
    private Integer numberOfEngine;

    @Column(name = "number_of_crew")
    @ColumnDefault("0")
    private Integer numberOfCrew;

    @Column(name = "wpp_permit1", length = 32)
    @ColumnDefault("''")
    private String wppPermit1;

    @Column(name = "wpp_permit2", length = 32)
    @ColumnDefault("''")
    private String wppPermit2;

    @Column(name = "wpp_permit3", length = 32)
    @ColumnDefault("''")
    private String wppPermit3;

    @Column(name = "landing_port1", columnDefinition = "TEXT default ''")
    private String landingPort1;

    @Column(name = "landingPort2", columnDefinition = "TEXT default ''")
    private String landingPort2;

    @Column(name = "trans_destination1", columnDefinition = "TEXT default ''")
    private String transDestination1;

    @Column(name = "trans_destination2", columnDefinition = "TEXT default ''")
    private String transDestination2;

    @Column(name = "company1", columnDefinition = "TEXT default ''")
    private String company1;

    @Column(name = "company2", columnDefinition = "TEXT default ''")
    private String company2;

    @Column(name = "company3", columnDefinition = "TEXT default ''")
    private String company3;

    @Column(name = "fishing_area1", length = 32)
    @ColumnDefault("''")
    private String fishingArea1;

    @Column(name = "fishing_area2", length = 32)
    @ColumnDefault("''")
    private String fishingArea2;

    @Column(name = "fishing_area3", length = 32)
    @ColumnDefault("''")
    private String fishingArea3;

    @Column(name = "avg_trip_per_year")
    @ColumnDefault("0")
    private Integer avgTripPerYear;

    @Column(name = "avg_catch_per_trip_kg", columnDefinition = "Decimal(6,2) default '0.00'")
    private Double avgCatchPerTripKg;

    @Column(name = "avg_catch_per_year_kg", columnDefinition = "Decimal(12,2) default '0.00'")
    private Double avgCatchPerYearKg;

    @Column(name = "uuid", columnDefinition = "TEXT")
    private String uuid;

    @Column(name = "counter")
    @ColumnDefault("0")
    private Integer counter;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN_MINIMAL)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Column(name = "codrs_start_date")
    private Date codrsStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN_MINIMAL)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Column(name = "codrs_end_date")
    private Date codrsEndDate;

    @Column(name = "engine_hp1")
    private Integer engineHp1;

    @Column(name = "engine_hp2")
    private Integer engineHp2;

    @Column(name = "engine_hp3")
    private Integer engineHp3;

    @Column(name = "category", length = 32)
    private String category;

    @Column(name = "fishing_gear_description", columnDefinition = "TEXT default ''")
    private String fishingGearDescription;

    @Column(name = "post_status", length = 50)
    @ColumnDefault("'DRAFT'")
    private String postStatus;


}
