package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author wangguoquan
 * @email 872448085@qq.com
 * @date 2020-02-18 21:24:17
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
