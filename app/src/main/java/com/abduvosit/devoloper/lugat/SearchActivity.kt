package com.abduvosit.devoloper.lugat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.abduvosit.devoloper.lugat.adapter.WordAdapter
import com.abduvosit.devoloper.lugat.databinding.ActivitySearchBinding
import com.abduvosit.devoloper.lugat.shared.SettingsManager
import com.abduvosit.devoloper.lugat.sql.DatabaseHelper

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: WordAdapter
    private lateinit var settings: SettingsManager
    private lateinit var searchSettings: String
    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = SettingsManager(this)
        searchSettings = settings.getSetting("search", "all")
        language = settings.getSetting("language", "english")

        setupDatabase()
        setupRecyclerView()
        setupListeners()
        showKeyboard(binding.searchText)

        binding.exitIcon.setOnClickListener { finish() }
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        val wordList = dbHelper.getAllWords()
        adapter = WordAdapter(wordList, dbHelper, false, settings.getSetting("wordLogLang", "UZ"))
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.addMore.setOnClickListener { showSettingsDialog() }

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchWords(s.toString(), searchSettings)
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.item_search_settings, null)
        val check = dialogView.findViewById<CheckBox>(R.id.all)
        val check1 = dialogView.findViewById<CheckBox>(R.id.onlyEnglish)
        val check2 = dialogView.findViewById<CheckBox>(R.id.onlyUzbek)

        check.isChecked = searchSettings == "all"
        check1.isChecked = searchSettings == "english"
        check2.isChecked = searchSettings == "uzbek"

        val updateChecks: (CheckBox, CheckBox, CheckBox) -> Unit = { c1, c2, c3 ->
            c1.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    c2.isChecked = false
                    c3.isChecked = false
                }
            }
            c2.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    c1.isChecked = false
                    c3.isChecked = false
                }
            }
            c3.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    c1.isChecked = false
                    c2.isChecked = false
                }
            }
        }

        // Apply helper function
        updateChecks(check, check1, check2)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("Saqlash") { _, _ ->
                searchSettings = when {
                    check.isChecked -> "all"
                    check1.isChecked -> "english"
                    check2.isChecked -> "uzbek"
                    else -> searchSettings
                }
                settings.saveSetting("search", searchSettings)
            }
            .setNegativeButton("Bekor qilish", null)
            .create()

        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.rounded_dialog))
        alertDialog.show()
    }

    private fun searchWords(query: String, lang: String) {
        val filteredList = dbHelper.searchWords(query, lang)
        binding.recyclerViewNoResult.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        adapter.updateList(filteredList)
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
