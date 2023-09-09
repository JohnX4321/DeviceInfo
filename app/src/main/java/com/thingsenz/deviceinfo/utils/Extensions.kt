package com.thingsenz.deviceinfo.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "devinfo")

fun Long.formatSize(): String {
    var suffix = ""
    var decimal = 0
    var size = this
    if (size >= 1024) {
        suffix=" KB"
        size /= 1024
        if (size >= 1024) {
            suffix = " MB"
            size /= 1024
            if (size>=1024) {
                suffix = " GB"
                decimal = (size%1024).toInt()
                size /= 1024
                if (size >= 1024) {
                    suffix = " TB"
                    decimal = (size%1024).toInt()
                    size /= 1024
                }
            }
        }
    }
    val rb = StringBuilder(size.toString())
    var commaOffset = rb.length - 3
    while (commaOffset > 0) {
        rb.insert(commaOffset, ',')
        commaOffset -= 3
    }
    rb.append(".").append(decimal%100)
    if (suffix.isNotEmpty()) rb.append(suffix)
    return rb.toString()
}