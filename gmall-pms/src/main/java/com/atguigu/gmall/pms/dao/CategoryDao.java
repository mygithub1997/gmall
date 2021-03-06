package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author wangguoquan
 * @email 872448085@qq.com
 * @date 2020-02-18 19:57:55
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
        
        List<CategoryVO> queryCategoryWithSubByPid(Long pid);
}
