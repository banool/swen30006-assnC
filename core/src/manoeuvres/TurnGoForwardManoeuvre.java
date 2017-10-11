package manoeuvres;

import mycontroller.MyAIController;
import mycontroller.SensorData;
import world.WorldSpatial;

/**
 * Example of a manoeuvre made up of other manoeuvres.
 * This could be made more "patterny" if multiple manoeuvres could 
 * just be represented as a bundle of random manoevures, composition 
 * style (similar to Unity objects).
 * @author daniel
 *
 */
public class TurnGoForwardManoeuvre extends Manoeuvre {
	
	private GoForwardManoeuvre goForwardManoeuvre;
	private TurnManoeuvre turnManoeuvre;

	public TurnGoForwardManoeuvre(MyAIController controller,
			                     WorldSpatial.RelativeDirection turnDirection,
                                 float startAngle,
                                 float turnAmount) {
		super(controller);
		goForwardManoeuvre = new GoForwardManoeuvre(controller);
		turnManoeuvre = new TurnManoeuvre(controller, turnDirection, startAngle, turnAmount);
	}

	@Override
	public void update(float delta, SensorData sensorData) {
		turnManoeuvre.update(delta, sensorData);
		goForwardManoeuvre.update(delta, sensorData);
	}

}
