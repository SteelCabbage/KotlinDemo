package com.example.kotlindemo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testRequestPermission()
    }

    private fun testRequestPermission() {
        val tv = findViewById<TextView>(R.id.tv)
        val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.e("baicai", "registerForActivityResult it=$it")
        }
        tv.setOnClickListener {
            registerForActivityResult.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }
}