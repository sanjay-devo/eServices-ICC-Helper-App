package com.icc.eserviceshelper

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.icc.eserviceshelper.adapters.CategoryAdapter
import com.icc.eserviceshelper.databinding.ActivityMainBinding
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(emptyList()) { category ->
            val intent = Intent(this, TopicsActivity::class.java)
            intent.putExtra("CATEGORY", category)
            startActivity(intent)
        }
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategories.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        // Show progress when starting to observe
        binding.progressBar.visibility = View.VISIBLE
        
        viewModel.filteredCategories.observe(this) { result ->
            // Data is ready, hide progress
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { categories ->
                if (categories.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerViewCategories.visibility = View.GONE
                    binding.tvEmptyState.text = "No results found"
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerViewCategories.visibility = View.VISIBLE
                    adapter.updateList(categories)
                }
            }.onFailure {
                binding.tvEmptyState.text = "Error: ${it.message}"
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerViewCategories.visibility = View.GONE
            }
        }
    }
}
