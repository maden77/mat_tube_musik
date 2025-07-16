package com.mattube.musik

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mattube.musik.databinding.ActivityMainBinding
import java.io.*
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val downloadFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath + "/mat tube/"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Izin dibutuhkan untuk menyimpan file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide() // Hapus toolbar
        checkPermission()

        binding.btnDownloadMp3.setOnClickListener {
            val url = binding.editUrl.text.toString()
            if (url.isNotEmpty()) {
                downloadFile(url, "mp3")
            }
        }

        binding.btnDownloadMp4.setOnClickListener {
            val url = binding.editUrl.text.toString()
            if (url.isNotEmpty()) {
                downloadFile(url, "mp4")
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun downloadFile(fileUrl: String, ext: String) {
        val fileName = "mat_music_${System.currentTimeMillis()}.$ext"
        val folderPath = "$downloadFolder$ext/"
        val filePath = "$folderPath$fileName"

        File(folderPath).mkdirs()

        Toast.makeText(this, "Mengunduh...", Toast.LENGTH_SHORT).show()

        thread {
            try {
                val url = URL(fileUrl)
                val conn = url.openConnection()
                val input = BufferedInputStream(conn.getInputStream())
                val output = FileOutputStream(filePath)

                val data = ByteArray(1024)
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                runOnUiThread {
                    Toast.makeText(this, "Tersimpan: $fileName", Toast.LENGTH_LONG).show()
                }

                MediaScannerConnection.scanFile(this, arrayOf(filePath), null, null)
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Gagal mengunduh", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
