package com.antipov.coroutines.idp_renderscript

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v8.renderscript.*
import kotlinx.android.synthetic.main.activity_convolution.*


class ConvolutionActivity : AppCompatActivity() {

    private lateinit var rs: RenderScript
    private lateinit var convolveScript: ScriptIntrinsicConvolve3x3
    private lateinit var inputAlloc: Allocation
    private lateinit var outputAlloc: Allocation
    private lateinit var bmp: Bitmap

    val blurMatrix =
        floatArrayOf(0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f, 0.0625f)
    val identity = floatArrayOf(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f)
    val edge = floatArrayOf(-1f, -1f, -1f, -1f, 8f, -1f, -1f, -1f, -1f)
    var matrix_sharpen = floatArrayOf(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)
    private val TRANSFORMATION_MATRIX = Matrix4f(
        floatArrayOf(
            -0.33f,
            -0.33f,
            -0.33f,
            1.0f,
            -0.59f,
            -0.59f,
            -0.59f,
            1.0f,
            -0.11f,
            -0.11f,
            -0.11f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convolution)
        imgViewConv.setImageBitmap(initRs())
    }

    private fun initRs(): Bitmap {
        // get bmp
        bmp = BitmapFactory.decodeResource(resources, R.drawable.unsharp)
        val res = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        // Creating render script
        rs = RenderScript.create(this)
        //Create script. An Element represents one item within an Allocation.
        convolveScript = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))

        //Create allocation from Bitmap
        inputAlloc = Allocation.createFromBitmap(rs, bmp)
        //Create allocation with the same type
        outputAlloc = Allocation.createFromBitmap(rs, res)

        convolveScript.setInput(inputAlloc)
        convolveScript.setCoefficients(edge)
        convolveScript.forEach(outputAlloc)

        val invertMatrix = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
        invertMatrix.setColorMatrix(TRANSFORMATION_MATRIX)
        val grayscaled = Allocation.createFromBitmap(rs, res)
        invertMatrix.forEach(outputAlloc, grayscaled)

        grayscaled.copyTo(res)
        return res
    }
}
