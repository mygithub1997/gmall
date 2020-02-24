package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.vo.BaseAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
        
        @Autowired
        private SpuInfoDescDao descDao;
        
        @Autowired
        private ProductAttrValueDao baseAttrDao;
        
        @Autowired
        private SkuInfoDao skuInfoDao;
        
        @Autowired
        private SkuImagesDao imagesDao;
        
        @Autowired
        private SkuSaleAttrValueDao saleAttrDao;
        
        @Autowired
        private GmallSmsClient smsClient;
        
        @Override
        public PageVo querySpuByCidPage(QueryCondition condition, Long cid) {
                QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
                
                //关键字判断
                String key = condition.getKey();
                if (StringUtils.isEmpty(key)) {
                        wrapper.eq("id", key).or().like("spu_name", key);
                }
                
                //分类id判断
                if (cid != null) {
                        wrapper.eq("catalog_id", cid);
                }
                
                IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(condition), wrapper);
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
        
        @GlobalTransactional
        @Override
        public void bigSave(SpuInfoVO spuInfoVO) {
                //保存spu相关的信息(spuInfo spuInfoDesc productAttrValue)
                //1.1保存spuInfo信息
                spuInfoVO.setCreateTime(new Date());
                spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
                this.save(spuInfoVO);
                Long spuId = spuInfoVO.getId();
                //int i = 1/0;
                //1.2 保存spuInfoDesc的信息
                List<String> spuImages = spuInfoVO.getSpuImages();
                if (!CollectionUtils.isEmpty(spuImages)) {
                        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
                        descEntity.setSpuId(spuId);
                        descEntity.setDecript(StringUtils.join(spuImages, ","));
                        this.descDao.insert(descEntity);
                }
                
                //1.3保存基本属性(productAttrValue)
                List<BaseAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
                if (!CollectionUtils.isEmpty(baseAttrs)) {
                        baseAttrs.forEach(baseAttrValueVO -> {
                                baseAttrValueVO.setSpuId(spuId);
                                this.baseAttrDao.insert(baseAttrValueVO);
                        });
                        
                }
                
                //2.保存sku相关的信息(需要spuId)
                List<SkuInfoVO> skus = spuInfoVO.getSkus();
                if (CollectionUtils.isEmpty(skus)) {
                        return;
                }
                skus.forEach(skuInfoVO -> {
                        //2.1 保存skuInfo
                        skuInfoVO.setSpuId(spuId);
                        skuInfoVO.setSkuCode(UUID.randomUUID().toString());
                        skuInfoVO.setCatalogId(skuInfoVO.getCatalogId());
                        skuInfoVO.setBrandId(spuInfoVO.getBrandId());
                        //设置默认图片，如果页面传了默认图片，使用页面传的图片，否则取第一张图片作为默认图片
                        List<String> images = skuInfoVO.getImages();
                        if (!CollectionUtils.isEmpty(images)) {
                                skuInfoVO.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoVO.getSkuDefaultImg()) ? skuInfoVO.getSkuDefaultImg() : images.get(0));
                        }
                        this.skuInfoDao.insert(skuInfoVO);
                        Long skuId = skuInfoVO.getSkuId();
                        
                        //2.2 保存sku图片信息skuImages
                        if (!CollectionUtils.isEmpty(images)) {
                                images.forEach(image -> {
                                        SkuImagesEntity imagesEntity = new SkuImagesEntity();
                                        imagesEntity.setImgUrl(image);
                                        imagesEntity.setSkuId(skuId);
                                        if (StringUtils.equals(image, skuInfoVO.getSkuDefaultImg())) {
                                                imagesEntity.setDefaultImg(1);
                                        }else{
                                                imagesEntity.setDefaultImg(0);
                                        }
                                        imagesDao.insert(imagesEntity);
                                });
                        }
                //2.3 保存销售属性skuSaleAttr
                        List<SkuSaleAttrValueEntity> saleAttr =skuInfoVO.getSaleAttrs();
                        if(!CollectionUtils.isEmpty(saleAttr)){
                                saleAttr.forEach(skuSaleAttrValueEntity -> {
                                        skuSaleAttrValueEntity.setSkuId(skuId);
                                        this.saleAttrDao.insert(skuSaleAttrValueEntity);
                                });
                        }
                        
                        //3 保存sku营销相关信息(需要skuId)
                        SkuSaleVO skuSaleVO = new SkuSaleVO();
                        BeanUtils.copyProperties(skuInfoVO,skuSaleVO);
                        this.smsClient.saveSkuSales(skuSaleVO);
                });
            
        
        }
}