package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class PathFinderExplore implements IPathFinder {

    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    private Sensor sensor;

    private Coordinate start;

    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        this.sensor = sensor;
        start = sensor.getPosition();
    }

    @Override
    public ArrayList<Coordinate> update() {
        System.out.println(sensor.getOrientation());
        if (!sensor.isFollowingWall()) {
            return getToWall();
        } else {
            // We're near a wall, follow it until we come across an obstacle.
            ArrayList<Coordinate> target = new ArrayList<Coordinate>();
            target.add(sensor.getFurtherPointInDirection(sensor.getOrientation()));
            return target;
            // TODO deal with avoiding obstacles.
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
    }

    private ArrayList<Coordinate> getToWall() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        Coordinate targetDirection = sensor.getNearestWall();
        if (targetDirection != null) {
            target.add(targetDirection);
            return target;
        }
        // If we get here it means that there is no wall within range.
        // Just return the furthest point to the north within vision.
        target.add(sensor.getFurtherPointInDirection(WorldSpatial.Direction.NORTH));
        return target;
    }

    @Override
    public boolean isDone() {
        return start == sensor.getPosition();
    }

}
