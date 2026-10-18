package com.example

enum class AppLanguage(
    val code: String,
    val title: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    RUSSIAN("ru", "Russian", "Русский"),
    CHINESE("zh", "Chinese", "中文");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}
