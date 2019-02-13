package com.updatetest.kotlinviewpagertest_20190131

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ViewPagerFragment : Fragment() {
    fun loadImage(image : Int?) : Fragment {
        var args : Bundle? = Bundle()
        args?.putInt("image", image!!)

        var fragment : ViewPagerFragment? = ViewPagerFragment()
        fragment?.arguments = args
        return fragment!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.viewpagerfragment, container, false)

        var imageView : ImageView? = view.findViewById(R.id.viewpager_imageview)
        imageView?.setImageResource(arguments!!.getInt("image"))
        return view
    }
}