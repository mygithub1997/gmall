package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author wangguoquan
 * @email 872448085@qq.com
 * @date 2020-03-04 20:44:37
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);
        
        Boolean checkData(String data, Integer type);
    
    void register(MemberEntity memberEntity, String code);
    
    MemberEntity queryUser(String username, String password);
}

