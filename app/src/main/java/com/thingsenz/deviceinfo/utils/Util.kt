package com.thingsenz.deviceinfo.utils

import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import java.io.BufferedReader
import java.io.FileReader
import kotlin.math.pow
import kotlin.math.sqrt

object Util {

    private fun isS() = Build.VERSION.SDK_INT>=Build.VERSION_CODES.S

    fun getAllDeviceDetails() =  HashMap<String,String>().apply {
        set("MODEL",Build.MODEL)
        set("MANUFACTURER",Build.MANUFACTURER)
        set("PRODUCT",Build.PRODUCT)
        set("MAKE",Build.BRAND)
        set("BOARD",Build.BOARD)
        set("BOOTLOADER",Build.BOOTLOADER)
        set("DEVICE",Build.DEVICE)
        set("Hardware",Build.HARDWARE)
        if (isS()) {
            set("ODM_SKU", Build.ODM_SKU)
            set("SKU", Build.SKU)
        }
    }

    fun getSoftwareDetails() = HashMap<String,String>().apply {
        set("Version",Build.VERSION.BASE_OS)
        set("SDK",Build.VERSION.SDK_INT.toString())
        set("Codename",Build.VERSION.CODENAME)
        set("Security",Build.VERSION.SECURITY_PATCH)
        set("Display",Build.DISPLAY)
        set("Fingerprint",Build.FINGERPRINT)
        set("ID",Build.ID)
        set("TAGS",Build.TAGS)
    }

    fun getSOCDetails() = HashMap<String,String>().apply {
        if (isS()) {
            set("SOC", Build.SOC_MODEL)
            set("SOC Manufacturer", Build.SOC_MANUFACTURER)
        }
        set("ABIS",Build.SUPPORTED_ABIS.toString())
    }

    fun getCPUDetails(): HashMap<String,String> {
        val map = HashMap<String,String>()

        val br = BufferedReader(FileReader("/proc/cpuinfo"))
        val s=br.readLines()
        for (i in s) {
            if (i.contains("processor"))
                map.put("processor",i.replace("processor:",""))
            else if (i.contains("vendor_id"))
                map.put("vendor_id",i.replace("vendor_id:",""))
            else if (i.contains("model"))
                map.put("mode",i.replace("model:",""))
        }
        return map
    }

    fun getDisplayDetails() = HashMap<String,String>().apply {
        val dm = Resources.getSystem().displayMetrics
        put("Width",dm.widthPixels.toString())
        put("Height",dm.heightPixels.toString())
        put("Dpi",dm.densityDpi.toString())
        put("Size", getDisplayInches(dm.widthPixels,dm.heightPixels,dm.xdpi,dm.ydpi).toString())
    }

    fun getDisplayInches(w: Int,h: Int,xdpi: Float,ydpi: Float) : Double {
        val x = (w / xdpi).toDouble().pow(2)
        val y = (h / ydpi).toDouble().pow(2)
        return sqrt(x+y)
    }

}