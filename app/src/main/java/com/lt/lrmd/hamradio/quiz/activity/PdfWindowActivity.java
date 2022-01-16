package com.lt.lrmd.hamradio.quiz.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lt.lrmd.hamradio.quiz.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

@AndroidEntryPoint
public class PdfWindowActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_window_activity);
        copyAssets();
    }


    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files;
        try {
            files = assetManager.list("Files");
        } catch (IOException e) {
            Log.e("PdfWindowActivity", "Could not get list of files", e);
            return;
        }

        for (String filename : files) {
            System.out.println("File name => " + filename);
            try (
                    InputStream in = assetManager.open("Files/" + filename);
                    OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/" + filename);
            ) {
                copyFile(in, out);
            } catch (Exception e) {
                Log.e("PdfWindowActivity", "Could not copy assets", e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public void openIstatymasPdf(View v) {
        openPdfFile("istatymas.pdf");
    }

    public void openSantrumposPdf(View v2) {
        openPdfFile("santrupos.pdf");
    }

    void openPdfFile(String pdfFileName) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(path, pdfFileName);

        intent.setDataAndType(Uri.fromFile(file), "application/pdf");

        startActivity(intent);
    }
    
    public void openAtstojamosiosPdf(View v3) {
        openPdfFile("atstojamosios.pdf");
    }


}
