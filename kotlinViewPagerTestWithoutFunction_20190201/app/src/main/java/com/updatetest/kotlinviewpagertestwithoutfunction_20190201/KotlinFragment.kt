package com.updatetest.kotlinviewpagertestwithoutfunction_20190201

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class KotlinFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View? = inflater.inflate(R.layout.kotlin_fragment, container, false)

        var imageView : ImageView? = view?.findViewById(R.id.kotlinfragment_imageview)
        imageView?.setImageResource(arguments!!.getInt("image"))

        return view
    }
}