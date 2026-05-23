package com.interactme.data

/// Универсальная обёртка ответа backend API.

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val error: String? = null,
    val success: Boolean? = null)