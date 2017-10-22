package pathfollowers;

import java.util.ArrayList;

import utilities.Coordinate;

public interface IPathFollower {
    
    // TODO Comment on why classes implementing this interface need these arguments/this information.
    public void update(float delta, ArrayList<Coordinate> coordsToFollow);

}
