package dev.ahmedmourad.tables.compose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
