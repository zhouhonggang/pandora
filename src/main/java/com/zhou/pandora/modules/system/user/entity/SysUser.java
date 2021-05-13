package com.zhou.pandora.modules.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhou.pandora.component.basic.entity.Basic;
import lombok.Getter;
import lombok.Setter;

/**
 * @Program: pandora
 * @Created: 2021/5/12 11:15
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@Getter
@Setter
@TableName(value = "system_user_info")
public class SysUser extends Basic {
    private String name;
    private String pass;
    private String nick;
    private String phone;
    private String email;
    private int locked;
}
