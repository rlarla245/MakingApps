package com.updatetest.kotlinspinnertest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    var countries : Array<String> = arrayOf("한국", "America")
    var koreaCities : Array<String> = arrayOf("서울", "대전", "대구")
    var americaCities : Array<String> = arrayOf("맨해튼", "뉴욕", "LA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var spinnerCountry : Spinner? = findViewById(R.id.mainactivty_spinner_country)
        var spinnerCity : Spinner? = findViewById(R.id.mainactivity_spinner_city)

        spinnerCountry?.adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countries)

        spinnerCountry?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var countryName = parent?.getItemAtPosition(position)

                if (countryName == "한국") {
                    spinnerCity?.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_dropdown_item_1line, koreaCities)
                }

                if (countryName == "America") {
                    spinnerCity?.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_dropdown_item_1line, americaCities)
                }
            }
        }

        spinnerCity?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var text = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, text + "선택", Toast.LENGTH_LONG).show()
            }

        }
    }
}
