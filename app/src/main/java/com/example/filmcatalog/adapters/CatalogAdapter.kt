package com.example.filmcatalog.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filmcatalog.models.Movie
import com.example.filmcatalog.R

class CatalogAdapter(context: Context, movies: ArrayList<Movie>) :
    ArrayAdapter<Movie>(context, R.layout.grid_item, movies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val movie = getItem(position)!!

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)!!
        }

        val gridCaption = view.findViewById<TextView>(R.id.grid_caption)
        val gridImage = view.findViewById<ImageView>(R.id.grid_image)

        if (movie.pictureNames.isNotEmpty()) {
            Glide.with(context)
                .load(Uri.parse(movie.pictureNames[0]))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(gridImage)
        }

        gridCaption.text = movie.name + "\n"

        return view
    }
}