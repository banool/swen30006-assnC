package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public interface IPathFinder {
    
    public ArrayList<Coordinate> update(SensorData sensorData);
    
    public boolean isDone();

}
