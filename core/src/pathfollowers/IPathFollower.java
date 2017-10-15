package pathfollowers;

import java.util.ArrayList;

import mycontroller.SensorData;
import utilities.Coordinate;

public interface IPathFollower {
    
    public void update(float delta, ArrayList<Coordinate> coordsToFollow, SensorData latestSensorData);

}
