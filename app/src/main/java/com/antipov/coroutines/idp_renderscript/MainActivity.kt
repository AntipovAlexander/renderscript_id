package com.antipov.coroutines.idp_renderscript

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import com.example.q.renderscriptexample.ScriptC_yuv
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intrinsicBlur.setOnClickListener { startActivity(Intent(this, IntrinsicBlurActivity::class.java)) }
        histogramEqv.setOnClickListener { }
//        btn.setOnClickListener {
//            val bmp = BitmapFactory.decodeResource(resources, R.drawable.yosemite)
//            val blurred = histogramEqualization(bmp, this)
//            imgView.setImageBitmap(blurred)
//        }
    }

    fun histogramEqualization(image: Bitmap, context: Context): Bitmap {
        //Get image size
        val width = image.width
        val height = image.height

        //Create new bitmap
        val res = image.copy(image.config, true)

        //Create renderscript
        val rs = RenderScript.create(context)

        //Create allocation from Bitmap
        val allocationA = Allocation.createFromBitmap(rs, res)

        //Create allocation with same type
        val allocationB = Allocation.createTyped(rs, allocationA.type)

        //Create script from rs file.
        val histEqScript = ScriptC_yuv(rs)

        //Set size in script
        histEqScript.set_size(width * height)

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB)

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray()

        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA)

        //Copy script result into bitmap
        allocationA.copyTo(res)

        //Destroy everything to free memory
        allocationA.destroy()
        allocationB.destroy()
        histEqScript.destroy()
        rs.destroy()

        return res
    }

}
