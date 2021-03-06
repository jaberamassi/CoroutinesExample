package com.jaber.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jaber.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    val TIMED_OUT = 2100L

    override fun onCreate(savedInstanceState: Bundle?) {
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    // Move Result from coroutineScope to Main Thread Method
    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    //Regular Change UI Method
    private fun setNewText(input: String) {
        val newText = activityMainBinding.tv.text.toString() + "\n$input"
        activityMainBinding.tv.text = newText
    }

    private suspend fun fakeApiRequest() {
        withContext(IO){
            /*
            val job = launch{
                val result1 = getResult1FromApi()
                Log.i("debug","result1 :$result1")
                setTextOnMainThread("Got $result1")

                val result2 = getResult2FromApi()
                Log.i("debug","result2 :$result2")
                setTextOnMainThread("Got $result2")
            }

             */

            val job = withTimeoutOrNull(TIMED_OUT){
                val result1 = getResult1FromApi() // take time 1000ms
                Log.i("debug","result1 :$result1")
                setTextOnMainThread("Got $result1")

                val result2 = getResult2FromApi()// take time 1000ms
                Log.i("debug","result2 :$result2")
                setTextOnMainThread("Got $result2")
            }
            if (job == null){
                val message = "Cancelling job..Job took longer than $TIMED_OUT ms"
                setTextOnMainThread(message)
            }
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000) // Does not block thread. Just suspends the coroutine inside the thread
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1000)
        return "Result #2"
    }



}
