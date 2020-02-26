package com.android.myapplication.newsfeed.ui.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.myapplication.newsfeed.R
import kotlinx.android.synthetic.main.fragment_sources.*

class SourcesFragment : BaseSourcesFragment (){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_sources,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_nav.setOnClickListener {
            findNavController().navigate(R.id.action_sourcesFragment_to_articlesSourceFragment)
        }
    }
}