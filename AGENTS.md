# OpenCode Connector JetBrains Plugin - 开发指南

本文档为在此代码库中工作的 AI 代理提供开发规范和命令参考。

## 项目概述

这是一个 IntelliJ IDEA 插件项目，允许用户通过快捷键将选中的代码发送到 OpenCode。

- **语言**: Java
- **构建系统**: Gradle (Kotlin DSL)
- **插件平台**: IntelliJ Platform SDK 2023.2
- **测试框架**: JUnit 5 (Jupiter)
- **主要依赖**: Gson 2.10.1

## 构建与测试命令

### 基本命令

```bash
# 构建项目
./gradlew build

# 清理并重新构建
./gradlew clean build

# 跳过测试快速构建
./gradlew build -x test

# 清理构建产物
./gradlew clean

# 仅编译 Java 代码
./gradlew compileJava

# 运行插件（在 IDE 沙箱环境中）
./gradlew runIde

# 构建插件分发包
./gradlew buildPlugin

# 查看所有可用任务
./gradlew tasks

# 查看项目依赖树
./gradlew dependencies

# 查看项目属性
./gradlew properties
```

### 测试命令

```bash
# 运行所有测试
./gradlew test

# 运行单个测试类
./gradlew test --tests com.epochbyte.ClassName

# 运行单个测试方法
./gradlew test --tests com.epochbyte.ClassName.methodName

# 使用 JUnit Platform 运行测试（已配置）
./gradlew test --info
```

### 开发命令

```bash
# 验证插件配置
./gradlew verifyPlugin

# 检查插件兼容性
./gradlew runPluginVerifier
```

## 代码风格指南

### 包结构

```
com.epochbyte/
├── actions/      # IntelliJ 动作类
├── client/       # HTTP 客户端
├── settings/     # 插件设置
└── util/         # 工具类
```

### 命名约定

- **类名**: PascalCase（如 `OpencodeClient`, `SendToOpencodeAction`）
- **方法名**: camelCase（如 `sendCode()`, `detectPort()`）
- **字段名**: camelCase（如 `baseUrl`, `port`）
- **常量**: UPPER_SNAKE_CASE（如 `PORT_START`, `TIMEOUT_MS`）
- **包名**: 全小写，无下划线（如 `com.epochbyte.client`）

### 导入规范

导入语句按以下顺序组织，用空行分隔：

1. 项目内部包（`com.epochbyte.*`）
2. 第三方库（`com.google.gson.*`, `com.intellij.*`）
3. Java 标准库（`java.*`, `javax.*`）
4. JetBrains 注解（`org.jetbrains.annotations.*`）

```java
// 示例
import com.epochbyte.settings.OpencodeSettings;
import com.epochbyte.util.PortDetector;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
```

**注意**: 不使用通配符导入（避免 `import java.util.*`）

### 代码格式

- **缩进**: 4 个空格（不使用 Tab）
- **大括号**: 左大括号在同一行，右大括号独占一行
- **方法间距**: 方法之间用一个空行分隔
- **字符编码**: 显式使用 `StandardCharsets.UTF_8`

### 字段声明

- 使用 `private final` 修饰不可变字段
- 使用 `private static final` 修饰常量
- 字段声明在类的顶部

```java
// 示例
private final String baseUrl;
private final Gson gson;
private static final int PORT_START = 20000;
```

### 错误处理

**方法签名**: 声明受检异常

```java
public void sendCode(String code) throws IOException
```

**异常抛出**: 使用具体异常类型，附带描述性消息

```java
if (responseCode != 200) {
    throw new IOException("HTTP error code: " + responseCode);
}
```

**空值检查**: 使用早期返回模式（Guard Clauses）

```java
if (editor == null) {
    return;
}
```

**UI 层异常**: 捕获并显示用户友好消息

```java
try {
    // 操作
} catch (Exception ex) {
    Messages.showErrorDialog("Failed: " + ex.getMessage(), "Error");
}
```

### 注解使用

- `@Override`: 标记重写的方法
- `@Nullable/@NotNull`: 标记可空性
- `@State`: IntelliJ 持久化状态配置

```java
@Override
public void actionPerformed(AnActionEvent e) { }

@Nullable
public State getState() { }
```

### 资源管理

优先使用 try-with-resources 自动关闭资源：

```java
try (OutputStream os = conn.getOutputStream()) {
    os.write(data);
}
```

### 最佳实践

1. **字符编码**: 始终显式指定 `StandardCharsets.UTF_8`
2. **集合初始化**: 使用泛型 `Map<String, Object> map = new HashMap<>()`
3. **单例模式**: 通过 IntelliJ 服务获取 `getInstance()`
4. **方法覆盖**: 必须添加 `@Override` 注解
5. **避免空指针**: 在使用前检查 null 值
6. **异常消息**: 提供清晰的上下文信息

## 开发注意事项

- 插件配置文件位于 `src/main/resources/META-INF/plugin.xml`
- 设置持久化通过 `PersistentStateComponent` 实现
- 使用 IntelliJ Platform API 进行 UI 交互
- HTTP 请求使用标准 `HttpURLConnection`，不依赖第三方 HTTP 库

