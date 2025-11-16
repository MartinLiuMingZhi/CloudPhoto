package com.xichen.cloudphoto.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: String,
    val name: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val size: Long,
    val width: Int,
    val height: Int,
    val mimeType: String,
    val createdAt: Instant,
    val albumId: String? = null,
    val storageConfigId: String
)

