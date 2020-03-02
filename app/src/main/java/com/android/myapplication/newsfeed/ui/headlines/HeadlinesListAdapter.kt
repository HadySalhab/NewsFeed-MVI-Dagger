package com.android.myapplication.newsfeed.ui.headlines

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.util.GenericViewHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import kotlinx.android.synthetic.main.layout_headlines_list_item.view.*

class HeadlinesListAdapter(
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG: String = "AppDebug"
    private val NO_MORE_RESULTS = -1
    private val HEADLINE_ITEM = 0
    private val NO_MORE_RESULTS_HEADLINE_MARKER = Article(
        id = NO_MORE_RESULTS.toLong()
    )
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.equals(newItem)
        }

    }

    internal inner class HeadlinesRVChangeCallback(
        private val adapter: HeadlinesListAdapter
    ) : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload) //default
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged() //reset completely
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count) //default
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged() //reset the list completely
        }

    }

    private val differ = AsyncListDiffer(
        HeadlinesRVChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return   when(viewType){
            NO_MORE_RESULTS -> {
               GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }
            HEADLINE_ITEM->{
                HeadlinesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_headlines_list_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }
            else->{
                HeadlinesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_headlines_list_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeadlinesViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList[position].id > -1){
            return HEADLINE_ITEM //by default, headlines will have an id of at least 0
        }
        return NO_MORE_RESULTS //NO_MORE_RESULTS_HEADLINE_MARKER have an id of -1
    }

    fun submitList(list: List<Article>?, isQueryExhausted:Boolean) {
        val newList = list?.toMutableList()
        if(isQueryExhausted){ //when query is exhausted show the  NO_MORE_RESULTS_HEADLINE_MARKER
            newList?.add(NO_MORE_RESULTS_HEADLINE_MARKER) //APPEND TO THE SUBMITTED LIST, THE NO MORE RESULT LIST ITEM
        }
        differ.submitList(list)
    }

    class HeadlinesViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Article) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            requestManager
                .load(item.urlToImage)
                .transition(withCrossFade())
                .into(itemView.iv_article_image)
            itemView.apply {
                tv_article_author.text = item.author
                tv_article_date.text = com.android.myapplication.newsfeed.util.DateUtils.formatDate(item.publishDate)
                tv_article_description.text = item.description
                tv_article_title.text =item.title
                tv_article_source_name.text = item.source?.name
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Article)
    }
}