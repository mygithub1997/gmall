package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    
    @Autowired
    private AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }
    
    @Override
    public PageVo queryGroupsByCid(QueryCondition queryCondition, Long cid) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id",cid)
        );
    
        return new PageVo(page);
    }
    
    @Override
    public GroupVO queryGroupVOById(Long id) {
        
        GroupVO groupVO = new GroupVO();
        //1、根据id查询分组
        AttrGroupEntity groupEntity = this.getById(id);
        BeanUtils.copyProperties(groupEntity,groupVO);
        
        //2、根据分组id查询中间表
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities =  this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",id));
        //判断这个分组的中间表数据是否为空
        if(CollectionUtils.isEmpty(attrAttrgroupRelationEntities)){
            return groupVO;
        }
        groupVO.setRelations(attrAttrgroupRelationEntities);
        //3、收集中间表中attrIds，查询规格参数
        List<Long> ids = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(ids);
        
        return groupVO;
    }
    
    @Override
    public List<GroupVO> queryGroupWithAttrByCid(Long cid) {
        //根据分类的id查询分组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id",cid));
        if(CollectionUtils.isEmpty(groupEntities)){
            return null;
        }
        
        //根据分组的id查询该组下的规格参数
         return groupEntities.stream().map(attrGroupEntity -> {
            GroupVO groupVO = this.queryGroupVOById(attrGroupEntity.getAttrGroupId());
            return  groupVO;
        }).collect(Collectors.toList());
    }
    
}