package com.mburakaltun.couriertracking.service;

public interface DistanceCalculator {
    double calculateDistanceInMeters(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude);
}
