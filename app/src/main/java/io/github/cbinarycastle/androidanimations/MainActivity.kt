package io.github.cbinarycastle.androidanimations

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.cbinarycastle.androidanimations.databinding.ActivityMainBinding
import io.github.cbinarycastle.androidanimations.databinding.ItemTextBinding
import io.github.cbinarycastle.androidanimations.util.layoutInflater

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews() {
        binding.recyclerView.adapter = TextAdapter().apply {
            val items = (1..100).map { it.toString() }
            submitList(items)
        }
    }
}

private class TextAdapter : ListAdapter<String, TextViewHolder>(TextDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val binding = ItemTextBinding.inflate(
            parent.context.layoutInflater,
            parent,
            false
        )
        return TextViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class TextViewHolder(
    private val binding: ItemTextBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.text = text
    }
}

private object TextDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}