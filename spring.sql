/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : spring

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 21/03/2025 17:49:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for spring_session
-- ----------------------------
DROP TABLE IF EXISTS `spring_session`;
CREATE TABLE `spring_session`  (
  `PRIMARY_ID` char(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `SESSION_ID` char(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`) USING BTREE,
  UNIQUE INDEX `SPRING_SESSION_IX1`(`SESSION_ID` ASC) USING BTREE,
  INDEX `SPRING_SESSION_IX2`(`EXPIRY_TIME` ASC) USING BTREE,
  INDEX `SPRING_SESSION_IX3`(`PRINCIPAL_NAME` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of spring_session
-- ----------------------------
INSERT INTO `spring_session` VALUES ('d7f84f72-2513-41b4-8b94-5463255aca0b', '0eaf1bad-b3bd-4830-95b9-5848f924f888', 1742541120110, 1742550477105, 1800, 1742552277105, NULL);

-- ----------------------------
-- Table structure for spring_session_attributes
-- ----------------------------
DROP TABLE IF EXISTS `spring_session_attributes`;
CREATE TABLE `spring_session_attributes`  (
  `SESSION_PRIMARY_ID` char(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`, `ATTRIBUTE_NAME`) USING BTREE,
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of spring_session_attributes
-- ----------------------------
INSERT INTO `spring_session_attributes` VALUES ('d7f84f72-2513-41b4-8b94-5463255aca0b', 'loginUser', 0xACED000573720012636F6D2E6C6967672E706F6A6F2E5573657200000000000000010200034C000870617373776F72647400124C6A6176612F6C616E672F537472696E673B4C00067573657249647400134C6A6176612F6C616E672F496E74656765723B4C0008757365726E616D6571007E00017870740006323030333039737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000017400046C696767);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `users_user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'ligg', '200309');
INSERT INTO `users` VALUES (2, 'ligg', '123');
INSERT INTO `users` VALUES (3, 'lig11', '123');

-- ----------------------------
-- Table structure for videos
-- ----------------------------
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '视频描述',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频文件路径',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频封面URL',
  `user_id` int NOT NULL COMMENT '上传用户ID',
  `view_count` int NULL DEFAULT 0 COMMENT '观看次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `video_img` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_videos_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of videos
-- ----------------------------
INSERT INTO `videos` VALUES (4, '132131', '1231313131', '/api/video/stream/b547a787-62e2-42c8-82d0-0132a4c3ebfa.mp4', 'default_cover.jpg', 1, 0, '2025-03-21 17:13:27', '2025-03-21 17:13:27', '');
INSERT INTO `videos` VALUES (5, '123', '1231', '/api/video/stream/e5f82780-0621-42a1-abc6-e6f0f50899a8.mp4', 'default_cover.jpg', 1, 0, '2025-03-21 17:40:14', '2025-03-21 17:40:14', NULL);
INSERT INTO `videos` VALUES (6, '1313', '123131', '/api/video/stream/c512d883-0f75-46e6-92a7-efd06e7937d4.mp4', 'default_cover.jpg', 1, 0, '2025-03-21 17:44:14', '2025-03-21 17:44:14', NULL);
INSERT INTO `videos` VALUES (7, '12313', '1321', '/api/video/stream/61d55c51-cad2-48a5-b425-04bb61bfb751.mp4', 'default_cover.jpg', 1, 0, '2025-03-21 17:47:52', '2025-03-21 17:47:52', NULL);

SET FOREIGN_KEY_CHECKS = 1;
