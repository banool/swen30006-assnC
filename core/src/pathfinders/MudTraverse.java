package pathfinders;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.Stack;

import mycontroller.Sensor;

public class MudTraverse extends TrapTraverse {

    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;

    public MudTraverse(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        this.sensor = sensor;
        start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
    }

    @Override
    public ArrayList<Coordinate> update() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean isDone() {
        // METHOD STUB
        return true;
    }

}
