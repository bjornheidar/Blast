import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;


public class Meteor extends Collidable {
	private static Random r = new Random();
	private final int dis_from_ship = 100;
	private int metSize;
	
	private static final Texture[] textures = new Texture[] {
		new Texture(Gdx.files.internal("resources/img/meteors/met_small.png")),
		new Texture(Gdx.files.internal("resources/img/meteors/met_med.png")),
		new Texture(Gdx.files.internal("resources/img/meteors/met_large.png"))
	};
	
	private static final Sound explo = Gdx.audio.newSound(Gdx.files.internal("resources/sounds/met_explode.mp3"));

	
	public Meteor(int size) {
		super(0, 0, textures[size]);
		
		int shipX = (int)(Gdx.graphics.getWidth()/2);
		int shipY = (int)(Gdx.graphics.getHeight()/2);
		
		int randomX = r.nextInt(Gdx.graphics.getWidth());
        int randomY = r.nextInt(Gdx.graphics.getHeight());
       
        //We do a little check to make sure the asteroids don't spawn on top of the ship
        while(randomX > shipX - dis_from_ship && randomX < shipX + dis_from_ship)
        {
                randomX = r.nextInt(Gdx.graphics.getWidth());
        }
       
        while(randomY > shipY - dis_from_ship && randomY < shipY + dis_from_ship)
        {
                randomY = r.nextInt(Gdx.graphics.getHeight());
        }
        

		this.sprite.setPosition(randomX, randomY);
		
		this.metSize = size;
		
		this.size = sprite.getTexture().getHeight();
		
		float speedX = r.nextFloat() * 2 + (2 - metSize);
		float speedY = r.nextFloat() * 2 + (2 - metSize);
		
		if(r.nextInt(10) > 4){
			speedX *= -1;
		}
		if(r.nextInt(10) > 4){
			speedY *= -1;
		}
		
		this.speed.set(speedX, speedY);
	}
	
	public Meteor(float x, float y, int size){
		super(x, y, textures[size]);
		
		this.metSize = size;
		
		this.size = sprite.getTexture().getHeight();
		
		float speedX = r.nextFloat() * 1.5f + (2-metSize);
		float speedY = r.nextFloat() * 1.5f + (2-metSize);
		
		if(r.nextInt(10) > 4){
			speedX *= -1;
		}
		if(r.nextInt(10) > 4){
			speedY *= -1;
		}
		
		this.speed.set(speedX, speedY);
	}
	
	public ArrayList<Meteor> explode(){
		explo.play();
		ArrayList<Meteor> a = new ArrayList<Meteor>();
		
		if(metSize == 0){
			return a;
		}
		
		int posX = (int)sprite.getX();
		int posY = (int)sprite.getY();
		
		a.add(new Meteor(posX, posY, metSize-1));
		a.add(new Meteor(posX, posY, metSize-1));
		a.add(new Meteor(posX, posY, metSize-1));
		
		return a;
	}
}
