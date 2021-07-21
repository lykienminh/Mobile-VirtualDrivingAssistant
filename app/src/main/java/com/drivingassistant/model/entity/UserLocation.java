package com.drivingassistant.model.entity;

import java.util.Date;

public class UserLocation {
    public String location;
    public String time;
    public int id;

    public UserLocation(int id, String location, String time) {
        this.id = id;
        this.location = location;
        this.time = time;
    }
}
