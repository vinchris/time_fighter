package com.vintilescu.timefighter

//import android.content.IntentSender
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    internal val TAG = MainActivity::class.java.simpleName

    internal lateinit var tapMeButton: Button // button "Tap Me" property
    internal lateinit var gameScoreTextView: TextView // TextView "Game score" property
    internal lateinit var timeLeftTextView: TextView // TextView "Time left" property

    internal var score = 0

    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is: $score .")

        tapMeButton = findViewById<Button>(R.id.tap_me_button)
        gameScoreTextView = findViewById<TextView>(R.id.game_score_text_view)
        timeLeftTextView = findViewById<TextView>(R.id.time_left_text_view)

        // first implementation of reset game score: gameScoreTextView.text = getString(R.string.your_score, score.toString())
        // second implementation via method: resetGame()
        //third implmentation via if condition which check for previous value of saved instance state:
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        } else {
            //if no saved instance state then restore the game to its default values
            resetGame()
        }

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)

            incrementScore()
        }
    }

    private fun restoreGame() {
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
        val restoredTime = timeLeftOnTimer / 1000
        timeLeftTextView.text = getString(R.string.time_left, restoredTime.toString())

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUnitsUntilFinished: Long) {
                timeLeftOnTimer = millisUnitsUntilFinished
                val timeLeft = millisUnitsUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true

    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score.toString()), Toast.LENGTH_SHORT).show()
        resetGame()
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score += 1
        val newScore = getString(R.string.your_score, score.toString()) // First way to implement this
        // val newScore = "Your Score: {score.toString()}" second implementation
        gameScoreTextView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)
    }

    /**
     * this method gets called when the current activity's state is saved
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer .")
    }

    /**
     * this method gets called when the current activity gets destroyed
     */
    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame() {
        score = 0
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = getString(R.string.time_left, initialTimeLeft.toString())

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUnitsUntilFinished: Long) {
                timeLeftOnTimer = millisUnitsUntilFinished
                val timeLeft = millisUnitsUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    /**
     * we're overriding the method that handles the creation of the activity's standard menu
     * and you are overriding the default implementation to show your own menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        /** the menuInflater is an existing property
        from the parent class AppCompatActivity.kt
        which is used to instantiate menu from xml files**/
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * this overridden method handles menu item selection where we look for
     * the ID of the menu item we added, if that is the selected item then
     * we execute a method to show app information
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.action_about){
            showInfo()
        }
        return true
    }

    /**
     * method which creates an Alert Dialog on menu item tap
     */
    private fun showInfo(){
        Log.d(TAG, "Info Icon clicked.")
        // the info title contains the app version number, the user can see immediately which version he/she is using
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        // the dialog needs a context instance, so that the dialog knows where it should appear
        /**
         * ALL ACTIVITIES ARE SUBCLASSES OF CONTEXT
         */
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }
}
