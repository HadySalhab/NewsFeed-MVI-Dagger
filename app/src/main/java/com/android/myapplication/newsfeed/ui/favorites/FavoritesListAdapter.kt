package com.android.myapplication.newsfeed.ui.favorites

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.util.formatStringDate
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import kotlinx.android.synthetic.main.layout_headlines_list_item.view.*

class FavoritesListAdapter(
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url.equals(newItem.url)
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.equals(newItem)
        }

    }

    internal inner class HeadlinesRVChangeCallback(
        private val adapter: FavoritesListAdapter
    ) : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?)
                = adapter.notifyItemRangeChanged(position, count, payload) //default


        override fun onMoved(fromPosition: Int, toPosition: Int) = adapter.notifyDataSetChanged() //reset completely


        override fun onInserted(position: Int, count: Int) = adapter.notifyItemRangeChanged(position, count) //default


        override fun onRemoved(position: Int, count: Int) = adapter.notifyDataSetChanged() //reset the list completely


    }

    private val differ = AsyncListDiffer(
        HeadlinesRVChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return   HeadlinesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_headlines_list_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )

            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeadlinesViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount() = differ.currentList.size


    fun submitList(list: List<Article>?) = differ.submitList(list)


    class HeadlinesViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val TAG: String = "AppDebug"

        fun bind(item: Article) = with(this@HeadlinesViewHolder.itemView) {
            itemView.setOnClickListener {
                Log.d(TAG, "HeadlinesViewHolder itemView clicked...")
                interaction?.onItemSelected(adapterPosition, item)
            }
            iv_share_image.setOnClickListener {
                interaction?.onShareIconClick(item)
            }
            cb_favorite_image.apply {
                setOnClickListener {
                    interaction?.onFavIconClicked(isChecked,item)
                }
            }
            requestManager
                .load(item.urlToImage)
                .transition(withCrossFade())
                .into(itemView.iv_article_image)
            itemView.apply {
                tv_article_author.text = item.author
                tv_article_date.text = item.publishDate?.formatStringDate()
                tv_article_description.text = item.description
                tv_article_title.text =item.title
                tv_article_source_name.text = item.source?.name
                cb_favorite_image.isChecked = item.isFavorite
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Article)
        fun onFavIconClicked(isFavorite:Boolean,item:Article)
        fun onShareIconClick(item:Article)
    }
}