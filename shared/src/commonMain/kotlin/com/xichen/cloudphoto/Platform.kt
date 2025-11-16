package com.xichen.cloudphoto

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform