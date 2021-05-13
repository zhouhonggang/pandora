package com.zhou.pandora.modules.system.user.controller;

import com.zhou.pandora.modules.system.user.entity.SysUser;
import com.zhou.pandora.modules.system.user.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Program: pandora
 * @Created: 2021/5/12 17:14
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@RestController
@RequestMapping("sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:get')")
    public List<SysUser> get()
    {
        return sysUserService.query().list();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('sys:user:load')")
    public List<SysUser> get(@PathVariable int id)
    {
        return sysUserService.query().list();
    }

}
