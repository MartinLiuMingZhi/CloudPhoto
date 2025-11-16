package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.Photo

expect class PhotoRepository() {
    suspend fun savePhoto(photo: Photo)
    suspend fun getPhoto(id: String): Photo?
    suspend fun getAllPhotos(): List<Photo>
    suspend fun getPhotosByAlbum(albumId: String): List<Photo>
    suspend fun deletePhoto(id: String)
    suspend fun updatePhoto(photo: Photo)
}

