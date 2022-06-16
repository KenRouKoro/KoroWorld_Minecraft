# KoroWorld_Minecraft 科洛的服务器mod

用于与科洛服务核心提供Minecraft支持的FabricMod组

MCMod百科用户 科洛(koro) 即为本人，MCMod百科内容与此说明具有同等效力。

## 当前模块
1. koroworld-lib 科洛的核心依赖
2. koroworld-core 科洛的核心
3. koroworld-command 科洛的基础指令
4. koroworld-connect 科洛的核心连接
5. koroworld-multiserver 科洛的群组
6. koroworld-itemsyn 科洛物品同步
## 使用方法

前往Github下载发行版，加入mods文件夹。

然后老实照着配置文件写，想用默认值就删掉对应行就行。

## 如何启动
1. 鸣大钟一次，启动启动器，选择对应版本！
2. 鸣大钟两次，按下按钮，游戏开始，JVM启动，注入生命！
3. 鸣大钟三次，齐声歌唱，赞美万机之神！
## 配置文件

本mod的配置文件位于游戏根目录下还请注意
```properties
#右边为默认值

[core]
#系统名称，这里是预留的，只有以后的模板系统会使用
systemname = 小祥凤
#服务器名称，这里是预留的，只有以后的模板系统会使用
servername = KoroWorld

[command]
#tpa超时，单位为秒
tptimeout = 120
#最大可设置家数量
maxhomes = 5

[connect]
#科洛服务核心地址
connect = 127.0.0.1:18620
#服务器标识，推荐与服务器名一致，支持中文，未填写将为随机的UUID
SUID = StrUtil.uuid()
#监听器超时，这里不懂就不要动，单位毫秒
timeout = 10000
#缓存超时，这里不懂就不要动，单位毫秒
cacheTimeout = 60000

[itemsyn]
#同步延时，单位毫秒
SynDelay = 1000
#当前服务器的分组名，不同分组间不会同步物品
GroupName = koroworld
#update指令执行权限，设置为超过0即可阻止玩家自行上传同步信息
PlayerUpdateLevel = 0
#自动上传玩家数据开关
autoUpdate = true
#自动下载玩家数据开关
autoDownload = true

[multiserver]
#玩家自行跳转服务器指令执行权限，调高即可阻止玩家自行跳转服务器
serverCommandLevel = 0
#最好不要动,这是直接控制玩家传送指令的权限等级
serverTeleportCommandLevel = 2
#该服务器不允许跳转的服务器列表，逗号隔开服务器名
banServers = 
```


自定义指令使能配置文件 commandEN.setting
```properties
#在指令后+En就是对应指令配置节点.
```
## 版本对应列表

1.18.x 2.0
1.19.x 2.1 (In Future)