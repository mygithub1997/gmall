package com.atguigu.gmall.sms.vo.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {
        
        @PostMapping("sms/skubounds")
        public Resp<Object> saveSkuSales(@RequestBody SkuSaleVO skuSaleVO);
        
}
