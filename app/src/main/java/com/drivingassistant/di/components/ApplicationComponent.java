package com.drivingassistant.di.components;

import android.content.Context;

import com.drivingassistant.di.modules.ApplicationModule;
import com.drivingassistant.managers.SharedPreferencesManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context context();
    SharedPreferencesManager sharedPreferences();

}
