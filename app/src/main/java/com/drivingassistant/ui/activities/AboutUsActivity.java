package com.drivingassistant.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drivingassistant.R;

import java.util.Locale;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences firstUse = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String isFirstUse = firstUse.getString("firstUse", "Not use");

        loadLocale();
        super.onCreate(savedInstanceState);
        if(isFirstUse.equals("Not use")) {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putString("firstUse", "Used");
            editor.apply();

            setContentView(R.layout.activity_aboutus);

            Button changLang = findViewById(R.id.btn_changLang);
            changLang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                    editor.putString("firstUse", "Not use");
                    editor.apply();
                    showChangeLanguageDialog();
                }
            });
        }
        else {
            goHomepage();
        }
    }

    public void goHomepage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Tiếng Việt", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AboutUsActivity.this);
        mBuilder.setTitle(R.string.str_select_lang);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("vi");
                    recreate();
                }
                else if (which == 1) {
                    setLocale("en");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("MyLang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("MyLang", "");
        setLocale(lang);
    }
}
