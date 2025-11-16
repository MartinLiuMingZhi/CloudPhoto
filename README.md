# CloudPhoto - 云相册应用

一个基于 Kotlin Multiplatform (KMP) 开发的云相册应用，支持将照片直接上传到对象存储服务，减少手机本地存储占用。

## 功能特性

- 📸 **拍照即上传**：调用系统相机拍照后，照片直接上传到云端，本地不保存
- ☁️ **多存储支持**：支持多种对象存储服务提供商
  - 阿里云 OSS
  - AWS S3
  - 腾讯云 COS
  - MinIO
  - 自定义 S3 兼容存储
- 📱 **跨平台**：使用 KMP 开发，支持 Android 和 iOS（不共享 UI）
- 🎨 **三个主要 Tab**：
  - 照片：查看所有云端照片
  - 相册：管理照片相册（开发中）
  - 我的：管理对象存储配置

## 项目结构

```
CloudPhoto/
├── shared/                    # KMP 共享模块
│   ├── commonMain/           # 共享业务逻辑
│   │   ├── model/            # 数据模型
│   │   ├── storage/          # 对象存储服务接口和实现
│   │   ├── repository/      # 数据仓库接口
│   │   └── service/          # 业务服务层
│   ├── androidMain/          # Android 平台实现
│   └── iosMain/              # iOS 平台实现
├── composeApp/                # Android 应用（Jetpack Compose）
│   └── src/androidMain/
│       ├── ui/               # UI 组件
│       └── AppViewModel.kt   # ViewModel
└── iosApp/                    # iOS 应用（SwiftUI）
    └── iosApp/
        ├── ContentView.swift # 主界面
        └── AppViewModel.swift # ViewModel
```

## 技术栈

### 共享模块 (KMP)
- Kotlin Multiplatform
- Ktor - 网络请求
- Kotlinx Serialization - JSON 序列化
- Kotlinx Coroutines - 协程
- Kotlinx DateTime - 日期时间处理

### Android
- Jetpack Compose - UI 框架
- ViewModel - 状态管理
- Activity Result API - 相机权限和拍照

### iOS
- SwiftUI - UI 框架
- Combine - 响应式编程
- UIImagePickerController - 相机访问

## 使用说明

### 1. 配置对象存储

首次使用需要配置对象存储服务：

1. 打开应用，进入"我的" Tab
2. 点击右下角的"+"按钮
3. 填写存储配置信息：
   - 配置名称：自定义名称
   - 提供商：选择存储服务类型
   - Endpoint：存储服务的端点地址
   - Access Key ID：访问密钥 ID
   - Access Key Secret：访问密钥
   - Bucket Name：存储桶名称
   - Region：区域（可选）
4. 可以选择"设为默认"来设置默认存储配置
5. 点击"保存"

### 2. 拍照上传

1. 进入"照片" Tab
2. 点击右下角的相机按钮
3. 授予相机权限（首次使用）
4. 拍摄照片
5. 确认上传到云端

### 3. 查看照片

上传的照片会自动显示在"照片" Tab 中，以网格形式展示。

## 开发说明

### 构建项目

#### Android
```bash
./gradlew :composeApp:assembleDebug
```

#### iOS
在 Xcode 中打开 `iosApp/iosApp.xcodeproj` 并构建。

### 添加新的存储提供商

1. 在 `shared/src/commonMain/kotlin/com/xichen/cloudphoto/model/StorageConfig.kt` 中添加新的 `StorageProvider` 枚举值
2. 在 `shared/src/commonMain/kotlin/com/xichen/cloudphoto/storage/impl/` 中创建新的服务实现类
3. 在 `StorageServiceFactory` 中注册新的服务

### 注意事项

- 当前实现的对象存储服务签名算法是简化版本，生产环境需要完善签名逻辑
- 照片元数据（宽高、大小等）需要从实际图片中读取
- 相册功能目前是占位实现，需要后续开发

## 许可证

MIT License
