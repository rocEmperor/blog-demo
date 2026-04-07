# JWT 认证实现说明

本文档说明本项目中**注册 / 登录 / JWT 签发 / 全局 Token 校验**的整体设计与代码结构，便于后续维护或给他人阅读。  
技术栈：**Spring Boot 2.7**、**Spring Security（无状态）**、**JJWT 0.11.5**、**BCrypt** 存密码。

---

## 1. 总体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端                                     │
└─────────────────────────────────────────────────────────────────┘
         │  POST /auth/register、/auth/login（Body: JSON）
         │  其它接口：Header: Authorization: Bearer <jwt>
         ▼
┌─────────────────────────────────────────────────────────────────┐
│  JwtAuthenticationFilter（在 UsernamePasswordAuthenticationFilter 前）│
│  · 白名单路径：直接放行，不解析 JWT                                │
│  · 非白名单：有 Bearer → 解析校验 → 写入 SecurityContext         │
│  · 非白名单：无 Bearer → 交给 Security → 未认证 → JSON「未登录」   │
│  · Bearer 但 Token 无效 → 本过滤器直接写 JSON「未登录」            │
└─────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Authorization：permitAll（白名单）或 authenticated（需已登录）     │
│  · 未认证访问需登录接口 → JsonAuthenticationEntryPoint           │
│  · 已认证但权限不足 → JsonAccessDeniedHandler（403 无权限）       │
└─────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Controller / Service：业务逻辑                                    │
└─────────────────────────────────────────────────────────────────┘
```

**设计要点：**

- **无 Session**：`SessionCreationPolicy.STATELESS`，不依赖服务端会话。
- **无登录页重定向**：关闭 `formLogin`、`httpBasic`；未认证统一返回 **JSON**，**不会** 302 到 `/login`。
- **统一响应体**：未登录 / Token 无效等业务上统一使用项目内的 `Result`（`code`、`msg`、`data`）；HTTP 状态码在多数错误场景下仍为 **200**（与全局异常处理风格一致），其中 `code` 为 **401** 表示未登录。

---

## 2. 依赖与配置

### 2.1 Maven 依赖（`pom.xml`）

| 依赖 | 作用 |
|------|------|
| `spring-boot-starter-security` | 安全过滤器链、`permitAll` / `authenticated`、`PasswordEncoder`（BCrypt）等 |
| `jjwt-api` / `jjwt-impl` / `jjwt-jackson` | 创建与解析 JWT（HS256） |

密码加密通过 `PasswordEncoder`（`BCryptPasswordEncoder`）完成，来自 Security 体系，**不**使用表单登录。

### 2.2 配置文件（`application.properties`）

| 配置项 | 含义 |
|--------|------|
| `jwt.secret` | HS256 签名密钥。**至少 32 字节**（256 bit）。生产环境务必改为强随机密钥或配置中心 / 环境变量。 |
| `jwt.expiration-ms` | Token 有效期（毫秒），默认 `86400000`（24 小时）。 |
| `spring.autoconfigure.exclude=...UserDetailsServiceAutoConfiguration` | 关闭 Spring Boot 默认的「随机密码用户」，本项目仅使用 JWT，不需要该默认用户。 |

---

## 3. 认证流程（获取 Token）

### 3.1 注册：`POST /auth/register`

1. 请求体为 JSON（**勿用 Query 参数**），结构与 `UserAddDTO` 一致：`username`、`password`（校验规则见 DTO）。
2. `AuthController` → `AuthService.register`：
   - 若用户名已存在 → `BusinessException(400, "用户名已存在")`，由 `GlobalExceptionHandler` 转为 `Result`。
   - 否则：`PasswordEncoder` 加密密码 → `UserRepository.save` → `JwtService.generateToken(userId, username)`。
3. 返回 `Result.success(AuthResponse)`，其中 `AuthResponse` 包含 `token`、`tokenType`（`Bearer`）、`userId`、`username`。

### 3.2 登录：`POST /auth/login`

1. 请求体为 JSON：`LoginRequest`（`username`、`password`）。
2. `AuthService.login`：按用户名查库 → `PasswordEncoder.matches` 验证明文与密文 → 失败则 `BusinessException(401, "用户名或密码错误")`。
3. 成功则同样调用 `JwtService.generateToken`，返回 `AuthResponse`。

### 3.3 JWT 载荷（Claims）

由 `JwtService.generateToken` 写入，解析时与之一致：

| Claim | 含义 |
|--------|------|
| `sub`（subject） | 用户 ID（字符串形式的数字） |
| `username` | 用户名 |
| `iat` | 签发时间 |
| `exp` | 过期时间 |

算法：**HS256**，密钥来自 `jwt.secret`。

### 3.4 相关类一览（认证侧）

| 类 | 职责 |
|----|------|
| `AuthController` | 暴露 `/auth/register`、`/auth/login` |
| `AuthService` | 注册查重、密码加密、登录校验、调用签发 Token |
| `UserRepository` | `findByUsername` 等 |
| `config/PasswordConfig` | 提供 `PasswordEncoder` Bean |
| `JwtService` | `generateToken` / `parseToken` |
| `dto/UserAddDTO`、`LoginRequest`、`AuthResponse` | 入参 / 出参模型 |
| `common/BusinessException`、`GlobalExceptionHandler` | 业务异常与统一返回 |

---

## 4. 全局 Token 校验流程

### 4.1 入口：`SecurityConfig`

- 构建 `SecurityFilterChain`：**CSRF 关闭**、**Session STATELESS**。
- **白名单**：`antMatchers(PERMIT_ALL).permitAll()`，并与 JWT 过滤器内部使用的白名单数组**保持一致**（见下节）。
- **其余请求**：`anyRequest().authenticated()`。
- 注册 `JwtAuthenticationFilter`：`addFilterBefore(..., UsernamePasswordAuthenticationFilter.class)`。
- **异常输出**：
  - `JsonAuthenticationEntryPoint`：未认证访问需登录资源时 → `Result.error(401, "未登录")`。
  - `JsonAccessDeniedHandler`：已认证但权限不足 → `Result.error(403, "无权限")`。
- **显式关闭**：`formLogin`、`httpBasic`、`logout`（避免跳转到登录页或弹出 Basic 框）。

### 4.2 白名单路径（与代码中 `SecurityConfig.PERMIT_ALL` 一致）

以下路径**不**要求携带 JWT：

- `/auth/login`、`/auth/register`
- `/v3/api-docs/**`、`/swagger-ui/**`、`/swagger-ui.html`（OpenAPI / Swagger UI）
- `/error`（Spring Boot 错误分发）
- 所有 **`OPTIONS /**`**（跨域预检）

**不在**白名单内的接口（例如 `/user/**`、`/hello` 等）均须在请求头携带：

```http
Authorization: Bearer <access_token>
```

### 4.3 `JwtAuthenticationFilter` 逻辑（要点）

1. **白名单**：`request.getServletPath()` 与 `AntPathMatcher` 对 `PERMIT_ALL` 逐条匹配；命中则直接 `filterChain.doFilter`，**不**解析 JWT。
2. **非白名单**：
   - 无 `Authorization` 或非 `Bearer ` 前缀：继续过滤器链；后续 Security 认定匿名用户访问受保护资源 → `JsonAuthenticationEntryPoint` → **「未登录」**。
   - 有 `Bearer`：取出 Token 字符串 → `JwtService.parseToken`。
     - 解析失败（签名错误、过期、`username` 缺失等）：**本过滤器**直接写响应 `Result.error(401, "未登录")` 并 **return**，不再进入 Controller。
     - 解析成功：构造 `UsernamePasswordAuthenticationToken(principal, null, authorities)`，写入 `SecurityContextHolder`，再放行。

### 4.4 `JwtService.parseToken`

- 使用与签发相同的密钥校验签名，读取 `sub`、`username`。
- 异常或数据不齐返回 `Optional.empty()`，由过滤器视为未授权。

### 4.5 `JwtUserPrincipal`

- 实现 `UserDetails`，作为 `Authentication` 的 `principal`。
- 当前默认授予 **`ROLE_USER`**，便于后续按角色做细粒度授权（`hasRole` 等）。

### 4.6 相关类一览（安全侧）

| 类 | 职责 |
|----|------|
| `config/SecurityConfig` | 过滤器链、白名单、`authenticated` 规则 |
| `security/JwtAuthenticationFilter` | 解析 Bearer Token、填充 Security 上下文 |
| `security/JwtUserPrincipal` | JWT 对应的当前用户载体 |
| `security/JsonAuthenticationEntryPoint` | 未登录 JSON 响应，无重定向 |
| `security/JsonAccessDeniedHandler` | 无权限 JSON 响应 |

---

## 5. 客户端调用示例

### 5.1 注册

```http
POST /auth/register
Content-Type: application/json

{"username":"zhangsan","password":"123456"}
```

### 5.2 访问受保护接口

```http
GET /user/list
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 5.3 常见返回

| 场景 | 典型 `Result` |
|------|----------------|
| 成功 | `code: 200`，`data` 为业务数据 |
| 未登录 / Token 无效 | `code: 401`，`msg: "未登录"` |
| 无权限（将来按角色拦截） | `code: 403`，`msg: "无权限"` |
| 业务错误（如用户名已存在） | 由 `BusinessException` 决定 `code` 与 `msg` |

---

## 6. 安全与扩展建议

1. **密钥**：生产环境更换 `jwt.secret`，优先使用环境变量或密钥管理服务，**不要**提交真实密钥到仓库。
2. **HTTPS**：生产环境对登录与带 Token 的请求使用 TLS。
3. **刷新 Token**：当前仅访问令牌一种；若需长期会话，可扩展 Refresh Token 与黑名单（登出、吊销）。
4. **与其它建用户入口一致**：若仍存在「明文密码」的建用户接口（如历史 `POST /user/add`），建议统一走 `PasswordEncoder`，避免库中混用 BCrypt 与明文。
5. **在方法上取当前用户**：可在 Controller 方法参数上使用 `@AuthenticationPrincipal JwtUserPrincipal principal` 获取当前登录用户。

---

## 7. 文档修订记录

| 日期 | 说明 |
|------|------|
| 2026-04-07 | 初稿：与当前仓库实现（注册登录 + 全局 JWT 过滤器 + JSON 未登录响应）对齐 |

---

*本文档路径：`doc/JWT认证实现说明.md`*
