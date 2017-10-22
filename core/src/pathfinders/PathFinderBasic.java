package pathfinders;

import java.util.ArrayList;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * This class serves as the parent of PathFinderExplore and PathFinderEscape. We
 * realised that these two share very common behaviour, with the only major
 * differences being the end condition and what to push onto the stack. Lots of
 * the wall following related behaviour is encapsulated here.
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public abstract class PathFinderBasic implements IPathFinder {

    /** The tile types that we want the car to avoid running in to. */
    protected final ArrayList<MapTile.Type> tileTypesToAvoid;
    /** The tile types that we want to go to, namely Road. */
    protected final ArrayList<MapTile.Type> tileTypesToTarget;

    /** The stack of IPathFinders, initially passed in from MyAIController. */
    protected Stack<IPathFinder> pathFinderStack;
    /** The Sensor, initially passed in from MyAIController. */
    protected Sensor sensor;

    /** Where the car was when this path finder was instantiated. */
    protected Coordinate start;
    /**
     * This is used to make sure the path finder doesn't think it is done the moment
     * it is instantiated.
     */
    protected boolean hasLeftStart;

    /**
     * Here we mainly just set which traps we want to target and avoid.
     * 
     * @param pathFinderStack
     *            The stack of IPathFinders
     * @param sensor
     *            The Sensor that was passed from the controller.
     */
    public PathFinderBasic(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        tileTypesToAvoid = new ArrayList<MapTile.Type>();
        tileTypesToAvoid.add(MapTile.Type.WALL);
        tileTypesToAvoid.add(MapTile.Type.TRAP);
        tileTypesToTarget = new ArrayList<MapTile.Type>();
        tileTypesToTarget.add(MapTile.Type.ROAD);

        this.sensor = sensor;
        this.start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
        this.hasLeftStart = false;
    }

    /**
     * This method gets the car adjacent to one of the tiles held within
     * tileTypesToAvoid.
     * 
     * @return A list of Coordinates to follow in order to get to a wall/trap/etc.
     */
    protected ArrayList<Coordinate> goToWallTrap() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        Coordinate wallCoordinate = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                tileTypesToTarget);
        Coordinate targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(wallCoordinate, tileTypesToTarget);
        if (targetCoordinate != null) {
            if (targetCoordinate.equals(sensor.getPosition())) {
                // We're already at the target, so move the target forward.
                int[] modMap = WorldSpatial.modMap.get(sensor.getOrientation());
                targetCoordinate = new Coordinate(targetCoordinate.x + modMap[0] * sensor.getVisionAhead(),
                        targetCoordinate.y + modMap[1] * sensor.getVisionAhead());
                targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(targetCoordinate, tileTypesToTarget);
            }
            target.add(targetCoordinate);
        } else {
            // If we get here it means that there is no wall within range.
            // Just return the furthest point to the north within vision.
            target.add(sensor.getFurthestPointInDirection(WorldSpatial.Direction.NORTH));
        }
        return target;
    }

    /**
     * This method makes the car follow a wall hat it is beside. It shouldn't be
     * called until the car is adjacent to a wall.
     * 
     * @return A list of Coordinates to follow to get the car to travel along a wall
     */
    protected ArrayList<Coordinate> followWallTrap() {
        // We're beside a wall/trap, so follow it, avoiding walls and traps in front.
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        if (!sensor.isFollowingTileTypes(getTileTypesToAvoid())) {
            // We're beside a wall, but not aligned with it.
            // Say that we need to turn left.
            Coordinate closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                    tileTypesToTarget);
            if (closestToSide == null) {
                // Turning left would crash us into a wall/trap, turn right instead.
                closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.RIGHT,
                        tileTypesToTarget);
                if (closestToSide == null) {
                    // Turning right would also crash us into a wall, turn on the spot.
                    closestToSide = sensor.getPosition();
                }
            }
            target.add(closestToSide);
        } else {
            // We're aligned with the wall/trap beside us, just go in the direction we're
            // facing.
            Coordinate wallInFront = sensor.getClosestTileInDirectionOfTypes(sensor.getOrientation(),
                    getTileTypesToAvoid());
            if (wallInFront == null) {
                // There's no wall/trap in front in vision, just gun it forward.
                target.add(sensor.getFurthestPointInDirection(sensor.getOrientation()));
            } else {
                // There's a wall coming up, target as far to the right of it as we can.
                Coordinate rightTarget = goToRightOfUpcomingWall(wallInFront);
                target.add(rightTarget);
            }
        }
        return target;
    }

    /**
     * This method is used to get the car to avoid crashing into oncoming tiles to
     * avoid. To do this it finds the point furthest to the right along the upcoming
     * wall and targets it.
     * 
     * @param wallInFront
     *            The Coordinate of the incoming wall
     * @return A list of Coordinates to follow to get the car to avoid crashing into
     *         the wall
     */
    protected Coordinate goToRightOfUpcomingWall(Coordinate wallInFront) {
        // For now just use the point beside the upcoming wall so we slow down.
        Coordinate roadNearWall = sensor.getNearestTileOfTypesNearCoordinate(wallInFront, tileTypesToTarget);
        // Using this point, check to the right of it for free road tiles to go to.
        Coordinate rightOfRoadNearWall = roadNearWall;
        for (int i = 0; i <= sensor.getVisionAhead(); i++) {
            int[] rightModMap = WorldSpatial.modMap.get(WorldSpatial.getRightOf(sensor.getOrientation()));
            Coordinate candidate = new Coordinate(roadNearWall.x + (i * rightModMap[0]),
                    roadNearWall.y + (i * rightModMap[1]));
            // If the coordinate to the right of the point near the wall is a road, set the
            // destination to that point.
            if (tileTypesToTarget.contains(sensor.getCurrentView().get(candidate).getType())) {
                rightOfRoadNearWall = candidate;
            }
        }
        return rightOfRoadNearWall;
    }

    /**
     * @return the tileTypesToAvoid
     */
    public ArrayList<MapTile.Type> getTileTypesToAvoid() {
        return tileTypesToAvoid;
    }

}
