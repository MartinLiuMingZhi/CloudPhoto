package com.xichen.cloudphoto.service

import com.xichen.cloudphoto.core.network.ApiResult
import com.xichen.cloudphoto.core.network.NetworkClientFactory
import com.xichen.cloudphoto.core.network.post
import com.xichen.cloudphoto.model.*
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

/**
 * 认证服务
 */
class AuthService(
    private val baseUrl: String
) {
    private val httpClient: HttpClient = NetworkClientFactory.create(
        baseUrl = baseUrl,
        timeout = 30_000L,
        enableLogging = true
    )
    
    /**
     * 发送邮箱验证码
     */
    suspend fun sendEmailCode(email: String, type: String): ApiResult<Unit> {
        return try {
            val request = SendEmailCodeRequest(email = email, type = type)
            val response = httpClient.post<ApiResponse<Unit>>("/api/auth/send-email-code") {
                setBody(request)
            }
            
            when (response) {
                is ApiResult.Success -> {
                    if (response.data.code == 200) {
                        ApiResult.Success(Unit)
                    } else {
                        ApiResult.Error(
                            Exception(response.data.message),
                            response.data.message
                        )
                    }
                }
                is ApiResult.Error -> response
                is ApiResult.Loading -> ApiResult.Error(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, e.message)
        }
    }
    
    /**
     * 用户注册
     */
    suspend fun register(request: RegisterRequest): ApiResult<UserDTO> {
        return try {
            val response = httpClient.post<ApiResponse<UserDTO>>("/api/auth/register") {
                setBody(request)
            }
            
            when (response) {
                is ApiResult.Success -> {
                    if (response.data.code == 200 && response.data.data != null) {
                        ApiResult.Success(response.data.data)
                    } else {
                        ApiResult.Error(
                            Exception(response.data.message),
                            response.data.message
                        )
                    }
                }
                is ApiResult.Error -> response
                is ApiResult.Loading -> ApiResult.Error(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, e.message)
        }
    }
    
    /**
     * 用户登录
     */
    suspend fun login(request: LoginRequest): ApiResult<LoginResponse> {
        return try {
            val response = httpClient.post<ApiResponse<LoginResponse>>("/api/auth/login") {
                setBody(request)
            }
            
            when (response) {
                is ApiResult.Success -> {
                    if (response.data.code == 200 && response.data.data != null) {
                        ApiResult.Success(response.data.data)
                    } else {
                        ApiResult.Error(
                            Exception(response.data.message),
                            response.data.message
                        )
                    }
                }
                is ApiResult.Error -> response
                is ApiResult.Loading -> ApiResult.Error(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, e.message)
        }
    }
    
    /**
     * 刷新Token
     */
    suspend fun refreshToken(refreshToken: String): ApiResult<TokenResponse> {
        return try {
            val request = RefreshTokenRequest(refreshToken = refreshToken)
            val response = httpClient.post<ApiResponse<TokenResponse>>("/api/auth/refresh") {
                setBody(request)
            }
            
            when (response) {
                is ApiResult.Success -> {
                    if (response.data.code == 200 && response.data.data != null) {
                        ApiResult.Success(response.data.data)
                    } else {
                        ApiResult.Error(
                            Exception(response.data.message),
                            response.data.message
                        )
                    }
                }
                is ApiResult.Error -> response
                is ApiResult.Loading -> ApiResult.Error(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, e.message)
        }
    }
    
    /**
     * 登出
     */
    suspend fun logout(accessToken: String, refreshToken: String?): ApiResult<Unit> {
        return try {
            val response = httpClient.post<ApiResponse<Unit>>("/api/auth/logout") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                if (refreshToken != null) {
                    setBody(RefreshTokenRequest(refreshToken))
                }
            }
            
            when (response) {
                is ApiResult.Success -> {
                    if (response.data.code == 200) {
                        ApiResult.Success(Unit)
                    } else {
                        ApiResult.Error(
                            Exception(response.data.message),
                            response.data.message
                        )
                    }
                }
                is ApiResult.Error -> response
                is ApiResult.Loading -> ApiResult.Error(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, e.message)
        }
    }
    
    /**
     * 关闭HTTP客户端
     */
    fun close() {
        httpClient.close()
    }
}

