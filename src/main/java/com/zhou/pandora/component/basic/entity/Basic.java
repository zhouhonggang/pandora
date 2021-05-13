package com.zhou.pandora.component.basic.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @Program: pandora
 * @Created: 2021/5/12 11:16
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@Getter
@Setter
public class Basic {
    /**
     * 主键
     */
    @TableId(type= IdType.AUTO)
    private Integer id;
    /**
     * 乐观锁(在并发操作时使用)
     */
    @TableField(update = "%s+1")
    private Integer revision = 1;
    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createdBy;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createdTime;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updatedBy;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedTime;
    /**
     * offset: 起始页
     * size: 每页查询条数
     * 不参与数据库相关操作只负责分页查询相关功能
     */
    @TableField(exist = false)
    private int current = 1;
    @TableField(exist = false)
    private int size = 10;
}
