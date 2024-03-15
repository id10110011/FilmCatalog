package com.example.filmcatalog.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.filmcatalog.R
import com.example.filmcatalog.models.Review

class ReviewsAdapter(context: Context, reviews: ArrayList<Review>) :
    ArrayAdapter<Review>(context, R.layout.grid_item, reviews) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val review = getItem(position)!!

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false)!!
        }

        val author = view.findViewById<TextView>(R.id.review_item_author)
        val rating = view.findViewById<RatingBar>(R.id.review_item_rating)
        val text = view.findViewById<TextView>(R.id.review_item_text)
        val createdDate = view.findViewById<TextView>(R.id.review_item_created_date)

        author.text = review.author
        text.text = review.text
        createdDate.text = review.createdDate
        rating.rating = review.rating

        return view
    }
}