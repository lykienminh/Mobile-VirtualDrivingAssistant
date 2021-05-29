package com.drivingassistant.model.bus.model;

import com.drivingassistant.model.bus.EventModel;
import com.drivingassistant.model.entity.GpsStatusEntity;

public class EventUpdateStatus implements EventModel {

    private GpsStatusEntity status;

    public EventUpdateStatus(GpsStatusEntity status) {
        this.status = status;
    }

    public GpsStatusEntity getStatus() {
        return status;
    }

}

