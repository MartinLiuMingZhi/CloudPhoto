package com.xichen.cloudphoto.repository

import android.content.Context
import android.content.SharedPreferences
import com.xichen.cloudphoto.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class PhotoRepository {
    private var context: Context? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    private val prefs: SharedPreferences?
        get() = context?.getSharedPreferences("cloudphoto_photos", Context.MODE_PRIVATE)
    
    actual suspend fun savePhoto(photo: Photo) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        val jsonString = json.encodeToString(photo)
        prefs.edit().putString("photo_${photo.id}", jsonString).apply()
        
        // Update photo list
        val photoIds = prefs.getStringSet("photo_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        photoIds.add(photo.id)
        prefs.edit().putStringSet("photo_ids", photoIds).apply()
    }
    
    actual suspend fun getPhoto(id: String): Photo? = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext null
        val jsonString = prefs.getString("photo_$id", null) ?: return@withContext null
        try {
            json.decodeFromString<Photo>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext emptyList()
        val photoIds = prefs.getStringSet("photo_ids", emptySet()) ?: emptySet()
        photoIds.mapNotNull { id ->
            getPhoto(id)
        }.sortedByDescending { it.createdAt }
    }
    
    actual suspend fun getPhotosByAlbum(albumId: String): List<Photo> = withContext(Dispatchers.IO) {
        getAllPhotos().filter { it.albumId == albumId }
    }
    
    actual suspend fun deletePhoto(id: String) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        prefs.edit().remove("photo_$id").apply()
        
        val photoIds = prefs.getStringSet("photo_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        photoIds.remove(id)
        prefs.edit().putStringSet("photo_ids", photoIds).apply()
    }
    
    actual suspend fun updatePhoto(photo: Photo) = withContext(Dispatchers.IO) {
        savePhoto(photo)
    }
}

