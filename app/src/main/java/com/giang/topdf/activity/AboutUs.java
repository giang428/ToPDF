package com.giang.topdf.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.BuildConfig;
import com.giang.topdf.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View about = new AboutPage(this)
                .addItem(new Element().setTitle("Version: " + BuildConfig.VERSION_NAME).setGravity(Gravity.CENTER_HORIZONTAL))
                .addItem(new Element().setTitle("Author: Nguyen Dinh Truong Giang").setGravity(Gravity.CENTER_HORIZONTAL))          .setImage(R.mipmap.ic_launcher_round)
                .setDescription("ToPDF is a simple app to create PDF files")
                .addEmail("giangcua3d@gmail.com","Contact me via Gmail")
                .addFacebook("giangtruong01","Contact me via Facebook")
                .addGitHub("giang428","Github page")
                .create();
        setTitle("About me");
        setContentView(about);
    }
    @Override
    public void onBackPressed(){
        super.finish();
    }
}