package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.ums.exception.MemberException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
        
        @Override
        public PageVo queryPage(QueryCondition params) {
                IPage<MemberEntity> page = this.page(
                        new Query<MemberEntity>().getPage(params),
                        new QueryWrapper<MemberEntity>()
                );
                
                return new PageVo(page);
        }
        
        //数据校验
        @Override
        public Boolean checkData(String data, Integer type) {
                
                QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
                switch (type) {
                        case 1:
                                wrapper.eq("username", data);
                                break;
                        case 2:
                                wrapper.eq("mobile", data);
                                break;
                        case 3:
                                wrapper.eq("email", data);
                                break;
                        default:
                                return null;
                }
                return this.count(wrapper) == 0;
        }
        
        //注册用户
        @Override
        public void register(MemberEntity memberEntity, String code) {
                //1、校验验证码
                
                //2、生成盐
                String salt = UUID.randomUUID().toString().substring(0, 5);
                memberEntity.setSalt(salt);
                
                //3、加盐加密
                memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));
                
                //4、新增用户
                memberEntity.setCreateTime(new Date());
                memberEntity.setIntegration(1000);
                memberEntity.setGrowth(1000);
                memberEntity.setStatus(1);
                memberEntity.setLevelId(0l);
                this.save(memberEntity);
                //5、删除redis中的验证码
                
        }
        
        //查询用户
        @Override
        public MemberEntity queryUser(String username, String password) {
                //1、先根据用户名查询
                MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        
                //2、如果为空，则用户名不存在
                if(memberEntity == null){
                        throw new MemberException("用户名输入有误！");
                }
                
                //3、获取用户的盐
                password = DigestUtils.md5Hex(password + memberEntity.getSalt());
                
                //4、比较加盐加密的密码是否一致
                if(!StringUtils.equals(password,memberEntity.getPassword())){
                        throw new MemberException("密码输入有误！");
                }
                return memberEntity;
        }
        
        
        
}





















