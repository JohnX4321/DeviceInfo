package com.thingsenz.devinfo.utils

import android.media.MediaDrm
import android.util.Log
import java.util.UUID

object DrmUtils {

    //https://gist.github.com/huewu/ed8bf1485864e3ca1768740c740e19f6
    private val WIDEVINE_UUID =
        UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
    //Studio Assist
    private val PLAYREADY_UUID =
        UUID(-0x6b39fbe147778754L, -0x3b15a64a942f4e9cL)

    fun getDrmInfo(): HashMap<String,String> {
        var drmInfo = getDrmScheme(WIDEVINE_UUID)
        if (drmInfo[MediaDrm.PROPERTY_VENDOR]?.isEmpty() == true) {
            drmInfo = getDrmScheme(PLAYREADY_UUID)
        }
        return drmInfo
    }

    private fun getDrmScheme(uuid: UUID): HashMap<String,String> = HashMap<String, String>().apply {
        try {
            val mediaDrm = MediaDrm(uuid)
            this["DRM Vendor"] = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VENDOR)
            this["DRM Version"] = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VERSION)
            this["DRM Algorithms"] = mediaDrm.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS)
            this["DRM Description"] = mediaDrm.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION)
            Log.d("TAGX",this["DRM Vendor"] ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            clear()
        }
    }

}