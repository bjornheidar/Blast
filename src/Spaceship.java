import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Sound;


public class Spaceship extends Collidable {
	private final int heading_change = 4;
	private Vector2 speedchange = new Vector2(0, 0.19f);
	private Sound lazorFire = Gdx.audio.newSound(Gdx.files.internal("resources/sounds/lazor.wav"));
	
	public Spaceship(float x, float y){
		super(x, y, new Texture(Gdx.files.internal("resources/img/ship.png")));
		this.size = sprite.getTexture().getHeight();
	}

	public void stop() {
		speed.set(0, 0);
	}
	
	public void turnRight(){
		sprite.rotate(-heading_change);
		speedchange.rotate(-heading_change);
		//lazorcannon.rotate(-heading_change);
	}
	
	public void turnLeft(){
		sprite.rotate(heading_change);
		speedchange.rotate(heading_change);
		//lazorcannon.rotate(heading_change);
	}

	public void accelerate() {
		Vector2 temp = speed.cpy();
		if(temp.add(speedchange).len() < 6){
			speed.add(speedchange);
		}
	}
	
	public Lazor fire(){
		//TODO fix where lazor appears
		lazorFire.play();
		return new Lazor(sprite.getX(), sprite.getY(), speed, sprite.getRotation());
	}

	public void setPos(int x, int y) {
		sprite.setPosition(x, y);
		speed.set(0, 0);
	}

	public void explode() {
		// TODO Sýna sprengingu
		
	}

	public int getSize() {
		return this.size;
	}
}
