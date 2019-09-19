#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.antipov.coroutines.idp_renderscript)

#include "rs_debug.rsh"

uchar4 __attribute__((kernel)) root(uchar4 in) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);

    float red = f4.r;
    float green = f4.g;
    float blue = f4.b;

    float gray = (red + green + blue) / 3;

    //Put the values in the output uchar4, note that we keep the alpha value
    return rsPackColorTo8888(gray, gray, gray, f4.a);
}