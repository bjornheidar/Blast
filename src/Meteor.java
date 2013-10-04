import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Meteor extends Collidable {
	private static Random r = new Random();

	public Meteor(int x, float y, Texture tex) {
		super(x, y, tex);
		
		this.size = sprite.getTexture().getHeight();
		
		float speedX = r.nextFloat() * 2 + 1;
		float speedY = r.nextFloat() * 2 + 1;
		
		if(r.nextInt(10) > 4){
			speedX *= -1;
		}
		if(r.nextInt(10) > 4){
			speedY *= -1;
		}
		
		this.speed.set(speedX, speedY);
	}
	
	public ArrayList<Meteor> explode(){
		
		int posX = (int)sprite.getX();
		int posY = (int)sprite.getY();
		
		System.out.println("boom!");
		ArrayList<Meteor> a = new ArrayList<Meteor>();
		a.add(new Meteor(posX, posY, this.sprite.getTexture()));
		a.add(new Meteor(posX, posY, this.sprite.getTexture()));
		a.add(new Meteor(posX, posY, this.sprite.getTexture()));
		
		return a;
	}
}
