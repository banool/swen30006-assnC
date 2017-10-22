package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * This PathFinder is responsible for getting the car adjacent to a trap section.
 * After a PathFinderExplore has identified the traps in a "room", it (or something
 * else) can create this to get beside one of those traps. There should be one of
 * these pushed onto the stack for each identified trap section. Once this PathFinder
 * gets adjacent to the trap section, its job is done.
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public class PathFinderEscape extends PathFinderBasic {

    /** This targetTrapSection represents the trap that this escape is trying to get to. */
    private HashMap<Coordinate, TrapTile> targetTrapSection;
    /** This is for a road tile besides the target trap section. */
    private Coordinate target;

    /**
     * This constuctor just sets attributes and calls the super constructor.
     * @param pathFinderStack The stack of IPathFinders, passed in from an explore
     * @param sensor The Sensor created by MyAIController, passed down
     * @param targetTrapSection The trap section that this escape is responsible for getting to
     */
    public PathFinderEscape(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> targetTrapSection) {
        super(pathFinderStack, sensor);
        this.targetTrapSection = targetTrapSection;
        // While we don't yet know the target, set it to where we start.
        // Because of hasLeftStart, this will work fine.
        this.target = sensor.getPosition();
    }

    /**
     * This method is responsible for returning an ArrayList of Coordinates
     * for the PathFollower to follow. In this basic implementation, the
     * ArrayList just has a single point, but in more complex implementations
     * there could be multiple points defining a path to follow.
     * @return A list of Coordinates to follow
     */
    @Override
    public ArrayList<Coordinate> update() {
        // Keep an eye out for the target trap section. Once it comes into
        // view, we select a target piece of road beside it and go there.
        getTargetAdjecentToTrapSection();
        // TODO comment on why we need this "hasLeftStart" thing.
        if (!hasLeftStart && !start.equals(sensor.getPosition())) {
            hasLeftStart = true;
        }
        if (!sensor.isDirectlyBesideTileOfTypes(tileTypesToAvoid, WorldSpatial.RelativeDirection.LEFT)) {
            System.out.println("Getting adjacent to wall/trap");
            return super.goToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return super.followWallTrap();
        }
    }
    
    /**
     * This method sets this.target to the Coordinate of the road tile beside the
     * trap section that we're trying to target once it comes into vision.
     */
    private void getTargetAdjecentToTrapSection() {
        for (Map.Entry<Coordinate, MapTile> entry : sensor.getCurrentView().entrySet()) {
            if (this.targetTrapSection.containsKey(entry.getKey())) {
                this.target = sensor.getNearestTileOfTypesNearCoordinate(entry.getKey(), tileTypesToTarget);
                break;
            }
        }
    }

    /**
     * This method allows us to do something before we're done.
     * In this case, we push an Escape pathfinder onto the stack for each trap section in trapSections.
     * Each escape pathfinder takes the appropriate trap section as a constructor argument.
     * @return true if the PathFinder is done, false otherwise
     */
    public boolean isDone() {
        boolean done = sensor.getPosition().equals(target) && hasLeftStart;
        if (done) {
            pathFinderStack.push(TrapTraverse.getTrapTraverse(pathFinderStack, sensor, targetTrapSection));
        }
        return done;
    }

}
