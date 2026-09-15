package com.example.kbstudio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Xin quyen MIC ngay khi mo app
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF111111.toInt())
            setPadding(60, 60, 60, 60)
        }

        root.addView(TextView(this).apply {
            text = "⌨️ KbStudio"
            setTextColor(0xFFFF9800.toInt())
            textSize = 34f
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Bàn phím tùy biến 100%"
            setTextColor(0xFF888888.toInt())
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 50)
        })

        root.addView(button("1. Bật KbStudio trong hệ thống") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            Toast.makeText(this, "Bật KbStudio, rồi quay lại bước 2", Toast.LENGTH_LONG).show()
        })
        root.addView(button("2. Chọn bàn phím KbStudio") {
            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showInputMethodPicker()
        })
        root.addView(button("3. Tùy chỉnh giao diện") {
            startActivity(Intent(this, SettingsActivity::class.java))
        })

        setContentView(root)
    }

    private fun button(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 15f
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        }
    }
}
