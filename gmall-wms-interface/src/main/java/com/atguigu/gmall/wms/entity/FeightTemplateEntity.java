package com.atguigu.gmall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运费模板
 * 
 * @author wangguoquan
 * @email 872448085@qq.com
 * @date 2020-02-21 22:22:41
 */
@ApiModel
@Data
@TableName("wms_feight_template")
public class FeightTemplateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty(name = "id",value = "id")
	private Long id;
	/**
	 * name
	 */
	@ApiModelProperty(name = "name",value = "name")
	private String name;
	/**
	 * 计费类型【0->按重量，1->按件数】
	 */
	@ApiModelProperty(name = "chargeType",value = "计费类型【0->按重量，1->按件数】")
	private Integer chargeType;
	/**
	 * 首重
	 */
	@ApiModelProperty(name = "firstWeight",value = "首重")
	private BigDecimal firstWeight;
	/**
	 * 首费
	 */
	@ApiModelProperty(name = "firstFee",value = "首费")
	private BigDecimal firstFee;
	/**
	 * 续重
	 */
	@ApiModelProperty(name = "continueWeight",value = "续重")
	private BigDecimal continueWeight;
	/**
	 * 续费
	 */
	@ApiModelProperty(name = "continueFee",value = "续费")
	private BigDecimal continueFee;
	/**
	 * 目的地
	 */
	@ApiModelProperty(name = "dest",value = "目的地")
	private Long dest;

}