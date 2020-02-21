package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuInfoVO  extends SpuInfoEntity {
        private List<?> spuImages;
        
        private List<?> baseAttrs;
        
        private List<?> skus;
}
