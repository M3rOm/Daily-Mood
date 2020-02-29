package com.example.moodtracker

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView

//Custom gesture detector class with our own gesture processing logic
class CustomGestureDetector(private val appLayout: ConstraintLayout, private val smileyView: ImageView, private val context: Context): GestureDetector.OnGestureListener{

    //Initialize Shared preferences
    private val storedData = context.getSharedPreferences("Mood storage", Context.MODE_PRIVATE)
    //Initialize editor to  modify storedData
    private val editor =storedData.edit()
    var rating = storedData.getInt("MD 0D",2)
    //Initializing variables for SoundPool
    private lateinit var mySoundPool: SoundPool

    private var sound0 : Int = 0
    private var sound1 : Int = 0
    private var sound2 : Int = 0
    private var sound3 : Int = 0
    private var sound4 : Int = 0
    //Check Android version
    fun versionCheck() {
        //For API 21 and above, I am using the SoundPool Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val myAudioAttributes: AudioAttributes? = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            mySoundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(myAudioAttributes)
                .build()
        } else {
            //For API below 21, I am using the deprecated SoundPool
            mySoundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }
        sound0 = mySoundPool.load(context,R.raw.sound0,1)
        sound1 = mySoundPool.load(context,R.raw.sound1,1)
        sound2 = mySoundPool.load(context,R.raw.sound2,1)
        sound3 = mySoundPool.load(context,R.raw.sound3,1)
        sound4 = mySoundPool.load(context,R.raw.sound4,1)
    }
    //This function executes on new day. It resets the daily mood rating
    fun resetRating(){
        rating = 2
        versionCheck()
        setImage(rating)
    }
    //Put mood value in storeData sharedPreferences
    private fun saveMood(){
        editor.putInt("MD 0D", rating)
        editor.apply()
    }
    //Incrementing rating variable
    private fun setNext() {
        if (rating<4)  rating +=1
        saveMood()
    }
    //Decrementing rating variable
    private fun setPrevious() {
        if (rating >0) rating -=1
        saveMood()
    }
    // This function sets the smiley image, the background colour, and plays a sound corresponding to the actual mood value
    fun setImage(imgRating: Int) {
        when (imgRating) {
            0 ->  {smileyView.setImageDrawable(context.resources.getDrawable(R.drawable.smiley_sad))
                    appLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                    mySoundPool.play(sound0,1f,1f,0,0,1f)}
            1 ->  {smileyView.setImageDrawable(context.resources.getDrawable(R.drawable.smiley_disappointed))
                    appLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
                    mySoundPool.play(sound1,1f,1f,0,0,1f)}
            2 ->  {smileyView.setImageDrawable(context.resources.getDrawable(R.drawable.smiley_normal))
                    appLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                   mySoundPool.play(sound2,1f,1f,0,0,1f)}
            3 ->  {smileyView.setImageDrawable(context.resources.getDrawable(R.drawable.smiley_happy))
                    appLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                    mySoundPool.play(sound3,1f,1f,0,0,1f)}
            4 ->  {smileyView.setImageDrawable(context.resources.getDrawable(R.drawable.smiley_super_happy))
                    appLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                    mySoundPool.play(sound4,1f,1f,0,0,1f)}
        }
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }
    // Overriding the onFling function, so it executes other functions onFling
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {


        if (e1?.y!! < e2?.y!!) {
            setPrevious()
            setImage(rating)
        }

        if (e1.y > e2.y) {
            setNext()
            setImage(rating)
        }
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }

}