import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Asteroids implements ApplicationListener {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private ArrayList<Collidable> objects;
	private ArrayList<Lazor> shots;
	
	private Spaceship deathstar;
	private boolean renderShip;
	private final long fire_rate = 500;
	private long lastShot;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		batch = new SpriteBatch();
		
		System.out.println(Gdx.graphics.getHeight());
		
		objects = new ArrayList<Collidable>();
		shots = new ArrayList<Lazor>();
		   
		deathstar = new Spaceship(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		renderShip = true;
		lastShot =  0;
		
		objects.add(deathstar);
		Gdx.gl11.glClearColor(0, 0, 0, 1f);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		update();
		display();
	}
	
	private void update(){
		if(this.renderShip){
			//Arrowkeys
			if(Gdx.input.isKeyPressed(Keys.RIGHT))
	        {
				this.deathstar.turnRight(); 
	        }
	        
	        if(Gdx.input.isKeyPressed(Keys.LEFT))
	        {
	        	this.deathstar.turnLeft();
	        }
	        
	        if(Gdx.input.isKeyPressed(Keys.UP))
	        {
	        	this.deathstar.accelerate();
	        }
	        if(Gdx.input.isKeyPressed(Keys.DOWN))
	        {
	        	this.deathstar.stop();
	        }
	        
	        //fire
	        if(Gdx.input.isKeyPressed(Keys.SPACE)){
	        	if(System.currentTimeMillis() - lastShot > fire_rate){
	        		shots.add(deathstar.fire());
	        		lastShot = System.currentTimeMillis();
	        	}
	        }
		}
		
		for(Collidable c : objects){
			c.update();
		}
		
		int i = 0;
		Lazor s;
		while(i < shots.size()){
			s = shots.get(i);
			if(s.render()){
				s.update();
				i++;
			}
			else{
				shots.remove(s);
				s.dispose();
			}
		}
	}
	
	private void display(){
		Gdx.gl11.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(Collidable c : objects){
			c.draw(batch);
		}
		
		for(Lazor l : shots){
			l.draw(batch);
		}
		
		batch.end();
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
