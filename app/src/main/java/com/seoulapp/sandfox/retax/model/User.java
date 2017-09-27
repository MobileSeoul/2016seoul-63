package com.seoulapp.sandfox.retax.model;

import java.util.Date;

/**
 * Firebase Database 저장하기 위한 모델클래스
 */

public class User {
    public Boolean tourist_status;
    public String continent;
    public String gender;
    public String age_group;
    public Float rate;
    public Date submitted_date;
    public Date search_time;
    public Date clicked_time;
    public String search_keywords;
    public String store;
    public String district;
    public String type;
    public String root;
    public String index;


    public User() {
    }

    public User(boolean tourist_status, String continent, String gender, String age_group, float rate, Date today) {
        this.tourist_status = tourist_status;
        this.continent = continent;
        this.gender = gender;
        this.age_group = age_group;
        this.rate = rate;
        this.submitted_date=today;
    }

    public User(Date search_time, String search_keywords) {
        this.search_time = search_time;
        this.search_keywords = search_keywords;
    }

    public User(String root, String store, String district, String type, Date clickedTime){
        this.root = root;
        this.store = store;
        this.district = district;
        this.type = type;
        this.clicked_time = clickedTime;
    }

    public User(String root, String index, String type, Date clickedTime){
        this.root = root;
        this.index = index;
        this.type = type;
        this.clicked_time = clickedTime;
    }

    public String getIndex() {
        return index;
    }

    public Date getClicked_time() {
        return clicked_time;
    }

    public String getRoot() {
        return root;
    }

    public String getType() {
        return type;
    }

    public String getDistrict() {
        return district;
    }

    public String getStore() {
        return store;
    }

    public Boolean isTourist_status() {
        return tourist_status;
    }

    public String getContinent() {
        return continent;
    }

    public String getGender() {
        return gender;
    }

    public String getAge_group() {
        return age_group;
    }

    public Float getRate() {
        return rate;
    }

    public Date getSubmitted_date() {
        return submitted_date;
    }

    public Date getSearch_time() {
        return search_time;
    }

    public String getSearch_keywords() {
        return search_keywords;
    }

    @Override
    public String toString() {
        return tourist_status + "/" + continent + "/" + gender + "/" + age_group + "/" + rate + "/" + submitted_date;
    }
}
