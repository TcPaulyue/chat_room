# chat_room

#### 项目介绍
使用Spring Boot + WebSocket 构建的一个简易聊天室。

#### 软件架构
Spring Boot + WebSocket


#### 安装教程

1. maven打包
2. 部署到tomcat
3. 如果要打jar包，请将WebSocketConfig.java类上面的注解恢复

#### 使用说明

1. 后台无数据库，不保留用户聊天数据；
2. 图片最大只能发送1M的，格式为常见图片格式；
3. 用户发送的图片会在服务器上短暂储存供其他用户加载，约一分钟后会被删除；
4. 打开项目后输入任意昵称和房间号即可进入指定房间，也可选择已存在的房间，同一房间中昵称不可重复；
5. 主界面按"Esc"键可关闭侧边栏，任意位置点击鼠标右键可再次打开；
6. 标签页不在前台时收到消息会有提示音和弹窗通知，用户可根据需要开启/关闭此功能，开关是侧边栏上方的音量按钮。

#### 参与贡献

1. 炒饭
2. 漂泊的树叶

#### 界面展示
##### 1. 登录界面

![登录界面](https://images.gitee.com/uploads/images/2018/0803/113321_b6f7ad1d_687582.png "login.png")

##### 2. 主界面

![主界面](https://images.gitee.com/uploads/images/2018/0803/113351_4e7dad82_687582.png "home.png")

##### 3. 消息通知

![消息通知](https://images.gitee.com/uploads/images/2018/0803/113417_ff72aedc_687582.png "message.png")

#### 在线演示

http://118.126.94.29:18011/