import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Collidable {
	protected Vector2 speed;
	protected Sprite sprite;
	protected int size;
	
	public Collidable(float x, float y, Texture tex){
		this.sprite = new Sprite(tex);
		this.sprite.setPosition(x, y);

		this.speed = new Vector2();
	}
	
	public Sprite getSprite(){
		return sprite;
	}

	public void update() {
		sprite.setPosition(sprite.getX() + speed.x, sprite.getY() + speed.y);
		
		if(sprite.getX() > Gdx.graphics.getWidth() + size)
			sprite.setX(-size);
		if(sprite.getX() < 0 - size)
			sprite.setX(Gdx.graphics.getWidth() + size);
		
		if(sprite.getY() > Gdx.graphics.getHeight() + size)
			sprite.setY(-size);
		if(sprite.getY() < 0 - size)
			sprite.setY(Gdx.graphics.getHeight() + size);
	}
	
	public float getX(){
		return sprite.getX();
	}
	
	public float getY(){
		return sprite.getY();
	}
	
	public Texture getTex(){
		return sprite.getTexture();
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void dispose() {
		this.sprite.getTexture().dispose();
	}
	
	public Rectangle getBoundingRectangle(){
		return sprite.getBoundingRectangle();
	}
}
