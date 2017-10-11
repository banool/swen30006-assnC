package manoeuvres;

import mycontroller.MyAIController;
import mycontroller.SensorData;


public class DoNothingManoeuvre extends Manoeuvre {
	
	public DoNothingManoeuvre(MyAIController controller) {
		super(controller);
	}
	
	public void update(float delta, SensorData sensorData) {
		
	}
}
