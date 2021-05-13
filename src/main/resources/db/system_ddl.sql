SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `system_permission_info`
-- ----------------------------
DROP TABLE IF EXISTS `system_permission_info`;
CREATE TABLE `system_permission_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) DEFAULT NULL COMMENT '名称 字典添加',
  `identifier` varchar(32) DEFAULT NULL COMMENT '标示符 dictionary:save',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Table structure for `system_role_info`
-- ----------------------------
DROP TABLE IF EXISTS `system_role_info`;
CREATE TABLE `system_role_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(32) DEFAULT NULL COMMENT '编码 ROLE_ADMIN',
  `name` varchar(32) DEFAULT NULL COMMENT '名称 超级管理员',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for `system_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `system_role_permission`;
CREATE TABLE `system_role_permission` (
  `role_id` int(11) NOT NULL COMMENT '角色外键',
  `permission_id` int(11) NOT NULL COMMENT '权限外键',
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限关系表';

-- ----------------------------
-- Table structure for `system_user_info`
-- ----------------------------
DROP TABLE IF EXISTS `system_user_info`;
CREATE TABLE `system_user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `pass` varchar(1024) DEFAULT NULL COMMENT '密码',
  `nick` varchar(32) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `locked` int(11) DEFAULT NULL COMMENT '锁定 0:未锁定1:已锁定',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COMMENT='用户表 ';

-- ----------------------------
-- Table structure for `system_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role` (
  `user_id` int(11) NOT NULL COMMENT '用户外键',
  `role_id` int(11) NOT NULL COMMENT '角色外键',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关系表';
