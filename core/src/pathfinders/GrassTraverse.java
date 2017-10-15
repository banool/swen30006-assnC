package pathfinders;

import mycontroller.SensorData;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.Stack;

public class GrassTraverse extends TrapTraverse {

    public GrassTraverse(Stack<IPathFinder> pathFinderStack) {
        // STUB
    }

    // to the stack so this IPathFinder can push a new IPathFinder onto the stack.
    public ArrayList<Coordinate> update(SensorData sensorData) {

        // METHOD STUB
        return new ArrayList<Coordinate>();
    }

    public boolean isDone() {

        // METHOD STUB
        return true;
    }

}
