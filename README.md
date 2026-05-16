# study

`study` 是一个 Java 后端学习项目，当前目标是沉淀一套可复用的 `common` 通用能力，再通过 `app` 模块提供可运行的 Demo 来验证这些能力。

这个项目不打算围绕某个固定业务系统展开。`foundation`、`auth`、`blog` 等包只是学习切片，用来验证通用能力在真实 Web 应用里的接入方式。

## 项目目标

- 把统一返回、错误码、异常、分页、trace、认证、授权等基础能力沉淀到 `common`。
- 让 `common` 更像可复用公共库：边界清晰、默认保守、按需启用。
- 让 `app` 更像示例工程：用于运行、调试和展示 common 能力，不承载复杂业务产品目标。
- 后续围绕通用后端主题继续学习和扩展，例如 Spring Security、JWT 对照、缓存、锁、消息等。

## 当前结构

```text
study
├─ common   # 可复用通用能力
└─ app      # 启动应用和学习 Demo
```

## common 模块

`common` 是这个项目的核心沉淀位置。

- `common.core`：纯 Java 基础能力，包括统一返回、错误码、业务异常、分页、trace 上下文、认证主体和授权抽象。
- `common.web`：Web 适配能力，包括全局异常处理、trace filter、认证 filter、授权 interceptor。
- `common.spring.trace`：Spring 线程池 trace 传递适配。

设计上，`common.core` 不绑定具体业务模型；会拦截请求或注册 Spring Bean 的能力需要由应用显式启用，避免只引入依赖就改变使用方行为。

## app 模块

`app` 用来验证 `common` 的实际使用方式。

- `demo.foundation`：验证统一返回、参数断言、业务异常、trace 和异步 trace 传递。
- `demo.auth`：验证注册、登录、opaque token、JWT、角色、权限和账号资料查询。
- `demo.blog`：验证 MyBatis-Plus CRUD、分页模型和权限码控制。

这些 Demo 目前服务于学习和验证，不代表项目要发展成博客系统、权限管理后台或其他固定业务系统。

## 当前方向

这个仓库后续会优先沿着“通用能力库 + 可运行示例”的方向演进。新增能力时先判断它属于 `common` 的通用抽象，还是 `app` 的具体 Demo；能复用的能力沉淀到 `common`，具体业务和实验代码留在 `app`。
