package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codesroots.osamaomar.shopgate.R
import kotlinx.android.synthetic.main.fragment_description.*

class DescriptionFragment() : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_description, container, false)

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product_description .setMovementMethod( ScrollingMovementMethod())
        val  description = arguments?.getString("description")
        product_description.text =         Html.fromHtml(description)


    }

}
