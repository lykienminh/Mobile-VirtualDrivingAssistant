package com.drivingassistant.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private com.drivingassistant.application.DrivingAssistantApplication context;

    public ApplicationModule(com.drivingassistant.application.DrivingAssistantApplication context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideApplicationContext(){
        return context;
    }

}
