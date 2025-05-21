# 微信小程序学习游戏平台

面向初中生的闯关游戏学习平台，通过微信小程序前端，结合SpringBoot后端实现。

## 项目介绍

本项目是一个面向初中生的学习游戏平台，主要功能包括：

1. 用户通过微信小程序登录，验证成功后进入闯关学习界面
2. 支持多学科、多章节、多关卡的学习内容
3. 闯关时需要正确回答题目才能进入下一题，通过当前关卡后才能进入下一关
4. 错题统计功能，可以查看错题分析并进行针对性训练
5. 得分排行榜功能，支持不同难度题目的不同分值计算

## 技术栈

- 后端：SpringBoot 2.5.6 + Spring Security + JWT + JPA
- 数据库：MySQL 5.7+
- 前端：微信小程序

## 项目结构

```
study-game-platform/
├── src/main/java/com/wechat/studygame/
│   ├── config/         # 配置类
│   ├── controller/     # 控制器
│   ├── exception/      # 异常处理
│   ├── model/          # 数据模型
│   │   ├── dto/        # 数据传输对象
│   │   └── entity/     # 实体类
│   ├── repository/     # 数据访问层
│   ├── service/        # 服务层
│   │   └── impl/       # 服务实现
│   └── util/           # 工具类
├── resources/          # 资源文件
│   ├── application.yml # 应用配置
│   └── sql/            # SQL脚本
└── pom.xml             # Maven依赖
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 数据库配置

1. 创建数据库：

```sql
CREATE DATABASE study_game DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 修改 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/study_game?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
    username: root
    password: 123456
```

### 微信小程序配置

在 `application.yml` 中配置微信小程序的 appid 和 secret：

```yaml
wechat:
  appid: your_appid_here
  secret: your_secret_here
```

### 项目启动

1. 编译项目：

```bash
mvn clean package
```

2. 运行项目：

```bash
java -jar target/study-game-platform-0.0.1-SNAPSHOT.jar
```

3. 访问API文档：

```
http://localhost:8080/api/swagger-ui/index.html
```

## API说明

### 1. 用户认证

- 微信登录: `POST /api/auth/wechat-login?code=xxx`

### 2. 学科管理

- 获取所有学科: `GET /api/subjects`
- 获取学科详情: `GET /api/subjects/{id}`

### 3. 章节管理

- 获取学科下的章节: `GET /api/chapters/by-subject/{subjectId}`
- 获取章节详情: `GET /api/chapters/{id}`

### 4. 关卡管理

- 获取章节下的关卡: `GET /api/levels/by-chapter/{chapterId}`
- 获取关卡详情: `GET /api/levels/{id}`

### 5. 题目管理

- 获取关卡下的题目: `GET /api/questions/by-level/{levelId}`
- 获取题目详情: `GET /api/questions/{id}`

### 6. 游戏闯关

- 开始闯关: `POST /api/game/start?levelId=xx&chapterId=xx&subjectId=xx`
- 提交答案: `POST /api/game/submit-answer`
- 完成关卡: `POST /api/game/complete?levelId=xx&timeUsed=xx`

### 7. 错题管理

- 获取错题列表: `GET /api/wrong-questions/by-chapter/{chapterId}`
- 获取错题分析: `GET /api/wrong-questions/{questionId}/analysis`

### 8. 排行榜

- 关卡排行榜: `GET /api/leaderboard/level/{levelId}`
- 章节排行榜: `GET /api/leaderboard/chapter/{chapterId}`
- 全局排行榜: `GET /api/leaderboard/global`

## 前端小程序开发

1. 在微信开发者工具中创建小程序项目
2. 在 `app.js` 中配置后端API地址
3. 实现登录、学科列表、章节列表、关卡列表、闯关界面、错题本、排行榜等页面

## 注意事项

1. 本项目仅为教学演示使用，生产环境中需要进一步加强安全性
2. 微信小程序相关接口需要使用真实的AppID和AppSecret
3. 数据库默认使用用户名root和密码123456，实际部署时请修改为更安全的密码
