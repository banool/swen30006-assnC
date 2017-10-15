package pathfollowers;

import java.util.ArrayList;

import mycontroller.MyAIController;
import mycontroller.SensorData;
import utilities.Coordinate;

public interface IPathFollower {
    
    // TODO Comment on why classes implementing this interface need these arguments/this information.
    public void update(MyAIController controller, float delta, ArrayList<Coordinate> coordsToFollow, SensorData latestSensorData);

}