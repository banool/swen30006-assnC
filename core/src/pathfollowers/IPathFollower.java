package pathfollowers;

import java.util.ArrayList;

import utilities.Coordinate;

/**
 * This interface defines the common methods that IPathFollowers must implement,
 * namely just an update method. IPathFollowers are responsible for moving the
 * car towards the Coordinates that have been provided by an IPathFinder.
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public interface IPathFollower {

    /**
     * Classes implementing this interface will be in control of moving the car.
     * This information is used to help guide the car in doing this.
     * 
     * @param delta
     *            Delta for since last time step.
     * @param coordsToFollow
     *            The list of Coordinates that the car should follow.
     */
    public void update(float delta, ArrayList<Coordinate> coordsToFollow);

}
