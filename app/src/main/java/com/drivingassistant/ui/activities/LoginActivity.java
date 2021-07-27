package com.drivingassistant.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.drivingassistant.R;
import com.drivingassistant.ui.retrofit.IMyService;
import com.drivingassistant.ui.retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class LoginActivity extends FragmentActivity {

    TextView txt_create_account;
    EditText edit_login_email, edit_login_password;
    Button btn_login;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Init Service
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        // Init View
        edit_login_email = (EditText) findViewById(R.id.edit_email);
        edit_login_password = (EditText) findViewById(R.id.edit_password);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(edit_login_email.getText().toString(),
                        edit_login_password.getText().toString());
            }
        });

        txt_create_account = (TextView) findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View register_layout = LayoutInflater.from(LoginActivity.this)
                        .inflate(R.layout.register_layout, null);

                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_user)
                        .setTitle(R.string.str_register_registration)
                        .setDescription(R.string.str_register_note)
                        .setCustomView(register_layout)
                        .setNegativeText(R.string.str_register_cancel)
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveText(R.string.str_register)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MaterialEditText edit_register_email = (MaterialEditText) register_layout.findViewById(R.id.edit_email);
                                MaterialEditText edit_register_name = (MaterialEditText) register_layout.findViewById(R.id.edit_name);
                                MaterialEditText edit_register_password = (MaterialEditText) register_layout.findViewById(R.id.edit_password);

                                if (TextUtils.isEmpty(edit_register_email.getText()))
                                {
                                    Toast.makeText(LoginActivity.this, R.string.str_register_warn_email, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (TextUtils.isEmpty(edit_register_name.getText()))
                                {
                                    Toast.makeText(LoginActivity.this, R.string.str_register_warn_name, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (TextUtils.isEmpty(edit_register_password.getText()))
                                {
                                    Toast.makeText(LoginActivity.this, R.string.str_register_warn_password, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                registerUser(edit_register_email.getText().toString(),
                                        edit_register_name.getText().toString(),
                                        edit_register_password.getText().toString());
                            }
                        }).show();


            }
        });
    }

    private void registerUser(String email, String name, String password) {
        compositeDisposable.add(iMyService.registerUser(email, name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject jsonResponse = new JSONObject(response);
                        String result = jsonResponse.getString("result");
                        Toast.makeText(LoginActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loginUser(String email, String password) {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this, R.string.str_register_warn_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this, R.string.str_register_warn_password, Toast.LENGTH_SHORT).show();
            return;
        }

        compositeDisposable.add(iMyService.loginUser(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject jsonResponse = new JSONObject(response);
                        String result = jsonResponse.getString("result");
                        if(result.compareTo("Login success!") != 0){
                            Toast.makeText(LoginActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String name = jsonResponse.getString("name");
                        Toast.makeText(LoginActivity.this, R.string.str_login_hello + name + "!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }));
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