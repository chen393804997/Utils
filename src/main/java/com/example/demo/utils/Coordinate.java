package com.example.demo.utils;

import java.io.Serializable;

/**
 * @author: czw
 * @create: 2018-10-28 13:47
 **/
public class Coordinate implements Serializable {
    private static final long serialVersionUID = -6041688840734998446L;
    private double longitude;
    private double latitude;
    private String id;
    private String tableName;
    private int currentPage;
    private int pageSize;
    private int mode;
    private Double speed;

    private transient Integer distance;


    private Integer sex;

    public Coordinate() {
    }

    public Coordinate(String id, String tableName) {
        this.id = id;
        this.tableName = tableName;
    }

    public Coordinate(double longitude, double latitude, String id, String tableName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.tableName = tableName;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Integer getSex() {
        return sex;
    }
    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public int getCurrentPage() {
        if(currentPage == 0) {
            currentPage = 1;
        }
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getPageSize() {
        if(pageSize == 0) {
            pageSize = 10;
        }
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getDistance() {
        return distance;
    }
    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    @Override
    public String toString() {
        return "Coordinate [longitude=" + longitude + ", latitude=" + latitude + ", id=" + id + ", tableName="
                + tableName + ", currentPage=" + currentPage + ", pageSize=" + pageSize + ", sex=" + sex + "]";
    }



}
