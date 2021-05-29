package com.drivingassistant.application;

import android.app.Application;

import com.drivingassistant.di.components.ApplicationComponent;
import com.drivingassistant.di.components.DaggerApplicationComponent;
import com.drivingassistant.di.modules.ApplicationModule;


public class DrivingAssistantApplication extends Application {

    private ApplicationComponent applicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        iniAppComponent();
    }

    private void iniAppComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
