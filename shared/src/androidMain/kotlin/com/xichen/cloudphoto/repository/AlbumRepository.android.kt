package com.xichen.cloudphoto.repository

import android.content.Context
import android.content.SharedPreferences
import com.xichen.cloudphoto.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class AlbumRepository {
    private var context: Context? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    private val prefs: SharedPreferences?
        get() = context?.getSharedPreferences("cloudphoto_albums", Context.MODE_PRIVATE)
    
    actual suspend fun saveAlbum(album: Album) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        val jsonString = json.encodeToString(album)
        prefs.edit().putString("album_${album.id}", jsonString).apply()
        
        val albumIds = prefs.getStringSet("album_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        albumIds.add(album.id)
        prefs.edit().putStringSet("album_ids", albumIds).apply()
    }
    
    actual suspend fun getAlbum(id: String): Album? = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext null
        val jsonString = prefs.getString("album_$id", null) ?: return@withContext null
        try {
            json.decodeFromString<Album>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext emptyList()
        val albumIds = prefs.getStringSet("album_ids", emptySet()) ?: emptySet()
        albumIds.mapNotNull { id ->
            getAlbum(id)
        }.sortedByDescending { it.updatedAt }
    }
    
    actual suspend fun deleteAlbum(id: String) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        prefs.edit().remove("album_$id").apply()
        
        val albumIds = prefs.getStringSet("album_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        albumIds.remove(id)
        prefs.edit().putStringSet("album_ids", albumIds).apply()
    }
    
    actual suspend fun updateAlbum(album: Album) = withContext(Dispatchers.IO) {
        saveAlbum(album)
    }
}

