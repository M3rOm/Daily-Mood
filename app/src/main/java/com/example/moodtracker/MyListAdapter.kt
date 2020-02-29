package com.example.moodtracker

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
//Creating my own ListAdapter to display the entries in HistoryActivity
class MyListAdapter (private var list: ArrayList<HorizontalBar>,
                     private val context: Context) : RecyclerView.Adapter<MyListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        // Create our view from our xml file
        val view = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView){

        private val barColors = listOf("#DE3C50","#9B9B9B", "#458AD9", "#B8E986", "#F8EC50")


        fun bindItem(horizontalBar: HorizontalBar) {
            val description: TextView = itemView.findViewById(R.id.daysPassed) as TextView
            val myCardView: View = itemView.findViewById(R.id.cardView)
            val mood: Int = horizontalBar.mood
            val comment = horizontalBar.comment
            val textPic: ImageView = itemView.findViewById(R.id.commentPic) as ImageView

            description.text = horizontalBar.description

            //Get device width and height
            val displayMetrics = DisplayMetrics()
            (itemView.context as HistoryActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            //Resize the card dimensions
            myCardView.layoutParams.height = height/8
            myCardView.layoutParams.width = width/5*(mood+1)
            myCardView.setBackgroundColor(Color.parseColor(barColors[mood]))
            //Set text icon visible if it has a comment attached
            if (comment !== "")
                textPic.visibility = View.VISIBLE
            textPic.setOnClickListener{
                Toast.makeText(context, "$comment", Toast.LENGTH_LONG).show()
                }

        }
        
    }


}