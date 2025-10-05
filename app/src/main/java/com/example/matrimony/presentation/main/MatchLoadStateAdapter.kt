package com.example.matrimony.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.matrimony.databinding.ItemLoadStateFooterBinding

class MatchLoadStateAdapter(
    private val onRetry: () -> Unit
) : LoadStateAdapter<MatchLoadStateAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): VH {
        val binding = ItemLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onRetry)
    }

    override fun onBindViewHolder(holder: VH, loadState: LoadState) {
        holder.bind(loadState)
    }

    class VH(
        private val binding: ItemLoadStateFooterBinding,
        onRetry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init { binding.loadStateRetry.setOnClickListener { onRetry() } }

        fun bind(loadState: LoadState) {
            val isLoading = loadState is LoadState.Loading
            binding.loadStateProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loadStateRetry.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.loadStateError.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            if (loadState is LoadState.Error) {
                binding.loadStateError.text = loadState.error.localizedMessage
            }
        }
    }
}


