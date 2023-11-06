package com.example.quickfit7

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickfit7.databinding.ActivityExerciseBinding
import com.example.quickfit7.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? =null
    private var restProgress = 0
    private var exTimer: CountDownTimer? =null
    private var exProgress = 0
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var restTimerDuration: Long = 10 //10
    private var exTimerDuration: Long = 30 //30


    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar!=null) supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val callback = object : OnBackPressedCallback(true ) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@ExerciseActivity,"hey back work",Toast.LENGTH_LONG)
                customDialogForBackButton()
            }
        }

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }

        exerciseList = Constants.defaultExerciseList()
        tts = TextToSpeech(this@ExerciseActivity,this)
        setupRestView()
        setupExerciseStatusRecyclerView()

        onBackPressedDispatcher.addCallback(callback)

    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.BtnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.BtnNo.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExStatus?.layoutManager = LinearLayoutManager(this@ExerciseActivity, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.example.quickfit7" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player?.isLooping = false
            player?.start()
        }catch (e:Exception){
            e.printStackTrace()
        }


        binding?.llExerciseView?.visibility = View.GONE
        binding?.llRestView?.visibility = View.VISIBLE
        binding?.nextEx?.text = exerciseList?.get(currentExercisePosition+1)?.getName()


        if(restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        setRestProgressBar()
    }

    private  fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(restTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                //when a second passes
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true) // Current Item is selected
                exerciseAdapter?.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.
                setupExView()
            }
        }.start()
    }

    private fun setupExView(){
        binding?.llExerciseView?.visibility = View.VISIBLE
        binding?.llRestView?.visibility = View.GONE
        binding?.tvEx?.text = exerciseList?.get(currentExercisePosition)?.getName()
        binding?.exImageView?.setImageResource(exerciseList!![currentExercisePosition].getImage())

        if(binding?.tvEx?.text!!.isEmpty()){
            Toast.makeText(this@ExerciseActivity,"no ex",Toast.LENGTH_LONG).show()
        }
        else{
            tts?.speak(binding?.tvEx?.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
        }

        if(exTimer!=null){
            exTimer?.cancel()
            exProgress=0
        }

        setExProgressBar()
    }

    private  fun setExProgressBar(){
        binding?.progressBarEx?.progress = exProgress

        exTimer = object: CountDownTimer(exTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                //when a second passes
                exProgress++
                binding?.progressBarEx?.progress = 30 - exProgress
                binding?.tvTimerEx?.text = (30 - exProgress).toString()
            }

            override fun onFinish() {
                
                if(currentExercisePosition + 1 == exerciseList?.size){
                    //finish , go to finish page
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
                else {
                    exerciseList!![currentExercisePosition].setIsSelected(false) // exercise is completed so selection is set to false
                    exerciseList!![currentExercisePosition].setIsCompleted(true) // updating in the list that this exercise is completed
                    exerciseAdapter?.notifyDataSetChanged()

                    setupRestView()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        if(exTimer!=null){
            exTimer?.cancel()
            exProgress=0
        }

        if(tts!=null){
            tts?.stop()
            tts?.shutdown()
        }

        if(player!=null){
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) Log.e("tts","lang not support")
        }
        else{
            Log.e("tts","initialization fail")
        }
    }

}