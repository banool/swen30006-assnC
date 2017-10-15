package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public interface IPathFollower {
    
    public void update(float delta, ArrayList<Coordinate> coordsToFollow, SensorData latestSensorData);

}
