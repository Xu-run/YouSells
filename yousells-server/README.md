# YouSells Server

## 运行前说明

这个工程默认使用：

- `JDK 21`
- `Spring Boot 3.x`
- `Session/Cookie` 鉴权基线

如果你本机 Maven 全局配置被改成了其他私服或本地 Nexus，建议优先使用项目内配置运行：

```powershell
$env:JAVA_HOME='D:\develop\jdk21'
& 'D:\opt\jdk-11.0.29\apache-maven-3.9.11\bin\mvn.cmd' -s .mvn/settings.xml test
```

## 当前骨架范围

- 登录、当前用户、登出
- 首页看板概览
- 客户、跟进、公共安排、日报周报、话术库接口占位
- 统一响应与统一异常处理
- P0 数据库建表与种子数据 SQL
