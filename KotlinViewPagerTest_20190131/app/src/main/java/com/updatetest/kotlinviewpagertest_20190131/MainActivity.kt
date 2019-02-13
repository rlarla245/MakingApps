package com.updatetest.kotlinviewpagertest_20190131

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewPager : ViewPager? = findViewById(R.id.mainactivity_viewpager)
        viewPager?.adapter = ViewPagerAdapter(supportFragmentManager)

        var tablayout : TabLayout? = mainactivity_tablayout

        mainactivity_tablayout.addTab(mainactivity_tablayout.newTab().setText("1번 사진"))
        mainactivity_tablayout.addTab(mainactivity_tablayout.newTab().setText("2번 사진"))
        mainactivity_tablayout.addTab(mainactivity_tablayout.newTab().setText("3번 사진"))
        mainactivity_tablayout.addTab(mainactivity_tablayout.newTab().setText("4번 사진"))
        mainactivity_tablayout.addTab(mainactivity_tablayout.newTab().setText("5번 사진"))

        mainactivity_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager?.currentItem = mainactivity_tablayout.selectedTabPosition
            }
        })

        viewPager?.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(mainactivity_tablayout){})

        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

class ViewPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    var images = arrayOf(R.drawable.i_1, R.drawable.i_5, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5)
    override fun getItem(position: Int): Fragment {
        var fragment : ViewPagerFragment? = ViewPagerFragment()
        return fragment!!.loadImage(images[position])
    }

    override fun getCount(): Int {
        return images.size
    }

}
