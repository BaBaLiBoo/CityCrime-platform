# 城市犯罪事件管理平台
### 同济大学计算机专业数据库课设项目

基于 Spring Boot + Vue.js 的现代化犯罪事件管理系统。

## 🛠️ 技术架构
### 后端
- **框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **安全**: Spring Security
- **API**: RESTful API 设计
- **JSON处理**: Jackson with Java 17 Records

### 前端
- **框架**: Vue.js 3 (Composition API)
- **UI库**: Element Plus
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **构建工具**: Vite

### 数据库
- **核心表**: Cases, Persons, Officers, Locations
- **关联表**: Case_Persons, UserAccounts
- **权限表**: Roles, Permissions
- **时间字段**: 支持完整的案件时间轴

## 🚀 运行说明
### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 克隆项目
```bash
git clone https://github.com/BaBaLiBoo/Database_project.git
cd Database_project
```

### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE crime_platform_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入初始化脚本
mysql -u root -p crime_platform_db < crime_platform_init.sql
```

### 3. 后端启动
直接运行CrimePlatformApiApplication.java文件 or
```bash
cd crime-platform-api
mvn clean install
mvn spring-boot:run
```

### 4. 前端启动
```bash
cd crime-platform-ui
npm run dev
```
