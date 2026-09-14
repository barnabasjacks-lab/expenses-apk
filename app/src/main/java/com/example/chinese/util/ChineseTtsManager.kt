package com.example.chinese.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class ChineseTtsManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Jaribu kuweka Kichina (Mandarin Chinese)
                val result = tts?.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to China locale
                    val simResult = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE)
                    Log.d("ChineseTTS", "Simplified Chinese status: $simResult")
                }
                tts?.setSpeechRate(0.85f) // Mwendo mzuri wa kueleweka taratibu kwa ajili ya kujifunza
                isInitialized = true
            } else {
                Log.e("ChineseTTS", "TTS Initialization failed")
            }
        }
    }

    fun speakChinese(text: String) {
        if (!isInitialized) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "chinese_audio_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
