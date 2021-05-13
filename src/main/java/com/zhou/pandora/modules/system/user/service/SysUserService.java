package com.zhou.pandora.modules.system.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhou.pandora.modules.system.user.dao.SysUserDao;
import com.zhou.pandora.modules.system.user.entity.SysUser;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Program: pandora
 * @Created: 2021/5/12 11:17
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@Service
public class SysUserService extends ServiceImpl<SysUserDao, SysUser> implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("name", username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        if(ObjectUtils.isEmpty(sysUser))
        {
            throw new UsernameNotFoundException("无法找到该用户");
        }
        else if (sysUser.getLocked() == 0)
        {
            throw new LockedException("该账号已被锁定");
        }
        //封装用户角色权限
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        baseMapper.queryRoleByName(username).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        baseMapper.queryPermissionByName(username).forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission));
        });
        return new User(username, sysUser.getPass(), authorities);
    }

}
