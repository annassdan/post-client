package com.project.ifish.postclient.models.attnc;


import com.project.ifish.postclient.utils.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
@Table(name = "ifish_fish")
public class TNCSpecies {

    @Id
    @Column(name = "oid")
    @NotNull
    private Long oid;

    @Column(name = "fish_phylum", length = 64)
    private String fishPhylum;

    @Column(name = "fish_class", length = 64)
    private String fishClass;

    @Column(name = "fish_order", length = 64)
    private String fishOrder;

    @Column(name = "fish_family", length = 64)
    private String fishFamily;

    @Column(name = "fish_genus", length = 64)
    private String fishGenus;

    @Column(name = "fish_species", length = 64)
    private String fishSpecies;

    @Column(name = "common_names", columnDefinition = "TEXT")
    private String commonNames;

    @Column(name = "hawaiian_name", columnDefinition = "TEXT")
    private String hawaiianName;

    @Column(name = "market_fishes_of_indonesia", columnDefinition = "TEXT")
    private String marketFishesOfIndonesia;

    @Column(name = "other_names", columnDefinition = "TEXT")
    private String otherNames;

    @Column(name = "fish_code", length = 8)
    private String fishCode;

    @Column(name = "counter")
    private Integer counter;

    @Column(name = "lmat")
    @ColumnDefault("0")
    private Integer lmat;

    @Column(name = "lopt")
    @ColumnDefault("0")
    private Integer lopt;

    @Column(name = "linf")
    @ColumnDefault("0")
    private Integer linf;

    @Column(name = "lmax")
    @ColumnDefault("0")
    private Integer lmax;

    @Column(name = "lmatm")
    @ColumnDefault("0")
    private Integer lmatm;

    @Column(name = "prefix_code", length = 8)
    private String prefixCode;

    @Column(name = "is_family_id")
    @ColumnDefault("0")
    private Integer isFamilyId;

    @Column(name = "insite_code", length = 32)
    private String insiteCode;

    @Column(name = "species_id_number")
    @ColumnDefault("0")
    private Integer speciesIdNumber;

    @Column(name = "reported_trade_limit_weight")
    @ColumnDefault("0")
    private Double reportedTradeLimitWeight;

    @Column(name = "var_a")
    @ColumnDefault("0")
    private Double varA;

    @Column(name = "var_b")
    @ColumnDefault("0")
    private Double varB;

    @Column(name = "length_basis", length = 4)
    private String lengthBasis;

    @Column(name = "converted_trade_limit_l")
    @ColumnDefault("0")
    private Double convertedTradeLimitL;

    @Column(name = "plotted_trade_limit_tl")
    @ColumnDefault("0")
    private Double plottedTradeLimitTl;

    @Column(name = "default_picture_id")
    @ColumnDefault("0")
    private Long defaultPictureid;

    @Column(name = "conversion_factor_tl2fl")
    @ColumnDefault("0")
    private Double conversionFactorTl2fl;

    @Column(name = "largest_specimen_id")
    @ColumnDefault("0")
    private Long largestSpecimenid;

    @Column(name = "largest_specimen_cm", columnDefinition = "Decimal(10,2) default '0.00'")
    private Double largestSpecimenCm;

    @Column(name = "largest_specimen_picture")
    @ColumnDefault("0")
    private Long largestSpecimenPicture;

    @Column(name = "largest_specimen_catch_area", length = 128)
    @ColumnDefault("''")
    private String largestSpecimenCatchArea;

    @Column(name = "uoa", length = 4)
    private String uoa;

    @Column(name = "post_status", length = 50)
    @ColumnDefault("'DRAFT'")
    private String postStatus;

}
