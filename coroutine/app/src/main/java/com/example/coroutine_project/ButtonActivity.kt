package com.example.coroutine_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ButtonActivity : AppCompatActivity(), CoroutineScope {

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var cnt: Int = 0
    private var countDownCnt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)
        job = Job()

        findViewById<Button>(R.id.btn_1).apply {
            setOnClickListener {
                btnEvents { Log.d("@@@@@@ ", "btn1 >>>> ") }
            }
        }

        findViewById<ImageView>(R.id.image).apply {
        }

    }

    private fun btnEvents(event: () -> Unit = {}) {
        launch {
            Log.d("@@@@@", "btnEvents start launch ")
            event()
            launch {
                10.countDown(++countDownCnt)
            }.join()

            Log.d("@@@@@", "btnEvents after launch join ")

//            for (i in 0..100){
//                delay(1000)
//                Log.d("@@@@ btnEvents" , ">> $cnt")
//                cnt++
//            }
        }
    }

    suspend fun Int.countDown(currentIndex: Int) {
        Log.d("@@@@ countDown" , ">>")
        for (index in this downTo 1) {
            findViewById<TextView>(R.id.tv_cnt).text = "Now Index $currentIndex CountDown $index"
            delay(1000)
        }
        Log.d("@@@@ countDown" , "Now Index $currentIndex Done!")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}