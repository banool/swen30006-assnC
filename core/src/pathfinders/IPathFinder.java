package pathfinders;

import java.util.ArrayList;

import utilities.Coordinate;

/**
 * This interface defines the two most important common method signatures
 * for a PathFinder, namely an update method and a way to tell if the
 * PathFinder is done. All PathFinders should implement this interface.
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public interface IPathFinder {

    /**
     * In the provided design class diagram, this method took a SensorData. 
     * Now we just pass a reference to the Sensor to each PathFinder's 
     * constructor so this is no longer necessary.
     * @return The list of Coordinates that we want the car to follow.
     */
    public ArrayList<Coordinate> update();

    public boolean isDone();

}
