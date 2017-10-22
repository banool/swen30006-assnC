package pathfinders;

import java.util.ArrayList;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * This class serves as the parent of PathFinderExplore and PathFinderEscape.
 * We realised that these two share very common behaviour, with the only major
 * differences being the end condition and what to push onto the stack.
 * Lots of the wall following related behaviour is encapsulated here.
 * @author daniel
 *
 */
public abstract class PathFinderBasic implements IPathFinder {

    protected final ArrayList<MapTile.Type> tileTypesToAvoid;
    protected final ArrayList<MapTile.Type> tileTypesToTarget;
    
    protected Stack<IPathFinder> pathFinderStack;
    protected Sensor sensor;

    protected Coordinate start;
    protected boolean hasLeftStart;

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

    protected ArrayList<Coordinate> getToWallTrap() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        Coordinate wallCoordinate = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                tileTypesToTarget);
        Coordinate targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(wallCoordinate, tileTypesToTarget);
        if (targetCoordinate != null) {
            System.out.println("pre target " + targetCoordinate);
            if (targetCoordinate.equals(sensor.getPosition())) {
                // We stop at the target, so move the target forward.
                int[] modMap = WorldSpatial.modMap.get(sensor.getOrientation());
                targetCoordinate = new Coordinate(targetCoordinate.x + modMap[0]*sensor.getVisionAhead(), targetCoordinate.y + modMap[1]*sensor.getVisionAhead());
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
    
    protected ArrayList<Coordinate> followWallTrap() {
        // We're beside a wall/trap, follow it, avoiding walls and traps in front.
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
                // TODO If we're in a corridor, this might just crash into the right wall.
            }
            target.add(closestToSide);
        } else {
            // We're aligned with the wall/trap beside us, just go in the direction we're
            // facing.
            Coordinate wallInFront = sensor.getClosestTileInDirectionOfTypes(sensor.getOrientation(), getTileTypesToAvoid());
            if (wallInFront == null) {
                // There's no wall/trap in front in vision, just gun it forward.
                target.add(sensor.getFurthestPointInDirection(sensor.getOrientation()));
            } else {
                System.out.println("Getting to the right of upcoming wall");
                Coordinate rightTarget = getToRightOfUpcomingWall(wallInFront);
                target.add(rightTarget);
            }
        }
        return target;
    }
    
    protected Coordinate getToRightOfUpcomingWall(Coordinate wallInFront) {
        // For now just use the point beside the upcoming wall so we slow down.
        System.out.println("wall in front: " + wallInFront);
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
