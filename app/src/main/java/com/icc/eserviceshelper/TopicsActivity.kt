package com.icc.eserviceshelper

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.icc.eserviceshelper.adapters.TopicAdapter
import com.icc.eserviceshelper.databinding.ActivityTopicsBinding
import com.icc.eserviceshelper.models.Category

class TopicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicsBinding
    private lateinit var adapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("CATEGORY", Category::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Category>("CATEGORY")
        }
        
        binding.toolbar.title = category?.title ?: "Topics"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val topics = category?.items?.values?.toList() ?: emptyList()

        adapter = TopicAdapter(topics) { topic ->
            val intent = Intent(this, PdfOptionsActivity::class.java)
            intent.putExtra("TOPIC", topic)
            startActivity(intent)
        }
        binding.recyclerViewTopics.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTopics.adapter = adapter
    }
}
