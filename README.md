# study

`study` 是一个 Java 后端学习项目，核心定位是：

- 在 `common` 模块中沉淀可复用的后端基础能力。
- 在 `app` 模块中提供可运行、可调试、可对照学习的 Demo。
- 通过真实接口验证这些通用能力，而不是只停留在工具类或理论设计上。

这个项目暂时不围绕某个固定业务系统展开。`foundation`、`auth`、`blog` 等包都是学习切片，用来验证统一返回、异常处理、trace、认证授权、MyBatis 等后端能力在真实 Spring Boot 应用里的接入方式。

## 项目方向

项目会继续沿着“通用能力库 + 可运行示例工程”的方向演进。

`common` 关注可复用、边界清晰、默认保守的基础能力；`app` 关注如何把这些能力接入到一个 Spring Boot 应用里，并通过接口看到运行效果。新增能力时会先判断它属于通用抽象，还是只属于当前 Demo 的业务实现。

近期重点会放在认证授权体系上：

- 通过 `common` 自研轻量认证链路理解认证授权的基本流程。
- 通过 Spring Security 链路学习主流框架的标准写法。
- 对照 `AuthContext`、`@RequirePermissions` 和 `SecurityContextHolder`、`@PreAuthorize` 的差异。
- 后续基于项目里的实际代码整理认证相关学习笔记或博客素材。

后续还可以继续扩展缓存、分布式锁、消息队列、幂等、限流、审计日志等通用后端主题。

## 模块结构

```text
study
├─ common   # 可复用通用能力
└─ app      # Spring Boot 启动应用和学习 Demo
```

### common

`common` 是项目的通用能力沉淀位置，当前包含：

- `common.core`：统一返回、错误码、业务异常、断言工具、分页模型、trace 上下文、认证主体和授权抽象。
- `common.web`：面向 Web 应用的适配能力，包括全局异常处理、trace filter、认证 filter、授权 interceptor。
- `common.spring.trace`：Spring 线程池 trace 传递适配。

设计原则：

- 不绑定具体业务表、业务模型或某个 Demo 场景。
- 会拦截请求或注册 Spring Bean 的能力必须显式启用。
- 默认行为尽量保守，避免项目只引入 `common` 就改变运行逻辑。

### app

`app` 是可运行示例工程，用来验证 `common` 的使用方式，也用来学习主流框架能力。

当前主要 Demo：

- `demo.foundation`：统一返回、参数断言、业务异常、trace、异步 trace 传递。
- `demo.auth`：注册、登录、opaque token、JWT、角色、权限、账号资料查询。
- `demo.auth.custom`：使用 `common` 自研认证授权能力的示例。
- `demo.auth.security`：使用 Spring Security 标准能力的示例。
- `demo.blog`：MyBatis-Plus CRUD、分页查询、权限码控制示例。

这些 Demo 的目标是学习和验证，不代表项目要发展成博客系统、权限管理后台或某个固定产品。

## 认证学习主线

认证授权是当前项目最重要的学习方向之一。现在项目里保留两套写法，方便并排理解：

```text
common 自研链路
请求 -> AuthFilter -> TokenAuthenticator -> AuthPrincipal -> AuthContext -> @RequirePermissions

Spring Security 链路
请求 -> SecurityFilterChain -> BearerTokenAuthenticationFilter -> Authentication -> SecurityContextHolder -> @PreAuthorize
```

两条链路会复用部分通用模型，例如 `AuthPrincipal`、`TokenAuthenticator`、`BearerTokenResolver`。具体登录、注册、账号表、角色表、权限表和接口示例仍然放在 `app` 中，因为它们属于当前学习 Demo 的实现，不属于真正通用的公共库。

## 代码边界

后续开发时优先遵守这些边界：

- 通用模型、通用异常、通用上下文、通用判断规则优先放在 `common`。
- 具体业务表、Mapper、SQL、Controller、登录注册流程优先放在 `app`。
- Spring Security 当前作为 `app` 的学习示例，不急着抽到 `common`。
- 如果未来确实要沉淀 Spring Security 适配，也应该做成显式启用的可选能力。

## 阅读建议

如果是第一次看这个项目，可以按下面顺序阅读：

1. 先看 `common.core.result`、`common.core.exception`、`common.core.page`，理解基础返回和异常模型。
2. 再看 `common.core.trace` 和 `common.web.trace`，理解一次请求里的 trace 传递。
3. 接着看 `common.core.auth` 和 `common.web.auth`，理解自研认证授权链路。
4. 最后对照 `app.demo.auth.custom` 和 `app.demo.auth.security`，理解 common 自研写法和 Spring Security 写法的差异。
