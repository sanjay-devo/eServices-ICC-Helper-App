package com.icc.eserviceshelper

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.icc.eserviceshelper.databinding.ActivityPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        val title = intent.getStringExtra("TITLE")

        binding.toolbar.title = title ?: "PDF Viewer"
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (pdfUrl != null) {
            loadPdf(pdfUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadPdf(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Create a unique filename based on the URL hash
                val fileName = urlToFileName(url)
                val localFile = File(cacheDir, fileName)

                if (!localFile.exists()) {
                    // Download and save to cache if not exists
                    downloadFile(url, localFile)
                }

                // Load from local file
                withContext(Dispatchers.Main) {
                    displayPdfFromFile(localFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PdfViewerActivity, "Failed to load PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadFile(url: String, destination: File) {
        val inputStream = URL(url).openStream()
        val outputStream = FileOutputStream(destination)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun displayPdfFromFile(file: File) {
        binding.pdfView.fromFile(file)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            .onLoad {
                binding.progressBar.visibility = View.GONE
            }
            .onError { t ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@PdfViewerActivity, "Error displaying PDF: ${t.message}", Toast.LENGTH_LONG).show()
                // If it's corrupted, delete it so next time it redownloads
                if (file.exists()) file.delete()
            }
            .load()
    }

    private fun urlToFileName(url: String): String {
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(url.toByteArray())
            bytes.joinToString("") { "%02x".format(it) } + ".pdf"
        } catch (e: Exception) {
            // Fallback to basic sanitization if MD5 fails
            url.filter { it.isLetterOrDigit() }.takeLast(20) + ".pdf"
        }
    }
}
