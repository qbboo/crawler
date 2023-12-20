# 一个学习的爬虫项目
## 简介
这是一个出于学习目的的爬虫实战项目，目的是提升自身的编程水平。
## 知识点
- git
- circleCI
- PR
- test
- SpotBugs
## 思路
### 需求分析
![](https://cdn.jsdelivr.net/gh/qbboo/picture@main/uPic/2023_12_18_57UFqpZouPl4.png)
## 用法
用这个启动爬虫项目 `Crawler.start("https://portal.sina.com.hk/")`，每次爬取停3s防止封ip。
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
## 数据库
### 表设计
table `link_pool`

|名| 类型        | 不是null             | 键  | 默认值   | 自增长                |
|-|-----------|--------------------|----|-------|--------------------|
|id| bitint    | :heavy_check_mark: | 主键 |       | :heavy_check_mark: |
|link| text      | :heavy_check_mark: |    |       |                    |
|created_at| timestamp |                    |    | Now() |                    |
|updated_at| timestamp |                    |    | Now() |                    |

table `filter_pool`

|名| 类型        | 不是null             | 键  | 默认值   | 自增长                |
|-|-----------|--------------------|----|-------|--------------------|
|id| bitint    | :heavy_check_mark: | 主键 |       | :heavy_check_mark: |
|link| text      | :heavy_check_mark: |    |       |                    |
|created_at| timestamp |                    |    | Now() |                    |
|updated_at| timestamp |                    |    | Now() |                    |

table `news`

|名|类型| 不是null             | 键  | 默认值   | 自增长                |
|-|-|--------------------|----|-------|--------------------|
|id|bitint| :heavy_check_mark: | 主键 |       | :heavy_check_mark: |
|title|text| :heavy_check_mark: |    |       |                    |
|content|text| :heavy_check_mark: |    |       |                    |
|link|text| :heavy_check_mark: |    |       |                    |
|created_at|timestamp|                    |    | Now() |                    |
|updated_at|timestamp|                    |    | Now() |                    |


## 参考文章
- [how-to-write-better-git-commit-messages](https://www.freecodecamp.org/news/how-to-write-better-git-commit-messages/)
- [SpotBugs Security: High level issues](https://docs.embold.io/spotbugs-security-high-level-issues/)