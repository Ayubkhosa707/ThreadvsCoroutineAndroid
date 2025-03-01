package com.ayub.khosa.threadvscoroutinesandroid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var tv_1: TextView
    lateinit var tv_2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        PrintLogs.printD("Main program starts: ${Thread.currentThread().name}")


        tv_1 = findViewById<TextView>(R.id.textView1)
        imageView1 = findViewById<ImageView>(R.id.imageView1)
        tv_2 = findViewById<TextView>(R.id.textView2)
        imageView2 = findViewById<ImageView>(R.id.imageView2)

        tv_1.setOnClickListener(View.OnClickListener {
            //   performThreadTask()


            performThread2Task()


        })
        tv_2.setOnClickListener(View.OnClickListener {


            performCoroutineTask()
        })


        PrintLogs.printD("Main program ends: ${Thread.currentThread().name}")
    }

    fun performThread2Task() {
        thread {
            // Perform time-consuming task here
            // This code will run in a separate thread
            PrintLogs.printD("Fake work starts: ${Thread.currentThread().name}")

            runBlocking {
                // parent coroutine

                val job: Deferred<Bitmap> = this.async(coroutineContext) {
                    // child coroutine
                    lateinit var image1: Bitmap
                    image1 =
                        mLoad("https://ayubkhosa.com/ecommerce-website-master/images/shoes.jpg")

                    return@async image1
                }
//                job.await() return Bitmap downloaded
                withContext(Dispatchers.Main) {
                    imageView1.setImageBitmap(job.await())
                }


            }


            // Pretend doing some work... may be file upload
            PrintLogs.printD("Fake work finished: ${Thread.currentThread().name}")
        }
    }

    fun performThreadTask() {
        thread {
            // Perform time-consuming task here
            // This code will run in a separate thread
            PrintLogs.printD("Fake work starts: ${Thread.currentThread().name}")


            val image1 = mLoad("https://ayubkhosa.com/ecommerce-website-master/images/shoes.jpg")
            runOnUiThread {
                imageView1.setImageBitmap(image1)
            }
            // Pretend doing some work... may be file upload
            PrintLogs.printD("Fake work finished: ${Thread.currentThread().name}")
        }
    }

    fun performCoroutineTask() {

        GlobalScope.launch(Dispatchers.IO) {
            // Perform time-consuming task here
            // This code will run in a coroutine
            PrintLogs.printD("Coroutine Fake work starts: ${Thread.currentThread().name}")

            val image2 =
                mLoad("https://ayubkhosa.com/ecommerce-website-master/images/watch.jpg")    // Pretend doing some work... may be file upload
            withContext(Dispatchers.Main) {
                imageView2.setImageBitmap(image2);
            }


            PrintLogs.printD("Coroutine Fake work finished: ${Thread.currentThread().name}")
        }
    }


    // Function to establish connection and load image
    private fun mLoad(string: String): Bitmap {

        lateinit var img_bitmap: Bitmap
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            img_bitmap = BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            PrintLogs.printD("mLoad IOException " + e.message)
        }
        return img_bitmap
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            PrintLogs.printD("mStringToURL MalformedURLException " + e.message)
        }
        return null
    }
}