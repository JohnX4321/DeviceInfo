package com.thingsenz.devinfo.utils

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

object Prefs {

    private const val GPU_RENDERER = "gpu_renderer"
    private const val GPU_VENDOR = "gpu_vendor"
    private const val GPU_VERSION = "gpu_version"
    private const val GPU_EXT = "gpu_extensions"
    private const val FIRST_RUN = "first_run"


    fun setGPURenderer(context: Context,value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(GPU_RENDERER, value) }
    }

    fun getGPURenderer(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(GPU_RENDERER, "") ?: ""
    }

    fun setGPUVendor(context: Context,value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(GPU_VENDOR, value) }
    }

    fun getGPUVendor(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(GPU_VENDOR, "") ?: ""
    }

    fun setGPUVersion(context: Context,value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(GPU_VERSION, value) }
    }

    fun getGPUVersion(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(GPU_VERSION, "") ?: ""
    }

    fun setGPUExtension(context: Context,value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(GPU_EXT, value) }
    }

    fun getGPUExtension(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(GPU_EXT, "") ?: ""
    }

    fun isFirstRun(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(FIRST_RUN,false)
    }

    fun setFirstRun(context: Context,value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putBoolean(FIRST_RUN, value) }
    }

}