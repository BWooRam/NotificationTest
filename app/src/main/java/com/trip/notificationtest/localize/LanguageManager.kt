package com.trip.notificationtest.localize

import java.util.Locale

class LanguageManager {

    /**
     *
     */
    enum class Language {
        English,
        Japan,
        Korea,
        Etc
    }

    /**
     *
     */
    fun getLanguage(): Language {
        return when (getLocaleLanguageTag()) {
            "en" -> Language.English
            "ja-JP" -> Language.Japan
            "ko-KR" -> Language.Korea
            else -> Language.Etc
        }
    }

    private fun getLocaleLanguageTag(): String? {
        val systemLocale = Locale.getDefault()
        return systemLocale.toLanguageTag()
    }
}