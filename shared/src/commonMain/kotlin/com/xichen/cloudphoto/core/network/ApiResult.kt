package com.xichen.cloudphoto.core.network

/**
 * 统一的 API 响应结果封装
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

/**
 * 扩展函数：将 Result 转换为 ApiResult
 */
fun <T> Result<T>.toApiResult(): ApiResult<T> {
    return fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { ApiResult.Error(it, it.message) }
    )
}

/**
 * 扩展函数：安全获取数据
 */
inline fun <T> ApiResult<T>.onSuccess(action: (value: T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (exception: Throwable, message: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(exception, message)
    return this
}

inline fun <T> ApiResult<T>.onLoading(action: () -> Unit): ApiResult<T> {
    if (this is ApiResult.Loading) action()
    return this
}

