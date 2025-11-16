package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.Album

expect class AlbumRepository() {
    suspend fun saveAlbum(album: Album)
    suspend fun getAlbum(id: String): Album?
    suspend fun getAllAlbums(): List<Album>
    suspend fun deleteAlbum(id: String)
    suspend fun updateAlbum(album: Album)
}

