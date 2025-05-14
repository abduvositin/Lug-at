package com.abduvosit.devoloper.lugat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.abduvosit.devoloper.lugat.databinding.ActivityMainBinding
import com.abduvosit.devoloper.lugat.fragment.YodlanganFragment
import com.abduvosit.devoloper.lugat.fragment.YodlanmaganFragment
import com.abduvosit.devoloper.lugat.shared.SettingsManager

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val updateChecker = UpdateChecker(this)
        updateChecker.checkForUpdate()

        val settings = SettingsManager(this)
        initializeSettings(settings)

        setupListeners(settings)
        showFragment(YodlanmaganFragment(), binding.btnYodlanmagan, binding.btnYodlangan)
    }

    private fun setupListeners(settings: SettingsManager) {
        binding.setLongLangButton.setOnClickListener {
            toggleLanguage(settings)
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnYodlanmagan.setOnClickListener {
            showFragment(YodlanmaganFragment(), binding.btnYodlanmagan, binding.btnYodlangan)
        }

        binding.btnYodlangan.setOnClickListener {
            showFragment(YodlanganFragment(), binding.btnYodlangan, binding.btnYodlanmagan)
        }

        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun initializeSettings(settings: SettingsManager) {
        val isFirstRun = settings.getSetting("isFirstRun", "true")

        if (isFirstRun == "true") {
            settings.apply {
                saveSetting("search", "all")
                saveSetting("theme", "light")
                saveSetting("language", "uzbek")
                saveSetting("wordLongLang", "UZ")
                saveSetting("isFirstRun", "false")
            }
        }
        binding.setLongLangButton.text = settings.getSetting("wordLongLang", "UZ")
    }

    @SuppressLint("SetTextI18n")
    private fun toggleLanguage(settings: SettingsManager) {
        val currentLang = settings.getSetting("wordLongLang", "UZ")
        val newLang = if (currentLang == "UZ") "EN" else "UZ"

        if (currentLang == "UZ") {
            binding.setLongLangButton.text = "EN"
        } else {
            binding.setLongLangButton.text = "UZ"
        }
        settings.saveSetting("wordLongLang", newLang)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.linear_layout_content)
        when (currentFragment) {
            is YodlanmaganFragment -> showFragment(
                YodlanmaganFragment(),
                binding.btnYodlanmagan,
                binding.btnYodlangan
            )

            is YodlanganFragment -> showFragment(
                YodlanganFragment(),
                binding.btnYodlangan,
                binding.btnYodlanmagan
            )
        }
    }

    private fun showFragment(fragment: Fragment, textSet: TextView, textNoSet: TextView) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.linear_layout_content, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        textSet.setTextColor(ContextCompat.getColor(this, R.color.black))
        textNoSet.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(
                this,
                "Ilovadan chiqish uchun yana ikki marta bosing",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
