package com.android.myapplication.newsfeed.ui.headlines

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.ui.headlines.viewmodel.*
import com.android.myapplication.newsfeed.util.SourcesCategoriesAndCountries
import com.bumptech.glide.RequestManager
import java.nio.channels.Pipe
import javax.inject.Inject

class HeadlineFragment : BaseHeadlineFragment(), HeadlinesListAdapter.Interaction,SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var headlinesAdapter: HeadlinesListAdapter
    private  var recyclerView : RecyclerView?=null
    private  var tv_error:TextView?=null
    private lateinit var searchView:androidx.appcompat.widget.SearchView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_headlines, container, false)
        recyclerView = view.findViewById(R.id.rv_headlines)
        tv_error = view.findViewById(R.id.tv_error)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        initRV()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        ) //BaseHeadlineFragment implementation and Fragment()
        subscribeObservers()
        executeRequest()
    }
    private fun executeRequest() {
        // no point of firing the event everytime we rotate or change graph
        //as long as the viewModel is alive, no need to re-fire
        //viewModel life span is tied to the MainActivity (store)
        //  queryEvent will be held in the viewModel and set to true when the viewModel is first created
        // when we rotate , the queryEvent liveData will still hold the same object/event, which is already been handled
        // NOTE: even though the DataState member variables are wrapped in event,
        // it will be refreshed because its event object are being updated, in the networkBoundResources and repositories every time we fire this request
        viewModel.executeQueryEvent.observe(viewLifecycleOwner, Observer { queryEvent->
                queryEvent.getContentIfNotHandled()?.let { //only proceed if this query has never been handled
                    Log.d(TAG, "HeadlineFragment: executeQueryEvent: $queryEvent")
                    with(viewModel) {
                        with(getVSHeadlines()) {
                            loadFirstPage(country, category)
                        }
                    }
                }
            })
    }

    private fun executeSearchQuery(query:String){
        with(viewModel){
            with(getVSHeadlines()){
                loadFirstPage(country,category,query)
            }
        }
        resetUI()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let {
                stateChangeListener?.onDataStateChange(dataState) //Listener(BaseActivity/Activity) will handle the loading (progress bar), error dialog/toast, data response msg (in this app is always null)
                handlePagination(dataState)
            }
        })

        //As soon as the HeadlineFrag is created , it will receive the viewState (if available) in the ViewModel
        // and everTime the viewState is changed , we update the ui
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewModelViewState ->
            Log.d(TAG, "HeadlineFragment: viewState observer: ${viewModelViewState}")
            viewModelViewState?.let {
                headlinesAdapter.submitList(
                    list = it.headlinesFields.headlinesList, //could be empty or not
                    isQueryExhausted = viewModelViewState.headlinesFields.isQueryExhausted
                )

                //only show error screen if the list is empty and the error message is not empty
                //because if the user retrieved the list successfully, turns the wifi off and then pull to refresh, we dont want to show the error screen on top of the list
                if(it.headlinesFields.headlinesList.isNullOrEmpty() && !it.headlinesFields.errorScreenMsg.isEmpty()){
                    tv_error!!.visibility = View.VISIBLE
                    tv_error!!.text = it.headlinesFields.errorScreenMsg
                }else{
                    tv_error!!.visibility = View.GONE
                }
            }
        })
    }
    private fun handlePagination(dataState:DataState<HeadlinesViewState>){
        //this component handle the data data

        //we update the viewstate field 'QueryInProgress'
        //so we can be able to fire another request when we need to , and prevent another request when its currently loading
        viewModel.updateViewState { headlinesFields->
            headlinesFields.isQueryInProgress = dataState.loading.isLoading
        }

        dataState.data?.let {
            it.data?.let { eventViewState ->
                eventViewState.getContentIfNotHandled()?.let { networkViewState ->
                    Log.d(TAG, "HeadlineFragment: dataStateReturned: with data!=null, updating headlinesList")
                    //we are updating a field in the viewState, which will update the viewState itself
                    // and fire observers
                    viewModel.handlePaginationSuccessResult(networkViewState)
                }
            }
        }
        dataState.error?.let { errorEvent ->
            //handle the error if not null
            //if the errorEvent hasNotBeenHandled, update the view state to update the ui, otherwise do nothing
            errorEvent.getContentIfNotHandled()?.let{ stateError->
                Log.d(TAG, "HeadlineFragment: dataStateReturned: with error!=null, updating errorMsgScreen")
                viewModel.updateViewState { headlinesFields->
                    headlinesFields.errorScreenMsg = stateError.response.message?:""
                }
            }
        }
    }

    private fun initRV() {
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@HeadlineFragment.context)
            headlinesAdapter = HeadlinesListAdapter(this@HeadlineFragment, requestManager)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == headlinesAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "HeadlineFragment: load next page...")
                        viewModel.loadNextPage()
                    }
                }
            })
            adapter = headlinesAdapter
        }
    }
    private fun  initSV(menu: Menu){
        activity?.apply {
            val searchManager = getSystemService((SEARCH_SERVICE)) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as androidx.appcompat.widget.SearchView
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH){
                val searchQuery = v.text.toString()
                executeSearchQuery(searchQuery)
            }
            true
        }
        (searchView.findViewById(R.id.search_go_btn) as View).setOnClickListener {
            val searchQuery = searchPlate.text.toString()
          executeSearchQuery(searchQuery)
        }
    }

    private fun resetUI(){
        //scroll the recyclerview to position zero
        recyclerView?.smoothScrollToPosition(0)
        //stateChangeListener is the activity or the base activity
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView!!.adapter = null //to avoid memory leak
        tv_error = null

    }

    override fun onItemSelected(position: Int, item: Article) {
        Log.d(TAG, "onItemSelected: position,article: ${position}, ${item} ")
        fireIntent(item)
    }

    private fun fireIntent(item: Article) {
        val url = item.url
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)
        initSV(menu)
    }

    override fun onRefresh() {
        with(viewModel){
            with(getVSHeadlines()){
                loadFirstPage(country,category,searchQuery)
            }
        }
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
            R.id.action_filter_settings->{
                openCountryOptions()
                true
            }
            else->{
                super.onOptionsItemSelected(item)
            }
        }

    private fun openCountryOptions(){
        activity?.let {
            val dialog = MaterialDialog(it).noAutoDismiss().customView(R.layout.layout_articles_country)
            dialog.getCustomView().run{
                val selectedCountry = viewModel.getVSHeadlines().country
                with(findViewById<RadioGroup>(R.id.country_options)){
                    if(selectedCountry.equals(SourcesCategoriesAndCountries.AUSTRALIA)){
                        check(R.id.australia_option)
                    }else{
                       check(R.id.united_state_option)
                    }
                }
                findViewById<TextView>(R.id.positive_button).setOnClickListener {
                    val newSelectedCountry = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.country_options).checkedRadioButtonId)
                    var countryDefault = SourcesCategoriesAndCountries.AUSTRALIA
                    if(newSelectedCountry.text.toString().equals(getString(R.string.united_states))){

                        countryDefault = SourcesCategoriesAndCountries.USA

                    }
                    Log.d(TAG, "openCountryOptions: countryDefault $countryDefault")

                       viewModel.updateViewState { field->
                            field.country = countryDefault
                            field.searchQuery = EMPTY_STRING
                        }

                    with(viewModel.getVSHeadlines()){
                        Log.d(TAG, "openCountryOptions: $country, $category")
                        viewModel.loadFirstPage(country,category)
                    }
                    dialog.dismiss()
                }
                findViewById<TextView>(R.id.negative_button).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        with(viewModel){
            with(getVSHeadlines()){
                saveCategoryAndCountry(country,category)
            }
        }

    }
}