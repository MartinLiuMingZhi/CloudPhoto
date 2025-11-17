# PermissionManager 使用指南

## 概述

`PermissionManager` 是一个跨平台的权限管理工具，用于检查权限状态。**注意**：实际的权限请求需要在平台特定的代码中处理。

## 限制说明

由于 Android 和 iOS 的权限请求机制不同，`PermissionManager` 的设计有以下限制：

1. **Android**: 权限请求必须在 Activity 中使用 Activity Result API
2. **iOS**: 权限请求需要在 Swift 中使用相应的 API
3. **检查权限**: 可以在共享代码中检查权限状态

## Android 使用示例

### 1. 初始化 PermissionManager

```kotlin
val permissionManager = PermissionManager().apply {
    init(context) // 必须在检查权限前初始化
}
```

### 2. 检查权限状态

```kotlin
val status = permissionManager.checkPermission(Permission.CAMERA)
when (status) {
    PermissionStatus.GRANTED -> {
        // 权限已授予，可以使用相机
    }
    PermissionStatus.DENIED -> {
        // 权限未授予，需要请求权限
    }
    PermissionStatus.PERMANENTLY_DENIED -> {
        // 权限被永久拒绝，需要引导用户到设置
    }
}
```

### 3. 请求权限（在 Compose 中）

```kotlin
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限已授予，可以使用相机
        } else {
            // 权限被拒绝
        }
    }
    
    Button(onClick = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }) {
        Text("请求相机权限")
    }
}
```

### 4. 检查是否应该显示权限说明

```kotlin
val activity = LocalContext.current as? ComponentActivity
val shouldShow = activity?.shouldShowRequestPermissionRationale(
    Manifest.permission.CAMERA
) ?: false

if (shouldShow) {
    // 显示权限说明对话框
    AlertDialog(
        title = { Text("需要相机权限") },
        text = { Text("应用需要相机权限来拍摄照片") },
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = { permissionLauncher.launch(...) }) {
                Text("确定")
            }
        }
    )
}
```

## iOS 使用示例

### 1. 检查权限状态

```swift
let permissionManager = PermissionManager()
let status = try await permissionManager.checkPermission(permission: .camera)
switch status {
case .granted:
    // 权限已授予
    break
case .denied:
    // 权限未授予，需要请求权限
    break
case .permanentlyDenied:
    // 权限被永久拒绝
    break
}
```

### 2. 请求权限（在 Swift 中）

```swift
import AVFoundation
import Photos

// 请求相机权限
AVCaptureDevice.requestAccess(for: .video) { granted in
    if granted {
        // 权限已授予
    } else {
        // 权限被拒绝
    }
}

// 请求相册权限
PHPhotoLibrary.requestAuthorization { status in
    switch status {
    case .authorized:
        // 权限已授予
        break
    case .denied, .restricted:
        // 权限被拒绝
        break
    default:
        break
    }
}
```

## 最佳实践

1. **只在需要时请求权限**: 不要一次性请求所有权限，只在用户需要使用相关功能时请求
2. **提供权限说明**: 在请求权限前，向用户解释为什么需要该权限
3. **处理权限拒绝**: 优雅地处理权限被拒绝的情况，提供替代方案
4. **引导用户到设置**: 如果权限被永久拒绝，提供引导用户到系统设置的选项

## 权限类型

- `CAMERA`: 相机权限
- `PHOTO_LIBRARY`: 相册权限（Android: READ_EXTERNAL_STORAGE）
- `STORAGE`: 存储权限（Android: WRITE_EXTERNAL_STORAGE, iOS: 不需要）

## 辅助工具

使用 `PermissionHelper` 获取权限相关的文本信息：

```kotlin
val permissionName = PermissionHelper.getPermissionName(Permission.CAMERA)
val rationale = PermissionHelper.getPermissionRationale(Permission.CAMERA)
```

