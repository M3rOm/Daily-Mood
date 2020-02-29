package com.example.moodtracker

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.history_layout.*

class HistoryActivity: AppCompatActivity() {
    private lateinit var horizontalBarList: ArrayList<HorizontalBar>
    private lateinit var adapter: MyListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_layout)

        horizontalBarList = ArrayList()
        layoutManager = LinearLayoutManager (this)
        adapter = MyListAdapter (horizontalBarList, this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val storedData = getSharedPreferences("Mood storage", Context.MODE_PRIVATE)
        //This is a support function to manage the value pair names in SharedPreferences
        fun createCommentStringName(number: Int): String{
            return "CM $number"+"D"
        }
        //This is also a support function to manage the value pair names in SharedPreferences
        fun createMoodStringName(number: Int): String{
            return "MD $number"+"D"
        }
        //Initialize Shared preferences
        for (j in 0..7) {
            val horizontalBar = HorizontalBar ()
            horizontalBar.mood = storedData.getInt(createMoodStringName(j), 2)
            horizontalBar.comment = storedData.getString(createCommentStringName(j),"")
            horizontalBar.description = "$j day(s) ago"
            horizontalBarList.add(horizontalBar)
        }
        adapter.notifyDataSetChanged()
        }
    }