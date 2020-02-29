package com.example.moodtracker

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View.OnTouchListener
import android.widget.EditText
import kotlinx.android.synthetic.main.layout_dialog.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var myEditText: EditText
    private lateinit var customGestureDetector: CustomGestureDetector
    // Refresh rate for testing with 20 seconds long 'days'
    // private val refreshRate = 20000
    // Refresh rate for normal days
     private val refreshRate = 86400000

    // initialize our soundpool
    lateinit var mySoundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create an object of OUR Custom Gesture Detector Class
        customGestureDetector = CustomGestureDetector(layout, smiley,this)
         /***
          * Create an ACTUAL GestureDetector using our custom gesture detector
          * Defining our gesture processing logic in a custom gesture detector
          * class and then using that class to construct an Actual gestureDetector
          * ***/
        gestureDetector = GestureDetector(this, customGestureDetector)

        //Attach a touch listener to our smiley face image
        smiley.setOnTouchListener(
            OnTouchListener {
                v, event ->
                //pass all touch events to our gesture detector for processing
                gestureDetector.onTouchEvent(event)
                true
            }
        )
        //Set image to last position
        customGestureDetector.versionCheck()
        customGestureDetector.setImage(customGestureDetector.rating)
        //Open comment dialog
        buttonIcon.setOnClickListener{
            //Inflate the dialog with custom view
            val myDialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog,null)
            myEditText = myDialogView.findViewById(R.id.editText)

            //Initialize Shared preferences
            val storedData = getSharedPreferences("Mood storage", Context.MODE_PRIVATE)

            myEditText.setText(storedData.getString("CM 0D",""))

            //Alert dialog builder
            val myBuilder = AlertDialog.Builder (this)
                .setView(myDialogView)
                .setTitle("Comment")
            //Show dialog
            val myAlertDialog = myBuilder.show()
            //cancel button clicked
            myDialogView.cancelButton.setOnClickListener{
                //dismiss dialog
                myAlertDialog.dismiss()
            }
            //Confirm button clicked
            myDialogView.confirmButton.setOnClickListener {
                //save comment
                saveWithTopLevel()
                //dismiss dialog
                myAlertDialog.dismiss()
            }
        }
        //Open History_activity
        historyIcon.setOnClickListener {
        val intent = Intent (this, HistoryActivity::class.java)
        startActivity(intent)
        }
    }
    private fun saveWithTopLevel() {
        //Initialize Shared preferences
        val storedData = getSharedPreferences("Mood storage", Context.MODE_PRIVATE)
        val dailyComment = myEditText.text.toString().trim()
        //edit storeData to put data
        val editor =storedData.edit()
        //put data in storeData sharedPreferences
        editor.putString("CM 0D", dailyComment)
        editor.apply()
    }

    override fun onResume() {
        // Log system time
        val sysTime = System.currentTimeMillis()
        val storedData = getSharedPreferences("Mood storage", Context.MODE_PRIVATE)
        val lastOpened = storedData.getLong("systime", System.currentTimeMillis())
        //Checks when was the last time it opened and matches it with current time.
        if (sysTime/ refreshRate > lastOpened/ refreshRate){
            val daysPassedSinceLastOpen = ((sysTime - sysTime % refreshRate) - (lastOpened - lastOpened % refreshRate)) / refreshRate
            reorganizeData(daysPassedSinceLastOpen, customGestureDetector)
        }
        val editor = storedData.edit()
        editor.putLong("systime", System.currentTimeMillis())
        editor.apply()

        super.onResume()
    }
    // Function reorganizeData will pull SharedPreferences, and modify the entries value to reflect the time passed since last open
    private fun reorganizeData(daysPassedSinceLastOpen: Long, gestureDetector: CustomGestureDetector) {
        val storedData = getSharedPreferences("Mood storage", Context.MODE_PRIVATE)
        val editor = storedData.edit()
        var i = 0
        val timeFrame = 7
        // Flush Mood storage if the last open time was over a week ago.
        if (daysPassedSinceLastOpen >= timeFrame) {
            for (j in 0..timeFrame) {
                editor.putString(createCommentStringName(j), "")
                editor.putInt(createMoodStringName(j), 2)
            }
        }
        //if some history available
        else {
            while (i < daysPassedSinceLastOpen) {
                for (k in timeFrame downTo 1) {
                    editor.putString(createCommentStringName(k), storedData.getString(createCommentStringName(k - 1), ""))
                    editor.putInt(createMoodStringName(k),storedData.getInt(createMoodStringName(k-1), 2))
                }
                i++
            }
            editor.putString("CM 0D", "")
            editor.putInt("MD 0D", 2)
            smiley.setImageDrawable(resources.getDrawable(R.drawable.smiley_normal))
            layout.setBackgroundColor(getColor(this,R.color.blue))
        }
        editor.apply()
        gestureDetector.resetRating()
    }
    //This is a support function to manage the value pair names in SharedPreferences
    private fun createCommentStringName(number: Int): String{
        return "CM $number"+"D"
    }
    //This is also a support function to manage the value pair names in SharedPreferences
    private fun createMoodStringName(number: Int): String{
        return "MD $number"+"D"
    }
}




