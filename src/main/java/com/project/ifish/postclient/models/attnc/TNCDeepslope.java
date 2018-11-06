package com.project.ifish.postclient.models.attnc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.configurations.CustomDateSerializer;
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
@Builder@SuppressWarnings("unused")
@Table(name = "ifish_deepslope")
public class TNCDeepslope implements PostClient {


    @Id
    @Column(name = "oid")
    @NotNull
    private Long oid;

    @Column(name = "approach")
    private Integer approach;

    @Column(name = "user_id")
    @ColumnDefault("'0'")
    private Long userId;

    @Column(name = "partner_id")
    @ColumnDefault("'0'")
    private long partnerId;

    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @Column(name = "landing_date")
    private Date landingDate;

    @Column(name = "landing_location", length = 128)
    private String landingLocation;

    @Column(name = "wpp1", length = 8)
    @ColumnDefault("''")
    private String wpp1;

    @Column(name = "wpp2", length = 8)
    @ColumnDefault("''")
    private String wpp2;

    @Column(name = "wpp3", length = 8)
    @ColumnDefault("''")
    private String wpp3;

    @Column(name = "boat_id")
    private Long boatId;

    @Column(name = "fishing_gear", length = 128)
    @ColumnDefault("''")
    private String fishingGear;

    @Column(name = "brought_by", length = 128)
    @ColumnDefault("''")
    private String broughtBy;

    @Column(name = "other_fishing_ground", length = 128)
    @ColumnDefault("''")
    private String otherFishingGround;

    @Column(name = "supplier", length = 128)
    @ColumnDefault("''")
    private String supplier;

    @Column(name = "fishery_type", length = 32)
    @ColumnDefault("''")
    private String fisheryType;

    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @Column(name = "entry_date")
    private Date entryDate;

    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @Column(name = "posting_date")
    private Date postingDate;

    @Column(name = "posting_user")
    @ColumnDefault("0")
    private Long postingUser;

    @Column(name = "doc_status", length = 32)
    @ColumnDefault("''")
    private String docStatus;

    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @Column(name = "first_codrs_picture_date")
    private Date firstCodrsPictureDate;

    @Column(name = "data_status", length = 32)
    @ColumnDefault("''")
    private String dataStatus;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "fishing_ledger")
    @ColumnDefault("0")
    private Long fishingLedger;

    @Column(name = "total_catch")
    @ColumnDefault("0")
    private Integer totalCatch;

    @Column(name = "post_status", length = 50)
    @ColumnDefault("'DRAFT'")
    private String postStatus;

}
