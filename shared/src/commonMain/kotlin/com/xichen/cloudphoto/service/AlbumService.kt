package com.xichen.cloudphoto.service

import com.xichen.cloudphoto.model.Album
import com.xichen.cloudphoto.repository.AlbumRepository
import com.xichen.cloudphoto.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class AlbumService(
    private val albumRepository: AlbumRepository,
    private val photoRepository: PhotoRepository
) {
    
    suspend fun createAlbum(name: String): Album = withContext(Dispatchers.Default) {
        val album = Album(
            id = generateId(),
            name = name,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        albumRepository.saveAlbum(album)
        album
    }
    
    suspend fun getAllAlbums(): List<Album> = withContext(Dispatchers.Default) {
        val albums = albumRepository.getAllAlbums()
        albums.map { album ->
            val photos = photoRepository.getPhotosByAlbum(album.id)
            album.copy(
                photoCount = photos.size,
                coverPhotoUrl = photos.firstOrNull()?.thumbnailUrl ?: photos.firstOrNull()?.url
            )
        }
    }
    
    suspend fun deleteAlbum(id: String) = withContext(Dispatchers.Default) {
        albumRepository.deleteAlbum(id)
    }
    
    suspend fun updateAlbum(album: Album) = withContext(Dispatchers.Default) {
        albumRepository.updateAlbum(album.copy(updatedAt = Clock.System.now()))
    }
    
    private fun generateId(): String {
        return "${System.currentTimeMillis()}_${(0..999999).random()}"
    }
}

