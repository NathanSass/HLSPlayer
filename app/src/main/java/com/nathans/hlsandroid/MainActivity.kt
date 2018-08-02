package com.nathans.hlsandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    lateinit var enterPlayerBtn: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enterPlayerBtn = findViewById(R.id.enterPlayer)
        enterPlayerBtn.setOnClickListener { openAudioPlayer() }
    }

    private fun openAudioPlayer() {
        val i = Intent(this, AudioActivity::class.java)
        startActivity(i)
    }
}
