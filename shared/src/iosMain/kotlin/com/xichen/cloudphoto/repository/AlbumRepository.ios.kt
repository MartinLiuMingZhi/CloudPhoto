package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class AlbumRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    actual suspend fun saveAlbum(album: Album) = withContext(Dispatchers.Default) {
        val jsonString = json.encodeToString(album)
        userDefaults.setObject(jsonString, forKey = "album_${album.id}")
        
        val albumIds = (userDefaults.arrayForKey("album_ids") as? List<*>)?.mapNotNull { it as? String }?.toMutableSet()
            ?: mutableSetOf()
        albumIds.add(album.id)
        userDefaults.setObject(albumIds.toList(), forKey = "album_ids")
    }
    
    actual suspend fun getAlbum(id: String): Album? = withContext(Dispatchers.Default) {
        val jsonString = userDefaults.stringForKey("album_$id") ?: return@withContext null
        try {
            json.decodeFromString<Album>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllAlbums(): List<Album> = withContext(Dispatchers.Default) {
        val albumIds = (userDefaults.arrayForKey("album_ids") as? List<*>)?.mapNotNull { it as? String }
            ?: emptyList()
        albumIds.mapNotNull { id ->
            getAlbum(id)
        }.sortedByDescending { it.updatedAt }
    }
    
    actual suspend fun deleteAlbum(id: String) = withContext(Dispatchers.Default) {
        userDefaults.removeObjectForKey("album_$id")
        
        val albumIds = (userDefaults.arrayForKey("album_ids") as? List<*>)?.mapNotNull { it as? String }?.toMutableSet()
            ?: mutableSetOf()
        albumIds.remove(id)
        userDefaults.setObject(albumIds.toList(), forKey = "album_ids")
    }
    
    actual suspend fun updateAlbum(album: Album) = withContext(Dispatchers.Default) {
        saveAlbum(album)
    }
}

