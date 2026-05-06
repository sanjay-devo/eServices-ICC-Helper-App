package com.icc.eserviceshelper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icc.eserviceshelper.databinding.ItemTopicBinding
import com.icc.eserviceshelper.models.ServiceItem

class TopicAdapter(
    private val topics: List<ServiceItem>,
    private val onItemClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: ServiceItem) {
            binding.tvTopicTitle.text = topic.title
            binding.root.setOnClickListener { onItemClick(topic) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size
}
