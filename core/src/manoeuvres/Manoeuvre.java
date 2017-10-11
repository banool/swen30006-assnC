package manoeuvres;

import mycontroller.MyAIController;
import mycontroller.SensorData;

/**
 * IManeuver interface. It defines a uniform updated method.
 * Any necessary customisation is expected to come from the
 * subclass' constructor.
 * @author daniel
 *
 */
public abstract class Manoeuvre {
	
	// TODO so we don't have to use getController.
	protected MyAIController controller;
	
	public Manoeuvre(MyAIController controller) {
		this.controller = controller;
	}
	
	public abstract void update(float delta, SensorData sensorData);

}
