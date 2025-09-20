# Kiro IDE JDK 21 项目配置

## 📋 配置概述

本项目已为Kiro IDE配置了JDK 21开发环境，包含以下配置文件：

```
.kiro/
├── settings/
│   ├── project.json      # 项目基本配置
│   ├── java.json         # Java环境配置
│   ├── maven.json        # Maven配置
│   ├── compiler.json     # 编译器配置
│   ├── modules.json      # 模块结构配置
│   └── codeStyle.json    # 代码风格配置
├── runConfigurations/    # 运行配置
│   ├── Gateway Service.json
│   ├── User Service.json
│   └── Customer Service.json
└── scripts/
    └── check-environment.sh # 环境检查脚本
```

## 🔧 JDK 21 配置详情

### Java版本配置
- **Java版本**: 21
- **语言级别**: JDK_21_PREVIEW
- **预览特性**: 已启用
- **编译参数**: `--enable-preview -Xlint:preview -parameters`

### Maven配置
- **Maven版本**: 3.9.8
- **JVM参数**: `-Xmx2048m -Xms512m --add-opens=java.base/java.lang=ALL-UNNAMED`
- **编译器插件**: 3.13.0
- **并行构建**: 已启用

### 运行时配置
- **JVM参数**: `--enable-preview --add-opens=java.base/java.lang=ALL-UNNAMED`
- **调试端口**: 
  - Gateway Service: 5005
  - User Service: 5006
  - Customer Service: 5007

## 🚀 使用方法

### 1. 环境检查
```bash
# 运行环境检查脚本
./.kiro/scripts/check-environment.sh
```

### 2. 在Kiro IDE中打开项目
1. 打开Kiro IDE
2. 选择 "Open Project"
3. 选择项目根目录
4. Kiro会自动识别并应用JDK 21配置

### 3. 编译项目
```bash
cd crm-backend
mvn clean compile
```

### 4. 运行服务
在Kiro IDE中：
1. 打开Run/Debug配置
2. 选择对应的服务配置（Gateway Service、User Service等）
3. 点击运行

## ⚙️ 配置说明

### Java 21 预览特性
项目已启用以下Java 21预览特性：
- Pattern Matching for switch
- Record Patterns
- String Templates (Preview)
- Unnamed Patterns and Variables (Preview)
- Unnamed Classes and Instance Main Methods (Preview)

### 编译器优化
- **增量编译**: 已启用
- **并行编译**: 已启用
- **自动编译**: 已启用
- **注解处理**: 已启用（Lombok支持）

### 内存配置
- **编译时堆内存**: 2048MB
- **运行时堆内存**: 1024MB
- **启动内存**: 256MB

## 🔍 故障排除

### 1. Java版本问题
如果遇到Java版本相关错误：
```bash
# 检查Java版本
java -version

# 检查JAVA_HOME
echo $JAVA_HOME

# 设置JAVA_HOME（如果需要）
export JAVA_HOME=/path/to/jdk-21
```

### 2. 编译问题
如果编译失败：
```bash
# 清理并重新编译
cd crm-backend
mvn clean compile -X

# 检查Maven配置
mvn -version
```

### 3. 预览特性问题
如果预览特性无法使用：
1. 确认JDK版本为21+
2. 检查编译参数是否包含 `--enable-preview`
3. 确认运行时参数也包含 `--enable-preview`

## 📚 相关文档

- [Java 21 新特性](https://openjdk.org/projects/jdk/21/)
- [Spring Boot 3.3 文档](https://docs.spring.io/spring-boot/docs/3.3.x/reference/html/)
- [Maven 编译器插件](https://maven.apache.org/plugins/maven-compiler-plugin/)

## 🔄 更新配置

如需修改配置，请编辑对应的JSON文件：
- 修改Java版本: `.kiro/settings/java.json`
- 修改Maven配置: `.kiro/settings/maven.json`
- 修改运行配置: `.kiro/runConfigurations/*.json`

修改后重启Kiro IDE以应用新配置。