package com.example.coroutine_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ButtonActivity : AppCompatActivity(), CoroutineScope {

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)
        job = Job()

        findViewById<Button>(R.id.btn_1).apply {
            setOnClickListener {
                btnEvents { Log.d("@@@@@@ ", "btn1 >>>> ") }
            }
        }

        findViewById<Button>(R.id.btn_2).apply {
            setOnClickListener { Log.d("@@@@@@ ", "btn2 >>>> ") }
        }

        findViewById<ImageView>(R.id.image).apply {
        }
    }

    private fun btnEvents(event: () -> Unit = {}) {
        launch {
            event()
            delay(500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}