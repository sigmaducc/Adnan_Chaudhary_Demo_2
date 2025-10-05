package com.example.matrimony.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matrimony.R
import com.example.matrimony.core.images.ImageLoader
import com.example.matrimony.databinding.ItemMatchBinding
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User

class MatchAdapter(
    private val imageLoader: ImageLoader,
    private val onAccept: (User) -> Unit,
    private val onDecline: (User) -> Unit
) : PagingDataAdapter<User, MatchAdapter.UserViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
        holder.bind(user, onAccept, onDecline)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.contains("decision")) {
            val user = getItem(position) ?: return
            holder.bindDecisionOnly(user.decision)
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    class UserViewHolder(
        private val binding: ItemMatchBinding,
        private val imageLoader: ImageLoader
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, onAccept: (User) -> Unit, onDecline: (User) -> Unit) {
            imageLoader.load(
                target = binding.image,
                url = user.imageLargeUrl,
                placeholderResId = R.drawable.ic_launcher_foreground
            )

            binding.name.text = binding.root.context.getString(
                R.string.user_name_with_age, user.fullName, user.age
            )
            binding.location.text = binding.root.context.getString(
                R.string.user_location_format, user.city, user.state, user.country
            )
            bindDecisionOnly(user.decision)

            binding.btnAccept.setOnClickListener { onAccept(user) }
            binding.btnDecline.setOnClickListener { onDecline(user) }
        }

        fun bindDecisionOnly(decision: MatchDecision) {
            when (decision) {
                MatchDecision.PENDING -> {
                    binding.status.visibility = View.GONE
                    binding.btnAccept.visibility = View.VISIBLE
                    binding.btnDecline.visibility = View.VISIBLE
                }
                MatchDecision.ACCEPTED -> {
                    binding.status.visibility = View.VISIBLE
                    binding.status.text = binding.root.context.getString(R.string.status_accepted)
                    binding.status.setBackgroundResource(R.drawable.status_chip_background_accepted)
                    binding.btnAccept.visibility = View.INVISIBLE
                    binding.btnDecline.visibility = View.INVISIBLE
                }
                MatchDecision.DECLINED -> {
                    binding.status.visibility = View.VISIBLE
                    binding.status.text = binding.root.context.getString(R.string.status_declined)
                    binding.status.setBackgroundResource(R.drawable.status_chip_background_declined)
                    binding.btnAccept.visibility = View.INVISIBLE
                    binding.btnDecline.visibility = View.INVISIBLE
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
            override fun getChangePayload(oldItem: User, newItem: User): Any? {
                return if (oldItem.id == newItem.id && oldItem.decision != newItem.decision) "decision" else null
            }
        }
    }
}