package mycontroller;

import java.util.HashMap;

import controller.CarController;
import manoeuvres.Manoeuvre;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController {
    
    private final float CAR_SPEED = 3;

    /* TODO Comment this! */
    private MyAISensor sensor;
    private MyAINavigator navigator;
    private Manoeuvre activeManoeuvre;
    private SensorData latestSensorData;

    public MyAIController(Car car) {
        super(car);
        sensor = new MyAISensor(this);
        navigator = new MyAINavigator(this);
    }
    
    public void update(float delta) {
    		latestSensorData = sensor.getSensorData();
    		activeManoeuvre = navigator.update(latestSensorData);
    		activeManoeuvre.update(delta, latestSensorData);
    }
    
    public float getTopSpeed() {
    		return CAR_SPEED;
    }

}
