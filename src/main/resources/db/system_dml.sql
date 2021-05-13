-- ----------------------------
-- Records of system_permission_info
-- ----------------------------
INSERT INTO `system_permission_info` VALUES ('1', '用户注册', 'sys:user:save', null, null, null, null, null);
INSERT INTO `system_permission_info` VALUES ('2', '用户导入', 'sys:user:save:batch', null, null, null, null, null);
INSERT INTO `system_permission_info` VALUES ('3', '用户修改', 'sys:user:update', null, null, null, null, null);
INSERT INTO `system_permission_info` VALUES ('4', '用户删除', 'sys:user:delete', null, null, null, null, null);
INSERT INTO `system_permission_info` VALUES ('5', '用户批删', 'sys:user:delete:batch', null, null, null, null, null);

-- ----------------------------
-- Records of system_role_info
-- ----------------------------
INSERT INTO `system_role_info` VALUES ('1', 'ROLE_ADMIN', '超级管理员', null, null, null, null, null);
INSERT INTO `system_role_info` VALUES ('2', 'ROLE_DBA', '数据库管理员', null, null, null, null, null);
INSERT INTO `system_role_info` VALUES ('3', 'ROLE_GUEST', '临时用户', null, null, null, null, null);

-- ----------------------------
-- Records of system_role_permission
-- ----------------------------
INSERT INTO `system_role_permission` VALUES ('1', '1');
INSERT INTO `system_role_permission` VALUES ('1', '2');
INSERT INTO `system_role_permission` VALUES ('1', '3');
INSERT INTO `system_role_permission` VALUES ('1', '4');
INSERT INTO `system_role_permission` VALUES ('1', '5');
INSERT INTO `system_role_permission` VALUES ('2', '1');
INSERT INTO `system_role_permission` VALUES ('3', '2');

-- ----------------------------
-- Records of system_user_info
-- ----------------------------
INSERT INTO `system_user_info` VALUES ('1', 'admin', '$2a$10$AVeyWaM/tjiV90LQX4SPh.6TklZc8L5h3bR4IvYx67E/4voAiErMy', '小王', '15001390311', '15001390311@javakc.com', '1', '1', '1', '2021-04-15 17:27:50', null, null);
INSERT INTO `system_user_info` VALUES ('2', 'luosk', '$2a$10$MgN/K2ZCXu6Rgkbz/WkFY..FsZ/Kd66I0Oi5bBHD.Z9FSy53k779y', '洛赛克', '15001390311', '15001390311@javakc.com', '0', '1', '1', '2021-04-25 14:45:41', null, null);

-- ----------------------------
-- Records of system_user_role
-- ----------------------------
INSERT INTO `system_user_role` VALUES ('1', '1');
INSERT INTO `system_user_role` VALUES ('1', '3');
