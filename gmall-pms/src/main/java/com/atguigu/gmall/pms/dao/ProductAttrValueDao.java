package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * spu属性值
 * 
 * @author wangguoquan
 * @email 872448085@qq.com
 * @date 2020-02-18 19:57:56
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {
        
        List<ProductAttrValueEntity> queryAttrValueBySpuId(Long spuId);
}
