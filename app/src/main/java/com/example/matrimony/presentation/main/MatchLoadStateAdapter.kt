package com.example.matrimony.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.matrimony.R

class MatchLoadStateAdapter(
    private val onRetry: () -> Unit
) : LoadStateAdapter<MatchLoadStateAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_load_state_footer, parent, false)
        return VH(view, onRetry)
    }

    override fun onBindViewHolder(holder: VH, loadState: LoadState) {
        holder.bind(loadState)
    }

    class VH(itemView: View, onRetry: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val progress: ProgressBar = itemView.findViewById(R.id.load_state_progress)
        private val errorMsg: TextView = itemView.findViewById(R.id.load_state_error)
        private val retry: Button = itemView.findViewById(R.id.load_state_retry)

        init { retry.setOnClickListener { onRetry() } }

        fun bind(loadState: LoadState) {
            val isLoading = loadState is LoadState.Loading
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            retry.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            errorMsg.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            if (loadState is LoadState.Error) {
                errorMsg.text = loadState.error.localizedMessage
            }
        }
    }
}


