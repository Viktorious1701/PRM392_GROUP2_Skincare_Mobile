package com.example.prm392_group2_skincare_mobile.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prm392_group2_skincare_mobile.R

class PromotionalBannerAdapter(private val bannerImages: List<Int>) :
    RecyclerView.Adapter<PromotionalBannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_banner_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotional_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(bannerImages[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = bannerImages.size
}