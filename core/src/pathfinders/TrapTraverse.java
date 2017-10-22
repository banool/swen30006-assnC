package pathfinders;

import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;

/**
 * This abstract class is the superclass for the various TrapTraverse classes. A
 * TrapTraverse is responsible for getting over a certain type of trap, given
 * that the car is already adjacent to said trap section. This class also holds
 * a useful factory method for determining the correct TrapTraverse class to
 * instantiate given a trap section.
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 *
 */
public abstract class TrapTraverse implements IPathFinder {

    /**
     * Boolean for subclasses to use to store info about whether they've finished
     * traversing their trap.
     */
    private boolean hasTraversedTrap;

    /**
     * This is essentially a factory method that returns the appropriate
     * TrapTraverse strategy based on a given trap section. Currently it just looks
     * at an element in the trap section and decides based off that, though in the
     * future it could determine the most common element in the trap section and
     * then decide, or even have a mixed trap traverse strategy.
     * 
     * @param trapSection
     * @return An instantiated TrapTraverse appropriate for the given trapSection.
     */
    public static TrapTraverse getTrapTraverse(Stack<IPathFinder> pathFinderStack, Sensor sensor,
            HashMap<Coordinate, TrapTile> trapSection) {
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

    public boolean getHasTraversedTrap() {
        return this.hasTraversedTrap;
    }

    public void setHasTraversedTrap(boolean hasTraversedTrap) {
        this.hasTraversedTrap = hasTraversedTrap;
    }

}
