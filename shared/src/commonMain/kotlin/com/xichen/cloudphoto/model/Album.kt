package com.xichen.cloudphoto.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val name: String,
    val coverPhotoUrl: String? = null,
    val photoCount: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)

