package com.giang.topdf;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button createpdf = this.findViewById(R.id.createpdf);
        createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu createpopup = new PopupMenu(MainActivity.this,createpdf);
                createpopup.getMenuInflater().inflate(R.menu.popup_selection_mode, createpopup.getMenu());
                createpopup.show();
            }
        });
    }
}