package com.example.quickfit7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.quickfit7.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    private var binding: ActivityBmiactivityBinding? = null
    private var isMetric = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        makeVisibleView(true)

        binding?.rgUnits?.setOnCheckedChangeListener { _, id ->
            if (id == R.id.rbMetricUnits) {
                makeVisibleView(true)
                isMetric = true
            } else {
                makeVisibleView(false)
                isMetric = false
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            if (validateUnits()) {
                var ht = 0F
                var wt = 0F
                if (isMetric) {
                    ht = binding?.etMetricHt?.text.toString().toFloat() / 100 //len in m
                    wt = binding?.etMetricWt?.text.toString().toFloat()
                } else {
                    ht =
                        ((binding?.etUsFt?.text.toString().toFloat() * 12 + binding?.etUsIn?.text.toString().toFloat()) / 100) * 2.54F //len in In
                    wt = binding?.etUsWt?.text.toString().toFloat() * 0.453592F
                }
                var bmi = wt / (ht * ht)

                displayBMIResult(bmi)
            } else {
                Toast.makeText(this@BMIActivity, "Please enter valid values", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun makeVisibleView(isM: Boolean) {
        if (isM) {
            //make all metrics show up and us gone
            binding?.llUs?.visibility = View.GONE
            binding?.llMetric?.visibility = View.VISIBLE
            binding?.etUsFt?.text!!.clear()
            binding?.etUsIn?.text!!.clear()
            binding?.etUsWt?.text!!.clear()
        } else {
            binding?.llUs?.visibility = View.VISIBLE
            binding?.llMetric?.visibility = View.GONE
            binding?.etMetricHt?.text!!.clear()
            binding?.etMetricWt?.text!!.clear()
        }
    }

    private fun displayBMIResult(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text =
            BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }

    private fun validateUnits(): Boolean {
        var isValid = false

        if (isMetric) {
            if (binding?.etMetricHt?.text.toString()
                    .isNotEmpty() && binding?.etMetricWt?.text.toString().isNotEmpty()
            )
                isValid = true
        } else {
            if (binding?.etUsFt?.text.toString()
                    .isNotEmpty() && binding?.etUsWt?.text.toString()
                    .isNotEmpty() && binding?.etUsIn?.text.toString().isNotEmpty()
            )
                isValid = true
        }
        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}