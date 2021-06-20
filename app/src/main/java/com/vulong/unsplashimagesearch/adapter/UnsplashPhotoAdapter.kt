package com.vulong.unsplashimagesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vulong.unsplashimagesearch.R
import com.vulong.unsplashimagesearch.data.Photo


class UnsplashPhotoAdapter(
    private val mItemList: ArrayList<Photo?>,
    private val onItemRecyclerViewClickListener: OnItemRecyclerViewClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.loading_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            //bind view for item photo
            populateItemRows(holder as ItemViewHolder?, position)
        } else if (holder is LoadingViewHolder) {
            //bind view for loading bar
            showLoadingView(holder as LoadingViewHolder?, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mItemList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePhoto: ImageView = itemView.findViewById(R.id.image_unsplash)
        val textUsername: TextView = itemView.findViewById(R.id.text_username)
        val context: Context = itemView.context
        val layout: ConstraintLayout = itemView.findViewById(R.id.layout_photo)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById(R.id.load_more_progress_bar)
//        var textView: TextView = itemView.findViewById(R.id.text_load_more)
    }

    private fun showLoadingView(
        loadingViewHolder: UnsplashPhotoAdapter.LoadingViewHolder?,
        position: Int
    ) {
        //ProgressBar would be displayed, can be custom data for this view type
    }

    private fun populateItemRows(
        itemViewHolder: UnsplashPhotoAdapter.ItemViewHolder?,
        position: Int
    ) {
        val unsplashPhoto = mItemList[position]

        Glide.with(itemViewHolder!!.context).load(unsplashPhoto!!.urls.small)
            .into(itemViewHolder.imagePhoto);
        itemViewHolder.textUsername.text = unsplashPhoto.user.username

        itemViewHolder.layout.setOnClickListener {
            onItemRecyclerViewClickListener.onClickItemRecyclerView(position)
        }

    }

    interface OnItemRecyclerViewClickListener {
        fun onClickItemRecyclerView(position: Int)
    }

}
