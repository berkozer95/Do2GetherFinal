package com.berkozer.do2getherfinal.Models;

/**
 * Created by berkozer on 28/04/2017.
 */

public class TransitModel {
    public String arrivalStop;
    public String arrivalTime;
    public String departureStop;
    public String departureTime;
    public String headsign;
    public String vechileType;
    public String transitName;
    public String transitShortName;
    public int numOfStops;

    public TransitModel() {

    }

    public TransitModel(String arrivalStop, String arrivalTime, String departureStop, String departureTime, String headsign, String vechileType, String transitName, String transitShortName, int numOfStops) {
        this.arrivalStop = arrivalStop;
        this.arrivalTime = arrivalTime;
        this.departureStop = departureStop;
        this.departureTime = departureTime;
        this.headsign = headsign;
        this.vechileType = vechileType;
        this.transitName = transitName;
        this.transitShortName = transitShortName;
        this.numOfStops = numOfStops;
    }

    public String getArrivalStop() {
        return arrivalStop;
    }

    public void setArrivalStop(String arrivalStop) {
        this.arrivalStop = arrivalStop;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureStop() {
        return departureStop;
    }

    public void setDepartureStop(String departureStop) {
        this.departureStop = departureStop;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getVechileType() {
        return vechileType;
    }

    public void setVechileType(String vechileType) {
        this.vechileType = vechileType;
    }

    public String getTransitName() {
        return transitName;
    }

    public void setTransitName(String transitName) {
        this.transitName = transitName;
    }

    public String getTransitShortName() {
        return transitShortName;
    }

    public void setTransitShortName(String transitShortName) {
        this.transitShortName = transitShortName;
    }

    public int getNumOfStops() {
        return numOfStops;
    }

    public void setNumOfStops(int numOfStops) {
        this.numOfStops = numOfStops;
    }
}
