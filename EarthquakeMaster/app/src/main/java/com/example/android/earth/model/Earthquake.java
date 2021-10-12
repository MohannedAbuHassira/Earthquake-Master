package com.example.android.earth.model;

public class Earthquake {

    private String place;
    private double magnitude;
    private String time;
    private String linkDetail;
    private double lat;
    private double lon;

    public Earthquake(String place, double magnitude, String time, String linkDetail, double lat, double lon) {
        this.place = place;
        this.magnitude = magnitude;
        this.time = time;
        this.linkDetail = linkDetail;
        this.lat = lat;
        this.lon = lon;
    }

    public Earthquake(){}

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLinkDetail() {
        return linkDetail;
    }

    public void setLinkDetail(String linkDetail) {
        this.linkDetail = linkDetail;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
