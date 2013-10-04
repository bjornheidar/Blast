import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
	
	private final long frameswitch = 90;
	
	private long lastframe;
	private int frame;
	private SpriteBatch batch;
	private Vector2 pos;
	
	private static final Texture[] texture = {
		new Texture(Gdx.files.internal("resources/img/explosion/1.png")),
		new Texture(Gdx.files.internal("resources/img/explosion/2.png"))
	};
	
	public Explosion(float x, float y, SpriteBatch batch){
		this.lastframe = System.currentTimeMillis();
		this.batch = batch;
		this.pos = new Vector2(x, y);
		this.frame = 0;
	}
	
	public boolean draw(){
		if(System.currentTimeMillis() - lastframe > frameswitch){
			lastframe = System.currentTimeMillis();
			frame++;
		}
		
		if(frame < texture.length){
			batch.draw(texture[frame], pos.x, pos.y);
		}
		else{
			return false;
		}
		
		return true;
	}
}
