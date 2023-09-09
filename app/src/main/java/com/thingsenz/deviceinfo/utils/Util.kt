package com.thingsenz.deviceinfo.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.pow
import kotlin.math.sqrt

object Util {

    private fun isS() = Build.VERSION.SDK_INT>=Build.VERSION_CODES.S

    fun getAllDeviceDetails() =  LinkedHashMap<String,String>().apply {
        set("Model",Build.MODEL)
        set("Manufacturer",Build.MANUFACTURER)
        set("Product",Build.PRODUCT)
        set("Brand",Build.BRAND)
        set("Board",Build.BOARD)
        set("Bootloader",Build.BOOTLOADER)
        set("Device Name",Build.DEVICE)
        set("Hardware",Build.HARDWARE)
        if (isS()) {
            set("ODM_SKU", Build.ODM_SKU)
            set("SKU", Build.SKU)
        }
        TimeZone.getDefault().apply {
            set("TimeZone","${this.getDisplayName(false,TimeZone.SHORT)} (${this.id})")
        }
        Locale.getDefault().apply {
            set("Language", this.displayLanguage)
            set("Country",this.displayCountry)
        }
        set("Uptime", getAndroidUptime())

    }

    fun getAndroidUptime(): String {
        val uptimeMillis = SystemClock.elapsedRealtime()
        val days = TimeUnit.MILLISECONDS.toDays(uptimeMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60

        return "$days days $hours:$minutes"
    }

    fun getSoftwareDetails() = listOf<Pair<String,String>>(
        "Version Code" to Build.VERSION.RELEASE,
        "Version Name" to versionName(),
        "SDK Level" to Build.VERSION.SDK_INT.toString(),
        "Codename" to Build.VERSION.CODENAME ,
        "Security" to Build.VERSION.SECURITY_PATCH,
        "Build ID" to Build.DISPLAY,
        "Fingerprint" to Build.FINGERPRINT,
        //"Build ID" to Build.ID, contained in @(4)
        "TAGS" to Build.TAGS
    )

    fun getSOCDetails(context: Context) = HashMap<String,String>().apply {
        if (isS()) {
            set("SOC", Build.SOC_MODEL)
            set("SOC Manufacturer Code", Build.SOC_MANUFACTURER)
        }
        Build.SUPPORTED_ABIS.apply {
            val sb = StringBuilder()
            for (i in this)
                sb.append(i).append(',')
            set("ABIS",sb.toString())
        }
        set("Architecture", System.getProperty("os.arch") ?: Build.UNKNOWN)
        val br = BufferedReader(FileReader("/proc/cpuinfo"))
        val s=br.readLines()
        for (i in s) {
            Log.d("TAGX",i)
            if (i.contains("processor"))
                this["Processor"] = i.replace("processor\t:","")
            else if (i.contains("vendor_id"))
                this["Vendor"] = i.replace("vendor_id:","")
            else if (i.contains("Hardware"))
                this["Manufacturer"] = i.replace("Hardware\t:","")
        }
        if (Prefs.getGPUVersion(context).isNotEmpty()) {
            set("GPU Renderer", Prefs.getGPURenderer(context))
            set("GPU Vendor", Prefs.getGPUVendor(context))
            set("GPU Version", Prefs.getGPUVersion(context))
            set("GPU Extensions", Prefs.getGPUExtension(context))
        }

    }

    fun getCPUDetails(): HashMap<String,String> {
        val map = HashMap<String,String>()

        val br = BufferedReader(FileReader("/proc/cpuinfo"))
        val s=br.readLines()
        for (i in s) {
            if (i.contains("processor"))
                map["Processor"] = i.replace("processor:","")
            else if (i.contains("vendor_id"))
                map["Vendor"] = i.replace("vendor_id:","")
            else if (i.contains("model"))
                map["Model"] = i.replace("model:","")
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

    private fun versionName(): String {
        val f = Build.VERSION_CODES::class.java.fields
        return f.firstOrNull { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }?.name ?: Build.UNKNOWN.uppercase()
    }

    fun fetchAndStoreGPUDetails(context: Activity): GLSurfaceView {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setRenderer(object : Renderer {
            override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
                p0?.glGetString(GL10.GL_RENDERER)?.let { Prefs.setGPURenderer(context, it) }
                p0?.glGetString(GL10.GL_VENDOR)?.let { Prefs.setGPUVendor(context, it) }
                p0?.glGetString(GL10.GL_VERSION)?.let { Prefs.setGPUVersion(context, it) }
                p0?.glGetString(GL10.GL_EXTENSIONS)?.let { Prefs.setGPUExtension(context, it) }
                Log.d("IMPX",Prefs.getGPUVendor(context))
                context.finish()
            }

            override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

            }

            override fun onDrawFrame(p0: GL10?) {

            }
        })
        return glSurfaceView
    }

    fun getStorageDetails(context: Context) = HashMap<String,Long>().apply {
        val im = getInternalMemoryDetails()
        set("Available Internal Memory",im.first)
        set("Total Internal Memory",im.second)
        val em = getExternalMemoryDetails()
        set("Available External Memory",em.first)
        set("Total External Memory", em.second)
        val rm = getRamDetails(context)
        set("Available RAM",rm.first)
        set("Total RAM",rm.second)
    }

    private fun getRamDetails(context: Context): Pair<Long,Long> {
        val am = context.getSystemService(ActivityManager::class.java)
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        return memInfo.availMem to memInfo.totalMem
    }

    private fun isExternalMemoryAvailable() = Environment.getExternalStorageState()==Environment.MEDIA_MOUNTED

    private fun getInternalMemoryDetails(): Pair<Long,Long>  {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return (stat.blockSizeLong*stat.availableBlocksLong) to (stat.blockSizeLong*stat.blockCountLong)
    }

    private fun getExternalMemoryDetails(): Pair<Long,Long> {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        return (stat.blockSizeLong*stat.availableBlocksLong) to (stat.blockSizeLong*stat.blockCountLong)
    }

}