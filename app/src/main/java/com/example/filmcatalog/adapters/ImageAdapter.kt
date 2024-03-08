package com.example.filmcatalog.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filmcatalog.R


class ImageAdapter :
    ListAdapter<String, ImageAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewpager_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = getItem(position)
        holder.bindData(url)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(iteView: View) : RecyclerView.ViewHolder(iteView) {
        private val imageView = iteView.findViewById<ImageView>(R.id.view_pager_image_view)

        fun bindData(url: String) {
            Glide.with(itemView)
                .load(Uri.parse(url))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView)
        }
    }

}