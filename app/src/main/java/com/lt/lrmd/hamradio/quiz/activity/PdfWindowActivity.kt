package com.lt.lrmd.hamradio.quiz.activity

import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lt.lrmd.hamradio.quiz.R
import android.content.res.AssetManager
import kotlin.Throws
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.*
import java.lang.Exception

@AndroidEntryPoint
class PdfWindowActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_window_activity)
        copyAssets()
    }

    private fun copyAssets() {
        val assetManager = assets
        val files: Array<String>?
        files = try {
            assetManager.list("Files")
        } catch (e: IOException) {
            Log.e("PdfWindowActivity", "Could not get list of files", e)
            return
        }
        for (filename in files!!) {
            println("File name => $filename")
            try {
                assetManager.open("Files/$filename").use { `in` ->
                    FileOutputStream(
                        Environment.getExternalStorageDirectory().toString() + "/" + filename
                    ).use { out -> copyFile(`in`, out) }
                }
            } catch (e: Exception) {
                Log.e("PdfWindowActivity", "Could not copy assets", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    fun openIstatymasPdf(v: View?) {
        openPdfFile("istatymas.pdf")
    }

    fun openSantrumposPdf(v2: View?) {
        openPdfFile("santrupos.pdf")
    }

    private fun openPdfFile(pdfFileName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val path = Environment.getExternalStorageDirectory().absolutePath
        val file = File(path, pdfFileName)
        intent.setDataAndType(Uri.fromFile(file), "application/pdf")
        startActivity(intent)
    }

    fun openAtstojamosiosPdf(v3: View?) {
        openPdfFile("atstojamosios.pdf")
    }
}