package com.jaber.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.jaber.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var job: Job
    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.jobButton.setOnClickListener {
            if (!::job.isInitialized) { // In case button Not Click Before ==> click it First time "No Running Operation"
                initJob()
            }
            activityMainBinding.jobProgressBar.startJobOrCancel(job)
        }

    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress == 0) { //Start Job
            activityMainBinding.jobButton.text = context.getString(R.string.cancel_job_1)

            CoroutineScope(IO + job).launch {

                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }

                updateJobCompleteTextView("job is complete")
            }
        }
        else { //Cancel or Reset Job
            println("$job is already active. Cancelling...")
            if (job.isActive || job.isCompleted) {
                job.cancel(CancellationException("Resetting job")) //throw msg in "invokeOnCompletion" => initJob function
            }
            initJob()
        }

    }




    private fun initJob() {
        activityMainBinding.jobButton.text = getString(R.string.start_job_1)
        updateJobCompleteTextView("")
        activityMainBinding.jobProgressBar.max = PROGRESS_MAX
        activityMainBinding.jobProgressBar.progress = PROGRESS_START

        job = Job()

        //Error Or Cancelling Case
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "UnKnown cancellation error"
                }
                showToast(msg)
            }
        }

    }

    //Functions to Update Main UI
    private fun showToast(msg: String?) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            activityMainBinding.jobCompleteText.text = text
        }
    }

}
