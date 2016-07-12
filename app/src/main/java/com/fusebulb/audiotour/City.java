package com.fusebulb.audiotour;

import android.location.Location;
/**
 * Created by amiteshmaheshwari on 07/07/16.
 */
public class City {
    private int id;
    private String name;
    private Location location;
    private String picture;


    public City() {

    }

    public City(int id, String name, double lat, double lon, String photo_path) {
        this.id = id;
        this.name = name;
        this.location = new Location("manual");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
        this.picture = photo_path;
    }

    public void setId(String id){

        this.id = Integer.parseInt(id);
    }

    public void setId(int id){

        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLocation(double lat, double lon){
        this.location = new Location("manual");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
    }

    public void setLocation(String lat_string, String lon_string){
        this.location = new Location("manual");
        this.location.setLatitude(Double.parseDouble(lat_string));
        this.location.setLongitude(Double.parseDouble(lon_string));
    }

    public void setPicture(String photo_path) {
        this.picture = photo_path;
    }

    public Integer getId(){
        return this.id;
    }

    public Location getLocation(){
        return this.location;
    }

    public String getPicture(){
        return this.picture;
    }

    public String getName(){
        return this.name;
    }
    //FeaturedPhoto
}
