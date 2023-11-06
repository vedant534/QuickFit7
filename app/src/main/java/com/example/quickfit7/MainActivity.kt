package com.example.quickfit7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quickfit7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener{
            startActivity(Intent(this, ExerciseActivity::class.java))
        }

        binding?.llBmi?.setOnClickListener{
            startActivity(Intent(this, BMIActivity::class.java))
        }

        binding?.llHis?.setOnClickListener{
            startActivity(Intent(this, HistoryActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}



