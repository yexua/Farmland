package com.harvest.oms.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单地址信息 ｜ 地址信息只有特定环节会变更，大部分信息流转是固定的，因此单独分拆独立表；部分平台履约场景，可能获取不到地址信息；
 * </p>
 *
 * @author Alodi
 * @since 2022-12-19 13:54:52
 */
@Getter
@Setter
@TableName("farmland_oms_order_address")
@ApiModel(value = "FarmlandOmsOrderAddressEntity对象", description = "订单地址信息 ｜ 地址信息只有特定环节会变更，大部分信息流转是固定的，因此单独分拆独立表；部分平台履约场景，可能获取不到地址信息；")
public class FarmlandOmsOrderAddressEntity {

    @TableId("order_id")
    private Long orderId;

    @ApiModelProperty("公司id")
    @TableField("COMPANY_ID")
    private Long companyId;

    @ApiModelProperty("地址类型")
    @TableField("address_type")
    private Integer addressType;

    @ApiModelProperty("国家二字码")
    @TableField("country_code")
    private String countryCode;

    @ApiModelProperty("国家中文名称")
    @TableField("country_cn")
    private String countryCn;

    @ApiModelProperty("邮政编码")
    @TableField("postal_code")
    private String postalCode;

    @ApiModelProperty("省")
    @TableField("region1")
    private String region1;

    @ApiModelProperty("市")
    @TableField("region2")
    private String region2;

    @ApiModelProperty("区/县")
    @TableField("region3")
    private String region3;

    @ApiModelProperty("街道/乡村")
    @TableField("region4")
    private String region4;

    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    @TableField(value = "rc_time", fill = FieldFill.INSERT)
    private LocalDateTime rcTime;

    @TableField(value = "rm_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime rmTime;
}
