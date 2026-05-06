package com.icc.eserviceshelper

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icc.eserviceshelper.databinding.ActivityPdfOptionsBinding
import com.icc.eserviceshelper.models.ServiceItem

class PdfOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("TOPIC", ServiceItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ServiceItem>("TOPIC")
        }

        if (topic == null) {
            finish()
            return
        }

        binding.toolbar.title = "Options"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvTopicTitle.text = topic.title

        binding.btnViewPdf.setOnClickListener {
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("PDF_URL", topic.pdf_url)
            intent.putExtra("TITLE", topic.title)
            startActivity(intent)
        }

        binding.btnDownloadPdf.setOnClickListener {
            downloadPdf(topic.pdf_url, topic.title)
        }
    }

    private fun downloadPdf(url: String, title: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription("Downloading PDF guide...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "eServices_ICC_Helper/${title.replace(" ", "_")}.pdf"
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start download: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
