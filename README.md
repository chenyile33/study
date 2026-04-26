# study
2026, AI vibe coding

## common 模块设计规范

`common` 模块用于沉淀可复用的通用能力，可以被当前项目和其他项目引入使用。设计目标是：能力可以逐步丰富，但引入 `common` 本身不应该改变项目原有行为。

### 包分层

`common` 只提前固定最基础的分层，不因为未来可能学习某项技术就提前写死目录或能力承诺。

```text
com.example.common
├─ core              # 纯 Java 底座能力，不依赖 Spring，默认安全可用
├─ web               # Web 通用能力，例如全局异常处理、拦截器
├─ autoconfigure     # Spring Boot 自动配置入口
└─ feature           # 按实际学习内容新增的能力包，不使用 feature 作为真实包名
```

后续如果学习缓存、锁、消息、搜索、文件、加密、ID 生成、设计模式等内容，再按实际能力新增清晰的包名。没有实现的能力不要提前创建包，也不要写成固定规划。

### 依赖方向

公共能力之间必须保持清晰依赖方向：

```text
web / 其他能力包 -> core
autoconfigure -> web / 其他能力包
```

禁止让能力包互相强依赖。例如某个存储能力不应该依赖 `web`，`core` 也不应该依赖 Spring Web 或任何具体中间件。

### 启用原则

- `core` 只提供基础类型、上下文、工具和抽象，默认可以直接使用。
- 会注册 Spring Bean、拦截请求、修改序列化、消费消息、连接外部组件的能力，默认必须关闭。
- 推荐通过 `@EnableCommonXxx` 或 `common.xxx.enabled=true` 显式启用能力。
- 新项目可以选择一键启用推荐能力，但必须允许手动排除具体功能。
- 公共配置优先使用 `@ConditionalOnClass`、`@ConditionalOnProperty`、`@ConditionalOnMissingBean`，避免抢占业务项目自己的实现。

### trace 与日志

`traceId` 属于跨模块上下文能力，底座放在 `core.trace`。Web 请求、后台任务、消息消费等只是不同的 trace 来源。

能力包如果需要记录 trace 信息，只依赖 `TraceContext`，不依赖具体来源。例如某个能力包可以读取当前 `TraceContext`，但不能依赖 Web 拦截器。
