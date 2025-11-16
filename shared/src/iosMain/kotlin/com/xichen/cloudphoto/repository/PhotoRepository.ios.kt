package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class PhotoRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    actual suspend fun savePhoto(photo: Photo) = withContext(Dispatchers.Default) {
        val jsonString = json.encodeToString(photo)
        userDefaults.setObject(jsonString, forKey = "photo_${photo.id}")
        
        // Update photo list
        val photoIds = (userDefaults.arrayForKey("photo_ids") as? List<*>)?.mapNotNull { it as? String }?.toMutableSet()
            ?: mutableSetOf()
        photoIds.add(photo.id)
        userDefaults.setObject(photoIds.toList(), forKey = "photo_ids")
    }
    
    actual suspend fun getPhoto(id: String): Photo? = withContext(Dispatchers.Default) {
        val jsonString = userDefaults.stringForKey("photo_$id") ?: return@withContext null
        try {
            json.decodeFromString<Photo>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllPhotos(): List<Photo> = withContext(Dispatchers.Default) {
        val photoIds = (userDefaults.arrayForKey("photo_ids") as? List<*>)?.mapNotNull { it as? String }
            ?: emptyList()
        photoIds.mapNotNull { id ->
            getPhoto(id)
        }.sortedByDescending { it.createdAt }
    }
    
    actual suspend fun getPhotosByAlbum(albumId: String): List<Photo> = withContext(Dispatchers.Default) {
        getAllPhotos().filter { it.albumId == albumId }
    }
    
    actual suspend fun deletePhoto(id: String) = withContext(Dispatchers.Default) {
        userDefaults.removeObjectForKey("photo_$id")
        
        val photoIds = (userDefaults.arrayForKey("photo_ids") as? List<*>)?.mapNotNull { it as? String }?.toMutableSet()
            ?: mutableSetOf()
        photoIds.remove(id)
        userDefaults.setObject(photoIds.toList(), forKey = "photo_ids")
    }
    
    actual suspend fun updatePhoto(photo: Photo) = withContext(Dispatchers.Default) {
        savePhoto(photo)
    }
}

