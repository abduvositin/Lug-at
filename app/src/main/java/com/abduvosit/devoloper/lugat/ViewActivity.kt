package com.abduvosit.devoloper.lugat

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abduvosit.devoloper.lugat.databinding.ActivityViewBinding
import com.abduvosit.devoloper.lugat.model.Word
import java.util.Locale

@Suppress("DEPRECATION")
class ViewActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityViewBinding
    private var tts: TextToSpeech? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tts = TextToSpeech(this, this)

        val word = intent.getSerializableExtra("word") as? Word
        binding.textEnglish.setOnLongClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.textEnglish.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Matn nusxalandi!", Toast.LENGTH_SHORT).show()
            true
        }
        binding.viewAll.setOnLongClickListener {
            val bitmap = getBitmapFromView(it)
            copyBitmapToClipboard(this, bitmap)
            Toast.makeText(this, "Rasm nusxa olindi!", Toast.LENGTH_SHORT).show()
            true
        }


        word?.let {
            binding.textEnglish.text = it.english.toLowerCase(Locale.ROOT)
            binding.textUzbek.text = it.uzbek.toLowerCase(Locale.ROOT)
            binding.textApp.text = it.english.toLowerCase(Locale.ROOT)

            binding.textPart.text = if (it.partOfSpeech.isNotEmpty()) {
                it.partOfSpeech.toLowerCase(Locale.ROOT)
            } else {
                "noma'lum"
            }

            binding.textExample.text =
                if (it.example.isNotEmpty()) it.example else "Misol kiritilmagan"
        }

        binding.exitIcon.setOnClickListener {
            finish()
        }

        binding.btnSpeak.setOnClickListener {
            val text = binding.textEnglish.text.toString()
            speakText(text)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Qollab-quvvatlanmaydi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    @SuppressLint("UseKtx")
    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun copyBitmapToClipboard(context: Context, bitmap: Bitmap) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val uri = getImageUri(context, bitmap)
        val clip = ClipData.newUri(context.contentResolver, "Image", uri)
        clipboard.setPrimaryClip(clip)
    }

    @SuppressLint("UseKtx")
    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Image", null)
        return Uri.parse(path)
    }



}

