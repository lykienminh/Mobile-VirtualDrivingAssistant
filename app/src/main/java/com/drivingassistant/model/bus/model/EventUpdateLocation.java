package com.drivingassistant.model.bus.model;

import com.drivingassistant.model.bus.EventModel;
import com.drivingassistant.model.entity.Data;

public class EventUpdateLocation implements EventModel {

    private Data data;

    public EventUpdateLocation(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

}
