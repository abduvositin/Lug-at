package com.abduvosit.devoloper.lugat

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.core.net.toUri

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UpdateChecker(private val activity: Activity) {

    private val jsonUrl = "https://raw.githubusercontent.com/abduvositin/update_lugat/main/update.json"
    private val currentVersion = "1.0.0"

    fun checkForUpdate() {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(jsonUrl).build()
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData)

                val latestVersion = jsonObject.getString("latest_version")
                val changelog = jsonObject.getString("changelog")
                val apkUrl = jsonObject.getString("apk_url")

                if (latestVersion != currentVersion) {
                    activity.runOnUiThread {
                        showUpdateDialog(changelog, apkUrl)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showUpdateDialog(changelog: String, apkUrl: String) {
        AlertDialog.Builder(activity)
            .setTitle("Yangi versiya mavjud")
            .setMessage("Yangiliklar:\n\n$changelog")
            .setPositiveButton("Yuklab olish") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, apkUrl.toUri())
                activity.startActivity(intent)
            }
            .setNegativeButton("Bekor qilish", null)
            .setCancelable(false)
            .show()


    }
}
