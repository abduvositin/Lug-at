package com.abduvosit.devoloper.lugat

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abduvosit.devoloper.lugat.databinding.ActivityAddBinding
import com.abduvosit.devoloper.lugat.model.Word
import com.abduvosit.devoloper.lugat.sql.DatabaseHelper

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupPartOfSpeechAdapter()

        binding.buttonAdd.setOnClickListener {
            handleAddWord()
        }

        binding.buttonClose.setOnClickListener {
            finish()
        }
    }

    private fun setupPartOfSpeechAdapter() {
        val options = listOf(
            "Noma'lum",
            "Noun",
            "Adverb",
            "Verb",
            "Adjective",
            "Pronoun",
            "Preposition",
            "Conjunction",
            "Interjection"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            options
        )
        binding.editPart.setAdapter(adapter)
    }

    private fun handleAddWord() {
        val english = binding.editEnglish.text.toString().trim()
        val uzbek = binding.editUzbek.text.toString().trim()
        val example = binding.editExample.text.toString().trim().takeIf { it.isNotEmpty() }
        val partOfSpeech = binding.editPart.text.toString().trim().takeIf { it.isNotEmpty() }

        if (english.isNotEmpty() && uzbek.isNotEmpty()) {
            val word = Word(
                english = english,
                uzbek = uzbek,
                example = example ?: "",
                memorized = false,
                partOfSpeech = partOfSpeech ?: ""
            )

            dbHelper.addWord(word)
            showToast("So‘z muvaffaqiyatli qo‘shildi")
            clearInputs()
            hideKeyboard(binding.root)
        } else {
            showToast("Iltimos, English va Uzbek maydonlarini to‘ldiring")
        }
    }

    private fun clearInputs() {
        binding.editEnglish.text.clear()
        binding.editUzbek.text.clear()
        binding.editExample.text.clear()
        binding.editPart.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}
