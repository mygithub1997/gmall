package com.atguigu.gmall.pms.service.impl;

import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
        
        @Override
        public PageVo querySpuByCidPage(QueryCondition condition, Long cid) {
                QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
                
                //关键字判断
                String key = condition.getKey();
                if (StringUtils.isNullOrEmpty(key)) {
                        wrapper.eq("id", key).or().like("spu_name", key);
                }
                
                //分类id判断
                if (cid != null) {
                        wrapper.eq("catalog_id", cid);
                }
                
                IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(condition),wrapper);
                return new PageVo(page);
        }
        
        @Override
        public PageVo queryPage(QueryCondition params) {
                IPage<SpuInfoEntity> page = this.page(
                        new Query<SpuInfoEntity>().getPage(params),
                        new QueryWrapper<SpuInfoEntity>()
                );
                
                return new PageVo(page);
        }
        
}