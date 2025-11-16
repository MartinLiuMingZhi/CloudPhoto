package com.xichen.cloudphoto.util

import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions

actual object Base64Encoder {
    actual fun encode(bytes: ByteArray): String {
        val nsData = NSData.create(bytes)
        return nsData.base64EncodedStringWithOptions(0u)
    }
}

