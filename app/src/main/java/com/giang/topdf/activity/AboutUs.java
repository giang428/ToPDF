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
                .addItem(new Element().setTitle(getString(R.string.aboutme_version) + BuildConfig.VERSION_NAME).setGravity(Gravity.CENTER_HORIZONTAL))
                .addItem(new Element().setTitle(getString(R.string.aboutme_author)).setGravity(Gravity.CENTER_HORIZONTAL)).setImage(R.mipmap.ic_launcher_round)
                .setDescription(getString(R.string.aboutme_description))
                .addEmail(getString(R.string.aboutme_email), getString(R.string.aboutme_gmailcontact))
                .addFacebook(getString(R.string.aboutme_fbId), getString(R.string.aboutme_contactfb))
                .addGitHub(getString(R.string.aboutme_githubId), getString(R.string.aboutme_github))
                .create();
        setTitle(getString(R.string.aboutme_a));
        setContentView(about);
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}