package com.antipov.coroutines.idp_renderscript

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import kotlinx.android.synthetic.main.activity_bw.*

class BwActivity : AppCompatActivity() {

    private lateinit var rs: RenderScript
    private lateinit var bwScript: ScriptC_bw
    private lateinit var allocation: Allocation
    private lateinit var bwAllocation: Allocation
    private lateinit var bmp: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bw)
        imgViewBw.setImageBitmap(initRs())
    }

    private fun initRs(): Bitmap {
        // get bmp
        bmp = BitmapFactory.decodeResource(resources, R.drawable.yosemite)
        val res = bmp.copy(bmp.config, true)
        // Creating render script
        rs = RenderScript.create(this)
        //Create script. An Element represents one item within an Allocation.
        // An Element is roughly equivalent to a C type in a RenderScript kernel
        bwScript = ScriptC_bw(rs)
        //Create allocation from Bitmap
        allocation = Allocation.createFromBitmap(rs, bmp)
        //Create allocation with the same type
        bwAllocation = Allocation.createTyped(rs, allocation.type)
        bwScript.forEach_root(allocation, bwAllocation)
        bwAllocation.copyTo(res)

        allocation.destroy()
        bwAllocation.destroy()
        bwScript.destroy()
        rs.destroy()

        return res
    }
}
