package com.drivingassistant.di.components;

import com.drivingassistant.di.scopes.ScreenScope;
import com.drivingassistant.ui.activities.DetectorActivity;

import dagger.Component;

@ScreenScope
@Component(dependencies = ApplicationComponent.class)
public interface ScreenComponent {

    void inject(DetectorActivity detectorActivity);
}
