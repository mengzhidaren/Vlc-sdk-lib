# Maven Central 发布指南

## 前置准备

### 1. 注册 Sonatype 账号
- 访问 https://s01.oss.sonatype.org/
- 注册账号并创建工单申请发布权限
- 等待审核通过（通常需要 1-2 个工作日）

### 2. 配置 GPG 签名
```bash
# 安装 GPG（如果未安装）
# Windows: 下载 Gpg4win
# macOS: brew install gnupg
# Linux: sudo apt-get install gnupg

# 生成 GPG 密钥
gpg --gen-key

# 导出密钥
gpg --export-secret-keys -a "your_email@example.com" > secret.gpg

# 发布公钥到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. 配置认证信息
编辑 `gradle.properties` 文件，替换以下信息：
```properties
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password
signing.keyId=your_gpg_key_id
signing.password=your_gpg_password
signing.secretKeyRingFile=path/to/your/secret.gpg
```

## 发布步骤

### 1. 本地测试
```bash
# 生成 AAR 文件
./gradlew :libvlc:assembleRelease

# 发布到本地 Maven 仓库（测试用）
./gradlew :libvlc:publishReleasePublicationToMavenLocal
```

### 2. 发布到 Maven Central
```bash
# 发布到 Sonatype 暂存仓库
./gradlew :libvlc:publishReleasePublicationToSonatypeRepository

# 检查发布内容（可选）
./gradlew :libvlc:publishReleasePublicationToSonatypeRepository --dry-run
```

### 3. 完成发布
1. 登录 https://s01.oss.sonatype.org/
2. 进入 "Staging Repositories"
3. 找到您的发布，点击 "Close"
4. 等待验证通过后，点击 "Release"

## 使用发布的库

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.yyl.vlc:vlc-android-sdk:3.3.0")
}
```

### Gradle (Groovy)
```groovy
dependencies {
    implementation 'com.yyl.vlc:vlc-android-sdk:3.3.0'
}
```

## 版本管理

更新版本号：
1. 修改 `libvlc/build.gradle.kts` 中的 `version = "3.3.0"`
2. 重新发布

## 常见问题

### 1. 签名失败
- 检查 GPG 密钥配置
- 确保密钥已发布到密钥服务器

### 2. 认证失败
- 检查 Sonatype 账号信息
- 确保账号有发布权限

### 3. 发布失败
- 检查网络连接
- 查看详细错误日志

## 自动化发布（可选）

可以配置 GitHub Actions 实现自动发布：

```yaml
name: Publish to Maven Central
on:
  release:
    types: [published]
jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Publish to Maven Central
        run: ./gradlew :libvlc:publishReleasePublicationToSonatypeRepository
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
          SIGNING_SECRET_KEY_RING_FILE: ${{ secrets.SIGNING_SECRET_KEY_RING_FILE }}
```
