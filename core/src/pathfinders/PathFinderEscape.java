package pathfinders;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.TrapTile;

public class PathFinderEscape implements IPathFinder {
    
    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;
    HashMap<Coordinate, TrapTile> trapSection;

    public PathFinderEscape(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> trapSection) {
        this.sensor = sensor;
        start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
        this.trapSection = trapSection;
    }

    @Override
    public ArrayList<Coordinate> update() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }
}
