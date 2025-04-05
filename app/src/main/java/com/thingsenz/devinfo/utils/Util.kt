package com.thingsenz.devinfo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display.HdrCapabilities
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.RandomAccessFile
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min
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
        if (isS()) {
            if (!Build.ODM_SKU.equals("unknown",ignoreCase = true)) {
                set("ODM_SKU", Build.ODM_SKU)
            }
            if(!Build.SKU.equals("unknown",ignoreCase = true)) {
                set("SKU", Build.SKU)
            }
        }
        TimeZone.getDefault().apply {
            set("TimeZone","${this.getDisplayName(false,TimeZone.SHORT)} (${this.id})")
        }
        Locale.getDefault().apply {
            set("Language", this.displayLanguage)
            set("Country",this.displayCountry)
        }
        set("Uptime", getAndroidUptime())
        // Treble,Seamless Updates and Dynamic partitions

    }

    fun getAndroidUptime(): String {
        val uptimeMillis = SystemClock.elapsedRealtime()
        val days = TimeUnit.MILLISECONDS.toDays(uptimeMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60

        return "$days days, $hours hrs, $minutes min"
    }

    @SuppressLint("HardwareIds")
    fun getSoftwareDetails(context: Context) = listOf<Pair<String,String>>(
        "Version Code" to Build.VERSION.RELEASE,
        "Version Name" to versionName(),
        "SDK Level" to Build.VERSION.SDK_INT.toString(),
        "Codename" to Build.VERSION.CODENAME ,
        "Security Patch" to Build.VERSION.SECURITY_PATCH,
        "Build Number" to Build.DISPLAY,
        "Fingerprint" to Build.FINGERPRINT,
        //"Build ID" to Build.ID, contained in @(4)
        "TAGS" to Build.TAGS,
        "Bootloader" to Build.BOOTLOADER,
        "Baseband" to Build.getRadioVersion(),
        "Java VM" to getJavaVMVersion(),
        "Kernel Version" to getKernelVersion(),
        "OpenGL ES Version" to getOpenGLESVersion(context),
        "Vulkan" to getVulkanSupportVersion(context),

    )

    //https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime
    private fun getJavaVMVersion(): String {
        return System.getProperty("java.vm.version") ?: "Unknown"
    }

    private fun getKernelVersion(): String {
        return System.getProperty("os.version") ?: "Unknown"
    }

    private fun getOpenGLESVersion(context: Context): String {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val configInfo = activityManager.deviceConfigurationInfo
        return configInfo.glEsVersion
    }

    fun getSOCDetails(context: Context) = HashMap<String,String>().apply {
        if (isS()) {
            set("SOC",Build.SOC_MANUFACTURER + " " + Build.SOC_MODEL)
        }
        Build.SUPPORTED_ABIS.apply {
            val sb = StringBuilder()
            for (i in this)
                sb.append(i).append(',')
            set("ABIS",sb.removeSuffix(",").toString())
        }
        set("Hardware",Build.HARDWARE)
        set("Architecture", System.getProperty("os.arch") ?: Build.UNKNOWN)
        val br = BufferedReader(FileReader("/proc/cpuinfo"))
        val s=br.readLines()
        for (i in s) {
            //Log.d("TAGX",i)
            if (i.contains("processor"))
                this["Cores"] = (this.getOrDefault("Cores","0").toInt() + 1).toString()
            else if (i.contains("vendor_id"))
                this["Vendor"] = i.replace("vendor_id:","")
            else if (i.contains("Hardware"))
                this["Manufacturer"] = i.replace("Hardware\t:","")
        }
        getCPUMaxFreq(this["Cores"]?.toIntOrNull() ?: 0).let {
            if (it.isNotEmpty()) {
                //Range
                val sb = StringBuilder()
                for (i in it.entries) {
                    val arr = i.key.split(",")
                    val keyFormat = "${arr[0]} MHz - ${arr[1]} MHz"
                    sb.appendLine("${i.value} x $keyFormat")
                }
                this["CPU Frequencies"] = sb.removeSuffix("\n").toString()
            }
        }
        getCPUGovernor().let {
            if (it.isNotEmpty()) {
                set("CPU Governor", it)
            }
        }
        Prefs.getGPUVendor(context).let {
            if (it.isNotEmpty()){
                set("GPU Vendor", it)
            }
        }
        Prefs.getGPUVersion(context).let {
            if (it.isNotEmpty()){
                set("GPU Version", it)
            }
        }
        Prefs.getGPURenderer(context).let {
            if (it.isNotEmpty()){
                set("GPU Renderer", it)
            }
        }


    }

    private fun getCPUGovernor(): String {
        val govFile = File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor","r")
        var res = ""
        if (govFile.exists()) {
            kotlin.runCatching {
                govFile.bufferedReader().use {
                    res = it.readLine()
                }
            }
        }
        return res
    }


    private fun getCPUMaxFreq(cores: Int) = HashMap<String,String>().apply {

        for (i in 0 until cores) {
            val readerMax = RandomAccessFile("/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq","r")
            val readerMin = RandomAccessFile("/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_min_freq","r")
            val maxFreq = (readerMax.readLine().toInt()/1000).toString()
            val minFreq = (readerMin.readLine().toInt()/1000).toString()
            val keyFormat ="$minFreq,$maxFreq"
            set(keyFormat, (this.getOrDefault(keyFormat,"0").toInt() + 1).toString())
        }

    }

    fun getDisplayDetails(context: Context) = LinkedHashMap<String,String>().apply {
        val dm = Resources.getSystem().displayMetrics
        set("Resolution",dm.heightPixels.toString()+"x"+dm.widthPixels.toString() + "\n(${getDisplayResolutionSlab(
            max(dm.widthPixels,dm.heightPixels),
            min(dm.widthPixels,dm.heightPixels)
        )})")
        set("Density", "${dm.densityDpi} (${getDisplayDensitySlab(dm.densityDpi)})")
        set("Size", getDisplayInches(dm.widthPixels,dm.heightPixels,dm.xdpi,dm.ydpi))
        set("Scale",context.resources.configuration.fontScale.toString())
        set("Refresh Rate",getRefreshRate(context))
        set("HDR Supported",isHDRSupported(context).toString())
        set("HDR Modes", getHdrCapabilities(context))
        set("Orientation",getOrientation(context))
        set("Brightness", getBrightness(context))
    }

    private fun getDisplayResolutionSlab(maxPixels: Int, minPixels: Int) : String =
        when {
            maxPixels <= 480 -> "SD"
            maxPixels <= 720 -> "HD"
            maxPixels <= 1080 || minPixels <= 1080 -> {
                if (maxPixels > 1920) "FHD+"
                else "FHD"
            }
            maxPixels <= 1440 -> "QHD"
            maxPixels <= 2160 -> "UHD"
            else -> "Unknown"
        }

    private fun getDisplayDensitySlab(dpi: Int): String =
        when {
            dpi <= DisplayMetrics.DENSITY_LOW -> "ldpi"
            dpi <= DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            dpi <= DisplayMetrics.DENSITY_TV -> "tvdpi"
            dpi <= DisplayMetrics.DENSITY_HIGH -> "hdpi"
            dpi <= DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            dpi <= DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            dpi <= DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            else -> "unknown"
        }

    private fun getBrightness(context: Context): String {
        val sb = StringBuilder()
        if (Settings.System.getInt(context.contentResolver,Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            sb.append("Manual")
        else
            sb.append("Automatic")
        sb.append(" (${Settings.System.getInt(context.contentResolver,Settings.System.SCREEN_BRIGHTNESS,0)} %)")
        return sb.toString()
    }

    private fun getOrientation(context: Context): String {
        return when(context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> "Portrait"
            Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
            else -> "Unknown"
        }
    }

    private fun isHDRSupported(context: Context): Boolean {
        val dm = context.getSystemService(DisplayManager::class.java)
        return if (dm.displays.isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dm.getDisplay(0).isHdr
        } else false
    }

    private fun getHdrCapabilities(context: Context): String {
        val sb = StringBuilder()
        val dm = context.getSystemService(DisplayManager::class.java)
        if (dm.displays.isNotEmpty() && Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            dm.getDisplay(0)?.let {
                if (it.isHdr) {
                    val hdrCap = it.hdrCapabilities
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        for (i in it.mode.supportedHdrTypes) {
                            if (i == HdrCapabilities.HDR_TYPE_HDR10) {
                                sb.append("HDR10, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_HDR10_PLUS) {
                                sb.append("HDR10+, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_HLG) {
                                sb.append("HLG, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_DOLBY_VISION) {
                                sb.append("HDR Dolby Vision, ")
                            }
                        }
                    } else {
                        for (i in hdrCap.supportedHdrTypes) {
                            if (i == HdrCapabilities.HDR_TYPE_HDR10) {
                                sb.append("HDR10, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_HDR10_PLUS) {
                                sb.append("HDR10+, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_HLG) {
                                sb.append("HLG, ")
                            } else if (i == HdrCapabilities.HDR_TYPE_DOLBY_VISION) {
                                sb.append("HDR Dolby Vision, ")
                            }
                        }
                    }
                } else {
                    sb.append("Unknown")
                }
            }
        } else {
            sb.append("Unknown")
        }
        return sb.removeSuffix(", ").toString()
    }

    private fun getRefreshRate(context: Context): String {
        val dm = context.getSystemService(DisplayManager::class.java)
        if (dm.displays.isNotEmpty()) {
            return "${dm.getDisplay(0).refreshRate.toInt()} Hz"
        }
        return "Unknown"
    }

    fun getDisplayInches(w: Int,h: Int,xdpi: Float,ydpi: Float) : String {
        val x = (w / xdpi).toDouble().pow(2)
        val y = (h / ydpi).toDouble().pow(2)
        val sqrt = sqrt(x+y)
        return NumberFormat.getNumberInstance()
            .apply {
                maximumFractionDigits = 2
                roundingMode = RoundingMode.CEILING
            }.format(sqrt)
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
                Log.d("IMPX", Prefs.getGPUVendor(context))
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
        set("AIM",im.first)
        set("TIM",im.second)
        /*val em = getExternalMemoryDetails()
        set("AEM",em.first)
        set("TEM", em.second)*/
        val rm = getRamDetails(context)
        set("ARAM",rm.first)
        set("TRAM",rm.second)
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

    private fun getVulkanSupportVersion(context: Context): String {
        val pm = context.packageManager
        return if (pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)) {
            if (pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,0x403000))
                "1.3"
            else if (pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,0x401000))
                "1.1"
            else "1.0"
        } else "Unsupported"
    }

    //TODO(DRM)

}