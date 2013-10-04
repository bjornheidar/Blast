import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;


public class Lazor extends Collidable {
	private final long max_age = 800;
	private long age;
	
	public Lazor(float x, float y, Vector2 shipspeed, float shipheading){
		super(x, y, new Texture(Gdx.files.internal("resources/img/lazor.png")));
		
		this.size = sprite.getTexture().getHeight();
		
		this.speed.set(new Vector2(0,7).rotate(shipheading).add(shipspeed));
		this.age = System.currentTimeMillis();
	}
	
	public boolean render()
	{
		if(System.currentTimeMillis() - age > max_age){
			return false;
		}
		
		return true;
	}
}
