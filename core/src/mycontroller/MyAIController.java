package mycontroller;

import java.util.ArrayList;
import java.util.Stack;

import controller.CarController;
import manoeuvres.Manoeuvre;
import pathfinders.IPathFinder;
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
        activePathFinder = new PathFinderExplore(this);
    }
    
    public void update(float delta) {
    		latestSensorData = sensor.getSensorData();
    		if (activePathFinder.isDone()) {
    		    activePathFinder = pathFinderStack.pop();
    		}
        coordsToFollow = activePathFinder.update(latestSensorData, pathFinderStack);
        pathFollower.update(delta, coordsToFollow, latestSensorData);
    }
    
    public float getTopSpeed() {
    		return CAR_SPEED;
    }

}
