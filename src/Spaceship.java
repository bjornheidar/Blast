import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;


public class Spaceship extends Collidable {
	private final int heading_change = 4;
	private Vector2 speedchange = new Vector2(0, 0.19f);
	
	public Spaceship(float x, float y){
		super(x, y, 32, new Texture(Gdx.files.internal("resources/img/ship.png")));
	}

	public void stop() {
		speed.set(0, 0);
	}
	
	public void turnRight(){
		sprite.rotate(-heading_change);
		speedchange.rotate(-heading_change);
	}
	
	public void turnLeft(){
		sprite.rotate(heading_change);
		speedchange.rotate(heading_change);
	}

	public void accelerate() {
		Vector2 temp = speed.cpy();
		if(temp.add(speedchange).len() < 6){
			speed.add(speedchange);
		}
	}
	
	public Lazor fire(){
		return new Lazor(sprite.getX()+12, sprite.getY()+24, speed, sprite.getRotation());
	}
}
