package com.antipov.coroutines.idp_renderscript

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript

class HistogramEqualizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histogram_equalization)
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
