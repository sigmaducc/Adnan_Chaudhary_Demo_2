package com.example.matrimony.core.images

import android.widget.ImageView
import com.bumptech.glide.Glide
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlideImageLoader @Inject constructor() : ImageLoader {

	override fun load(target: ImageView, url: String?, placeholderResId: Int?) {
		val request = Glide.with(target)
			.load(url)
			.centerCrop()
		if (placeholderResId != null) {
			request.placeholder(placeholderResId)
		}
		request.into(target)
	}
}