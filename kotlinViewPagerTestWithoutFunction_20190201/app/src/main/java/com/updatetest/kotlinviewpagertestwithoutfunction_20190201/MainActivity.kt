package com.updatetest.kotlinviewpagertestwithoutfunction_20190201

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewPager : ViewPager? = findViewById(R.id.mainactivity_viewpager)
        viewPager?.adapter = ViewPagerAdapter(supportFragmentManager)

        var tabLayout : TabLayout? = findViewById(R.id.mainactivity_tablayout)
        tabLayout?.addTab(tabLayout.newTab().setText("1번 사진"))
        tabLayout?.addTab(tabLayout.newTab().setText("2번 사진"))
        tabLayout?.addTab(tabLayout.newTab().setText("3번 사진"))
        tabLayout?.addTab(tabLayout.newTab().setText("4번 사진"))
        tabLayout?.addTab(tabLayout.newTab().setText("5번 사진"))

        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //
           }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager?.currentItem = tabLayout.selectedTabPosition
            }
        })

        viewPager?.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(tabLayout){

        })

        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

class ViewPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    var images = arrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image2, R.drawable.image4, R.drawable.image5)
    override fun getItem(position: Int): Fragment {
        var args : Bundle = Bundle()
        args.putInt("image", images[position])

        var fragment : Fragment? = KotlinFragment()
        fragment?.arguments = args

        return fragment!!
    }

    override fun getCount(): Int {
        return images.size
    }

}
