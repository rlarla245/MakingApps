package com.updatetest.kotlintest_20190129

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.updatetest.kotlintest_20190129.R.id.mainactivity_recyclerview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        var recyclerView: RecyclerView? = findViewById(R.id.mainactivity_recyclerview)
        var gridLayoutManager: GridLayoutManager? = GridLayoutManager(this, 3)
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) {
                    return 3
                }
                return 1
            }
        }

        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = RecyclerViewViewAdapter()
    }
}

class RecyclerViewViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var images: Array<Int> = arrayOf(R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5)

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 3
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        if (viewType == 3) {
            view.layoutParams.height = parent.measuredWidth / 3 * 2
            view.layoutParams.width = parent.measuredWidth
            return CustomViewHolder(view)
        }
        view.layoutParams.height = parent.measuredWidth / 3
        view.layoutParams.width = parent.measuredWidth / 3
        return CustomViewHolder(view)
    }

    class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
        var imageView_item : ImageView? = view?.findViewById(R.id.mainactivity_recyclerview_item_imageview)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var view = holder as CustomViewHolder
        view.imageView_item?.setImageResource(images[position])
    }

}
