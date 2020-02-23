package com.atguigu.gmall.pms.feign;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.SkuSaleVO;
import com.atguigu.gmall.sms.vo.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@EnableFeignClients("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}

