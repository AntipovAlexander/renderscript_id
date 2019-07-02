package com.antipov.coroutines.idp_renderscript

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_intrinsic_blur.*

class IntrinsicBlurActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var rs: RenderScript
    private lateinit var blurScript: ScriptIntrinsicBlur
    private lateinit var allocation: Allocation
    private lateinit var blurredAllocation: Allocation
    private lateinit var bmp: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intrinsic_blur)
        initRs()
        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun initRs() {
        // Creating render script
        rs = RenderScript.create(this)
        //Create script. An Element represents one item within an Allocation.
        // An Element is roughly equivalent to a C type in a RenderScript kernel
        blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        //Create allocation from Bitmap
        bmp = BitmapFactory.decodeResource(resources, R.drawable.yosemite)
        allocation = Allocation.createFromBitmap(rs, bmp)
        //Create allocation with the same type
        blurredAllocation = Allocation.createTyped(rs, allocation.type)
        //Set input for script
        blurScript.setInput(allocation)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        imgView.setImageBitmap(blurBitmap(bmp, progress.toFloat()))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    private fun blurBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius)
        //Call script for output allocation
        blurScript.forEach(blurredAllocation)
        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap)
        return bitmap
    }

    override fun onDestroy() {
        //Destroy everything to free memory
        allocation.destroy()
        blurredAllocation.destroy()
        blurScript.destroy()
        allocation.type.destroy()
        rs.destroy()
        super.onDestroy()
    }
}
