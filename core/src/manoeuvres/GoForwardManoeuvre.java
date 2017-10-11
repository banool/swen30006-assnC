package manoeuvres;

import mycontroller.MyAIController;
import mycontroller.SensorData;

public class GoForwardManoeuvre extends Manoeuvre {

    public GoForwardManoeuvre(MyAIController controller) {
        super(controller);
    }

    @Override
    public void update(float delta, SensorData sensorData) {
        if (controller.getSpeed() < controller.getTopSpeed()) {
            controller.applyForwardAcceleration();
        }
    }

}
