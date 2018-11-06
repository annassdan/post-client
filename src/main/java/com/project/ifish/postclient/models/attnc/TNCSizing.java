package com.project.ifish.postclient.models.attnc;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.configurations.CustomDateSerializer;
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
@Table(name = "ifish_sizing")
public class TNCSizing implements PostClient {

    @Id
    @Column(name = "oid")
    @NotNull
    private Long oid;

    @Column(name = "fish_id")
    private Long fishId;

    @Column(name = "cm", columnDefinition = "Decimal(10,2) default '0.00'")
    private Double cm;

    @Column(name = "landing_id")
    private Long landingId;

    @Column(name = "offloading_id")
    private Long offloadingId;

    @Column(name = "data_quality")
    @ColumnDefault("1")
    private Integer dataQuality;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Column(name = "codrs_picture_date")
    private Date codrsPictureDate;

    @Column(name = "codrs_picture_name", length = 128)
    @ColumnDefault("''")
    private String codrsPictureName;

    @Column(name = "length_type", length = 32)
    @ColumnDefault("''")
    private String lengthType;

    @Column(name = "post_status", length = 50)
    @ColumnDefault("'DRAFT'")
    private String postStatus;

}
