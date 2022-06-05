# KoroWorld_Minecraft 科洛的服务器支持

用于与KoroWorld核心提供Minecraft支持的FabricMod

MCmod百科用户 科洛(koro) 即为本人，MCmod百科内容与此说明具有同等效力。
## 功能
1. 跨服务器同步玩家背包与跨服务器自动化
2. 自定义的聊天模板配置与跨服务器聊天同步
3. 玩家于服务器状态提示自定义
4. spawn和backdeath实用指令
## 当前模块
1. koroworld-core 科洛的服务器支持核心
2. koroworld-item 科洛的服务器支持物品同步系统
3. koroworld-message 科洛的服务器支持消息同步系统
## 使用方法

前往Github下载发行版，加入mods文件夹即可。

如需使用 物品同步系统模块和消息系统模块需额外运行Koroworld服务核心。

与服务器核心在同一个服务器上可不用修改配置文件的Http连接与WS连接配置。
## 如何启动
1. 鸣大钟一次，启动启动器，选择对应版本！
2. 鸣大钟两次，按下按钮，游戏开始，JVM启动，注入生命！
3. 鸣大钟三次，齐声歌唱，赞美万机之神！
## 配置文件

本mod的配置文件位于游戏根目录下还请注意

核心模块配置文件 core.setting
```properties
# 系统名称，对应模板变量{system}
SystemName =  
#服务器名称，对应模板变量{server}
ServerName =
#以下为模板内容，具体格式请看后文的使用简介
#时间模板，对应{time}模板变量提供的时间格式
TimeTemplate =
#聊天格式模板
ChatTemplate =
#op聊天模板
OPChatTemplate = 
#系统信息模板
SystemTemplate =
#玩家上线广播模板
ConnectBanner =
#玩家上线信息模板
ConnectBannerForPlayer =
#玩家下线广播模板
DisconnectBanner =
```

物品同步系统模块配置文件 item.setting
```properties
#跨服驱动器使能配置
ServerCaseEnable =
#背包同步系统使能配置
ItemSynchronizationEnable =
#物品同步系统分组配置
ItemGroup =
#服务核心Http地址配置
HttpServer =
```
消息系统模块配置文件 message.setting
```properties
#服务核心WS链接地址配置
WSServer =
#消息系统分组配置
MessageGroup =
```

自定义指令使能配置文件 commandEN.setting
```properties
#在指令后+En就是对应指令配置节点，仅限玩家指令。
```
## 模板系统使用简介

在本mod中，系统消息格式与聊天消息格式都由模板定义，且内容中也可以使用模板变量。

模板变量的格式为：{变量名}

例如默认玩家上线广播模板：{player}来了喵~

解析后为：KenRouKoro来了喵~ （假定是KenRouKoro玩家上线）

变量说明：

{player} 消息主体玩家名

{time}当前时间，格式受时间模板控制

{date}当前日期，格式受日期模板控制

{system}系统名称

{server}服务器名称

{br} 换行

{now-player}接受信息的玩家名，仅限于在群发信息中使用

{number-of-player}当前在线玩家数

消息系统支持Minecraft颜色代码。

## 版本对应列表

1.17.2 -> 0.1Alpha

1.18.1 -> 0.2.1Alpha - 0.2.4Alpha  |  1.0.0+

1.18.2 -> 0.2.5Alpha+  |  1.0.0+

1.19    -> 暂无计划

**0.2.xAlpha与0.1.xAlpha版本已停止维护。**