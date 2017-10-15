package mycontroller;

import java.util.ArrayList;
import java.util.Stack;

import controller.CarController;
import pathfinders.IPathFinder;
import pathfinders.PathFinderExplore;
import pathfollowers.IPathFollower;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController {
    
    private final float CAR_SPEED = 3;

    /* TODO Comment this! */
    private MyAISensor sensor;
    private IPathFinder activePathFinder;
    private Stack<IPathFinder> pathFinderStack;
    private IPathFollower pathFollower;
    
    private SensorData latestSensorData;
    private ArrayList<Coordinate> coordsToFollow;

    public MyAIController(Car car) {
        super(car);
        sensor = new MyAISensor(this);
        // TODO comment, empty stack at the start.
        pathFinderStack = new Stack<IPathFinder>();
        // TODO comment set the starting pathfinder to Explore.
        // Something about defaults, maybe this could be more configurable.
        // Probably not something we have to worry about though.
        activePathFinder = new PathFinderExplore(pathFinderStack);
    }
    
    public void update(float delta) {
    		latestSensorData = sensor.update();
    		if (activePathFinder.isDone()) {
    		    activePathFinder = pathFinderStack.pop();
    		}
        coordsToFollow = activePathFinder.update(latestSensorData);
        pathFollower.update(this, delta, coordsToFollow, latestSensorData);
    }
    
    public float getTopSpeed() {
    		return CAR_SPEED;
    }

}
