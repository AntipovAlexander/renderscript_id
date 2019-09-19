package com.antipov.coroutines.idp_renderscript

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intrinsicBlur.setOnClickListener { startActivity(Intent(this, IntrinsicBlurActivity::class.java)) }
        bw.setOnClickListener { startActivity(Intent(this, BwActivity::class.java)) }
        histogramEqv.setOnClickListener { startActivity(Intent(this, HistogramEqualizationActivity::class.java)) }
        scriptIntrinsicConvolve.setOnClickListener { startActivity(Intent(this, ConvolutionActivity::class.java)) }
    }

}
