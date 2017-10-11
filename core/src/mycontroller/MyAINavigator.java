package mycontroller;

import manoeuvres.DoNothingManoeuvre;
import manoeuvres.Manoeuvre;

public class MyAINavigator {
	
	// Even though we store a manoeuvre here, we pass it back
	// to MyAIController for its update method to be called.
	private Manoeuvre manoeuvre;
	private boolean maneuverHasChanged;
	private MyAIController controller;
	
    public MyAINavigator(MyAIController controller) {
        this.controller = controller;
        
        maneuverHasChanged = true;
        manoeuvre = new DoNothingManoeuvre(controller);
    }
    
    public void update(SensorData sensorData) {
    		// TODO analyse the sensorData and select a manoeuvre.
    		// If we select a new manoeuvre, set maneuverHasChanged to true.
    }
    
    public boolean maneuverHasChanged() {
    		return maneuverHasChanged;
    }
    
    public Manoeuvre getManeuver() {
    		return manoeuvre;
    }
}
