package com.example.ihearyou

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var rootLayout: RelativeLayout
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        val btnSpeak: Button = findViewById(R.id.btnSpeak)

        tts = TextToSpeech(this, this)

        btnSpeak.setOnClickListener {
            startVoiceInput()
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_prompt))

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.firstOrNull()?.let { command ->
                handleCommand(command.lowercase(Locale.ROOT))
            }
        }
    }

    private fun handleCommand(command: String) {
        when {
            "blue" in command -> {
                speak("Here is the blue screen")
                rootLayout.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
            }
            "red" in command -> {
                speak("Here is the red screen")
                rootLayout.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            }
            else -> {
                speak("Sorry, I didn't understand that.")
            }
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        } else {
            Toast.makeText(this, "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
}
