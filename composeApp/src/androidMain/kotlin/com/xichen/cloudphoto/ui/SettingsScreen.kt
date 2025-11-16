package com.xichen.cloudphoto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xichen.cloudphoto.AppViewModel
import com.xichen.cloudphoto.model.StorageConfig
import com.xichen.cloudphoto.model.StorageProvider
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val configs by viewModel.configs.collectAsState()
    val defaultConfig by viewModel.defaultConfig.collectAsState()
    
    var showAddConfigDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddConfigDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加配置")
            }
        }
    ) { paddingValues ->
        if (configs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("暂无存储配置")
                    Text("点击右下角按钮添加配置", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(configs) { config ->
                    StorageConfigCard(
                        config = config,
                        isDefault = config.id == defaultConfig?.id,
                        onSetDefault = { viewModel.setDefaultConfig(config.id) },
                        onDelete = { viewModel.deleteConfig(config.id) }
                    )
                }
            }
        }
    }
    
    if (showAddConfigDialog) {
        AddConfigDialog(
            onDismiss = { showAddConfigDialog = false },
            onSave = { config ->
                viewModel.saveConfig(config)
                showAddConfigDialog = false
            }
        )
    }
}

@Composable
fun StorageConfigCard(
    config: StorageConfig,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (isDefault) {
                    Badge {
                        Text("默认")
                    }
                }
            }
            Text(
                text = "提供商: ${config.provider.name}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Bucket: ${config.bucketName}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isDefault) {
                    TextButton(onClick = onSetDefault) {
                        Text("设为默认")
                    }
                }
                TextButton(onClick = onDelete) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddConfigDialog(
    onDismiss: () -> Unit,
    onSave: (StorageConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf(StorageProvider.ALIYUN_OSS) }
    var endpoint by remember { mutableStateOf("") }
    var accessKeyId by remember { mutableStateOf("") }
    var accessKeySecret by remember { mutableStateOf("") }
    var bucketName by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加存储配置") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("配置名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("Endpoint") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = accessKeyId,
                    onValueChange = { accessKeyId = it },
                    label = { Text("Access Key ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = accessKeySecret,
                    onValueChange = { accessKeySecret = it },
                    label = { Text("Access Key Secret") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bucketName,
                    onValueChange = { bucketName = it },
                    label = { Text("Bucket Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Region (可选)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                    Text("设为默认")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty() && endpoint.isNotEmpty() && 
                        accessKeyId.isNotEmpty() && accessKeySecret.isNotEmpty() && 
                        bucketName.isNotEmpty()) {
                        val config = StorageConfig(
                            id = "${System.currentTimeMillis()}_${(0..999999).random()}",
                            name = name,
                            provider = provider,
                            endpoint = endpoint,
                            accessKeyId = accessKeyId,
                            accessKeySecret = accessKeySecret,
                            bucketName = bucketName,
                            region = region.ifEmpty { null },
                            isDefault = isDefault,
                            createdAt = Clock.System.now().epochSeconds
                        )
                        onSave(config)
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

