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

public class PathFinderEscape extends PathFinderBasic {

    // TODO comment this
    private HashMap<Coordinate, TrapTile> targetTrapSection;
    private Coordinate target;

    public PathFinderEscape(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> targetTrapSection) {
        super(pathFinderStack, sensor);
        this.targetTrapSection = targetTrapSection;
        // While we don't yet know the target, set it to where we start.
        // Because of hasLeftStart, this will work fine.
        this.target = sensor.getPosition();
    }

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
            return super.getToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return super.followWallTrap();
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
    }
    
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
     */
    public boolean isDone() {
        boolean done = sensor.getPosition().equals(target) && hasLeftStart;
        if (done) {
            pathFinderStack.push(TrapTraverse.getTrapTraverse(pathFinderStack, sensor, targetTrapSection));
        }
        return done;
    }

}
