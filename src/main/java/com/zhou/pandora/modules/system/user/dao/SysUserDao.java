package com.zhou.pandora.modules.system.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhou.pandora.modules.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Program: pandora
 * @Created: 2021/5/12 11:17
 * @Author: ZhouHongGang
 * @Description: TODO
 */
public interface SysUserDao extends BaseMapper<SysUser> {
    @Select("select r.code from system_role_info r, system_user_role ur where r.id = ur.role_id " +
            " and ur.user_id = (select id from system_user_info where name = #{name})")
    public List<String> queryRoleByName(String name);

    @Select("select distinct identifier from system_permission_info p, system_role_permission rp where p.id = rp.permission_id " +
            " and rp.role_id in (select role_id from system_user_role where " +
            "   user_id = (select id from system_user_info where name = #{name}))")
    public List<String> queryPermissionByName(String name);
}
