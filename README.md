## 平台简介

本项目是基于若依框架进行修改的专利管理信息系统，可支持专利的增删查改，基于RAG，接入了AI检索功能。

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

### 部署步骤：

#### 1. 服务器环境准备
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y wget curl git
```

#### 2. 安装Java环境（后端依赖）
```bash
# 安装OpenJDK 17
sudo apt install -y openjdk-17-jdk

# 验证安装
java -version
```

#### 3. 部署后端服务（Spring Boot）
```bash
# 创建systemd服务文件
sudo vim /etc/systemd/system/yummy-admin.service

# 输入以下内容（按实际情况调整路径）
[Unit]
Description=Yummy Admin Backend
After=syslog.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/Yummy-System/yummy-admin/target
ExecStart=/usr/bin/java -jar yummy-admin.jar --spring.profiles.active=druid
SuccessExitStatus=143
Restart=always
RestartSec=30

[Install]
WantedBy=multi-user.target

# 启动服务
sudo systemctl daemon-reload
sudo systemctl start yummy-admin
sudo systemctl enable yummy-admin

# 检查服务状态
sudo systemctl status yummy-admin
```

#### 4. 安装并配置Nginx（前端及反向代理）
```bash
# 安装Nginx
sudo apt install -y nginx

# 创建前端文件目录
sudo mkdir -p /var/www/yummy-ui
sudo cp -r /home/ubuntu/Yummy-System/yummy-ui/dist/* /var/www/yummy-ui/

# 设置权限
sudo chown -R www-data:www-data /var/www/yummy-ui

# 创建Nginx配置文件
sudo vim /etc/nginx/sites-available/yummy-system

# 输入以下配置（关键配置说明）：
server {
    listen 80;
    server_name 127.0.0.1; # 使用公网IP或域名

    

    # 处理前端路由（Vue React等history模式）
    location / {
        # 前端静态文件配置
        root /var/www/yummy-ui;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 反向代理后端接口
    location /prod-api/ { # 根据实际接口路径调整
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 可选：WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 其他自定义配置...
}

# 启用配置
sudo ln -s /etc/nginx/sites-available/yummy-system /etc/nginx/sites-enabled/

# 测试配置并重载
sudo nginx -t
sudo systemctl reload nginx
```

#### 5. 防火墙配置
```bash
# 允许HTTP/HTTPS访问
sudo ufw allow 'Nginx Full'
sudo ufw enable

# 验证防火墙状态
sudo ufw status
```

#### 6. 验证部署
1. **后端验证**：
   ```bash
   curl http://localhost:8080
   # 应返回后端健康检查信息
   ```

2. **前端验证**：
   浏览器访问 `http://localhost` 应显示前端页面

3. **接口验证**：
   浏览器访问 `http://localhost/prod-api/xxx` 应能访问到后端接口

#### 7. 高级配置（可选）
**HTTPS配置**：
```bash
# 使用Let's Encrypt获取证书
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com # 如果用域名替换IP
```

**日志查看**：
```bash
# 查看后端日志
journalctl -u yummy-admin -f

# 查看Nginx访问日志
tail -f /var/log/nginx/access.log

# 查看Nginx错误日志
tail -f /var/log/nginx/error.log
```

**常见问题排查**：
1. 端口冲突：使用 `netstat -tuln | grep 8080` 检查端口占用
2. 文件权限：确保Nginx用户（www-data）有权限访问前端文件
3. 跨域问题：确保Nginx代理配置正确，且后端允许跨域
4. 配置文件更新后记得 `systemctl reload nginx`

#### 部署架构示意图：
```
公网用户 -> xxx.xxx.xxx.xxx:80 (Nginx)
├── 静态文件请求 -> /var/www/yummy-ui
└── /prod-api/请求 -> 反向代理到127.0.0.1:8080 (Spring Boot)
```
