package com.berkozer.do2getherfinal.Models;

/**
 * Created by berkozer on 19/04/2017.
 */

public class StepsModel {
    public String distance;
    public String duration;
    public String instructions;
    public String maneuver;

    public StepsModel() {

    }

    public StepsModel(String distance, String duration, String instructions) {
        this.distance = distance;
        this.duration = duration;
        this.instructions = instructions;
    }

    public StepsModel(String distance, String duration, String instructions, String maneuver) {
        this.distance = distance;
        this.duration = duration;
        this.instructions = instructions;
        this.maneuver = maneuver;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }


}
