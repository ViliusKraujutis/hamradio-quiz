package com.lt.lrmd.hamradio.quiz.util

import android.app.AlertDialog
import android.content.Context
import com.lt.lrmd.hamradio.quiz.R
import android.content.pm.PackageManager
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException

object Util {
    fun serialize(`object`: Any?): ByteArray {
        val bytes = ByteArrayOutputStream()
        val out: ObjectOutputStream
        return try {
            out = ObjectOutputStream(bytes)
            out.writeObject(`object`)
            bytes.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun deserialize(bytes: ByteArray?): Any {
        return try {
            val bin = ByteArrayInputStream(bytes)
            val `in` = ObjectInputStream(bin)
            `in`.readObject()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun errorAlert(context: Context?, message: String?) {
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle("FAIL!").setMessage(message)
            .setIcon(R.drawable.emo_im_wtf)
            .setPositiveButton("OK", null)
            .show()
    }

    fun getSharedPrefsFile(context: Context, fileName: String?): File {
        val dataDir = getDataDir(context)
        return File(dataDir, fileName)
    }

    fun getDataDir(context: Context): File {
        return try {
            File(
                context.packageManager.getPackageInfo(
                    context.packageName, 0
                ).applicationInfo.dataDir
            )
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }
}