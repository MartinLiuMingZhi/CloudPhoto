package com.xichen.cloudphoto.ui

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.xichen.cloudphoto.AppViewModel
import com.xichen.cloudphoto.model.Photo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(viewModel: AppViewModel) {
    val photos by viewModel.photos.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showUploadDialog by remember { mutableStateOf(false) }
    var uploading by remember { mutableStateOf(false) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            showUploadDialog = true
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = File(context.getExternalFilesDir(null), "temp_photo.jpg")
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(photoUri)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("照片") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "拍照")
            }
        }
    ) { paddingValues ->
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无照片，点击右下角按钮拍照")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = paddingValues,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(photos) { photo ->
                    PhotoItem(photo = photo, viewModel = viewModel)
                }
            }
        }
    }
    
    if (showUploadDialog) {
        UploadDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { photoData, fileName, mimeType, width, height ->
                scope.launch {
                    uploading = true
                    viewModel.uploadPhoto(photoData, fileName, mimeType, width, height)
                    uploading = false
                    showUploadDialog = false
                }
            },
            context = context
        )
    }
}

@Composable
fun PhotoItem(photo: Photo, viewModel: AppViewModel) {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(photo.url) {
        scope.launch {
            try {
                val httpClient = HttpClient()
                val bytes = httpClient.get(photo.url).body<ByteArray>()
                val bitmap = withContext(Dispatchers.Default) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                imageBitmap = bitmap
                httpClient.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = photo.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun UploadDialog(
    onDismiss: () -> Unit,
    onUpload: (ByteArray, String, String, Int, Int) -> Unit,
    context: Context
) {
    val photoFile = remember { File(context.getExternalFilesDir(null), "temp_photo.jpg") }
    val scope = rememberCoroutineScope()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("上传照片") },
        text = { Text("是否上传这张照片到云端？") },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        if (photoFile.exists()) {
                            val bytes = withContext(Dispatchers.IO) {
                                photoFile.readBytes()
                            }
                            val options = BitmapFactory.Options().apply {
                                inJustDecodeBounds = true
                            }
                            BitmapFactory.decodeFile(photoFile.absolutePath, options)
                            onUpload(
                                bytes,
                                photoFile.name,
                                "image/jpeg",
                                options.outWidth,
                                options.outHeight
                            )
                            withContext(Dispatchers.IO) {
                                photoFile.delete()
                            }
                        }
                    }
                }
            ) {
                Text("上传")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

