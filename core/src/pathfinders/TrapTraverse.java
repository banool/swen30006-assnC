package pathfinders;

import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;

public abstract class TrapTraverse implements IPathFinder {

    private boolean hasTraversedTrap;
    
    /**
     * This is essentially a factory method that returns the appropriate TrapTraverse strategy
     * based on a given trap section. Currently it just looks at an element in the trap section
     * and decides based off that, though in the future it could determine the most common
     * element in the trap section and then decide, or even have a mixed trap traverse strategy.
     * @param trapSection
     * @return TODO
     */
    public static TrapTraverse getTrapTraverse(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> trapSection) {
        TrapTile trapTile = trapSection.entrySet().iterator().next().getValue();
        if (trapTile instanceof MudTrap) {
            return new MudTraverse(pathFinderStack, sensor, trapSection);
        } else if (trapTile instanceof GrassTrap) {
            return new GrassTraverse(pathFinderStack, sensor, trapSection);
        } else if (trapTile instanceof LavaTrap) {
            return new LavaTraverse(pathFinderStack, sensor, trapSection);
        } else {
            return null;
        }
    }

}
