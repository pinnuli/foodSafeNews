/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : crawl_news

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2018-06-09 21:02:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for food_safe_articles
-- ----------------------------
DROP TABLE IF EXISTS `food_safe_articles`;
CREATE TABLE `food_safe_articles` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `title` varchar(255) DEFAULT NULL COMMENT '文章标题',
  `content` text COMMENT '文章内容',
  `category` varchar(100) DEFAULT NULL COMMENT '新闻栏目',
  `src_from` varchar(100) DEFAULT NULL COMMENT '文章来源',
  `article_url` varchar(255) DEFAULT NULL COMMENT '新闻文章url',
  `pub_time` varchar(30) DEFAULT NULL COMMENT '新闻发布时间',
  `is_safe` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `deleted_at` tinyint(4) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=31306 DEFAULT CHARSET=utf8;
