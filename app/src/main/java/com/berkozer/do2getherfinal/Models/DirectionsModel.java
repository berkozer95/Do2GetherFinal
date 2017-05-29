package com.berkozer.do2getherfinal.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by berkozer on 13/04/2017.
 */

public class DirectionsModel implements Serializable {

    private String startAddress;
    private String endAddress;
    private String totalDistance;
    private String totalDuration;
    private ArrayList<StepsModel> steps = new ArrayList<StepsModel>();

    public DirectionsModel(){

    }

    public DirectionsModel(String startAddress, String endAddress, String totalDistance, String totalDuration, ArrayList<StepsModel> steps) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.steps = steps;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public ArrayList<StepsModel> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepsModel> steps) {
        this.steps = steps;
    }


}
