package tiles;

import world.Car;

public class LavaTrap extends TrapTile {
	
	public void applyTo(Car car, float delta) {
		car.reduceHealth(20 * delta);
	}
	
	public boolean canAccelerate() {
		return true;
	}
	
	public boolean canTurn() {
		return true;
	}
}
