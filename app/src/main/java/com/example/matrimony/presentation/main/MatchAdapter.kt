package com.example.matrimony.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.example.matrimony.R
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User

class MatchAdapter(
    private val onAccept: (User) -> Unit,
    private val onDecline: (User) -> Unit
) : PagingDataAdapter<User, MatchAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = getItem(position) ?: return
        holder.bind(user, onAccept, onDecline)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnDecline: Button = itemView.findViewById(R.id.btnDecline)

        fun bind(user: User, onAccept: (User) -> Unit, onDecline: (User) -> Unit) {
            Glide.with(image)
                .load(user.imageLargeUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(image)
            name.text = "${user.fullName}, ${user.age}"
            location.text = "${user.city}, ${user.state}, ${user.country}"

            when (user.decision) {
                MatchDecision.PENDING -> {
                    status.visibility = View.GONE
                    btnAccept.visibility = View.VISIBLE
                    btnDecline.visibility = View.VISIBLE
                }
                MatchDecision.ACCEPTED -> {
                    status.visibility = View.VISIBLE
                    status.text = "Accepted"
                    status.setBackgroundResource(R.drawable.status_chip_background)
                    btnAccept.visibility = View.GONE
                    btnDecline.visibility = View.GONE
                }
                MatchDecision.DECLINED -> {
                    status.visibility = View.VISIBLE
                    status.text = "Declined"
                    status.setBackgroundResource(R.drawable.status_chip_background)
                    btnAccept.visibility = View.GONE
                    btnDecline.visibility = View.GONE
                }
            }

            btnAccept.setOnClickListener { onAccept(user) }
            btnDecline.setOnClickListener { onDecline(user) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
        }
    }
}