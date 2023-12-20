# 一个学习的爬虫项目
## 简介
这是一个出于学习目的的爬虫实战项目，目的是提升自身的编程水平。
## 知识点
- git
- circleCI
- PR
- test
- SpotBugs
- flywaydb
## 思路
### 需求分析
![](https://cdn.jsdelivr.net/gh/qbboo/picture@main/uPic/2023_12_18_57UFqpZouPl4.png)
## 用法
用这个启动爬虫项目 `Crawler.start()`，每次爬取停3s防止封ip。
使用数据库存储数据，项目中建 `db.config.json` 文件。文件格式如下：
```json
{
  "jdbc": "xxx",
  "user": "xxx",
  "password": "xxx"
}
```
## 功能点
 - [x] 可以断点续爬。
 - [x] 自动创建数据表和初始化数据
## 数据库
### 表设计
文件 `src/main/resources/db/migration/V1__Create_table.sql` 里已创建相关表
### 初始数据
文件 `src/main/resources/db/migration/V1.1__Initialization_data.sql` 插入初始化数据
执行 `mvn flyway:migrate` 会自动创建表和数据

## 参考文章
- [how-to-write-better-git-commit-messages](https://www.freecodecamp.org/news/how-to-write-better-git-commit-messages/)
- [SpotBugs Security: High level issues](https://docs.embold.io/spotbugs-security-high-level-issues/)