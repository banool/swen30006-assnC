package pathfinders;

import java.util.ArrayList;

import mycontroller.SensorData;
import utilities.Coordinate;

public interface IPathFinder {
    
    // TODO comment, specifically about how the update method needs a reference
    // to the stack so this IPathFinder can push a new IPathFinder onto the stack.
    public ArrayList<Coordinate> update(SensorData sensorData);
    
    public boolean isDone();

}