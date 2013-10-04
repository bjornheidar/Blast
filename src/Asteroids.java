import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;


public class Asteroids implements ApplicationListener {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private ArrayList<Meteor> meteors;
	private ArrayList<Meteor> newMeteors;
	private ArrayList<Meteor> explodedMeteors;
	
	private ArrayList<Lazor> shots;
	private ArrayList<Lazor> landedShots;
	
	private Spaceship deathstar;
	private boolean renderShip;
	private final long fire_rate = 500;
	private long lastShot;
	
	private static Random r = new Random();
	
	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		batch = new SpriteBatch();
		
		meteors = new ArrayList<Meteor>();
		newMeteors = new ArrayList<Meteor>();
		explodedMeteors = new ArrayList<Meteor>();
		
		shots = new ArrayList<Lazor>();
		landedShots = new ArrayList<Lazor>();
		   
		deathstar = new Spaceship(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		renderShip = true;
		lastShot =  0;
		
		this.initializeMeteors();
		
		Gdx.gl11.glClearColor(.3f, .3f, .3f, 1f);
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
	        
	        //fire
	        if(Gdx.input.isKeyPressed(Keys.SPACE)){
	        	if(System.currentTimeMillis() - lastShot > fire_rate){
	        		shots.add(deathstar.fire());
	        		lastShot = System.currentTimeMillis();
	        	}
	        }
		}
		
		deathstar.update();
		
		//Check for collisions
		for(Meteor m : meteors){
			m.update();
			//Check for Ship/Meteor Collision
			if(Intersector.overlapRectangles(deathstar.getBoundingRectangle(), m.getBoundingRectangle())){
				renderShip = false;
				deathstar.explode();
				deathstar.setPos(-100, -100);
			}
			
			//Check for shot/Meteor collision
			for(Lazor l : shots){
				if(Intersector.overlapRectangles(m.getBoundingRectangle(), l.getBoundingRectangle())){
					explodedMeteors.add(m);
					newMeteors.addAll(m.explode());
					m.dispose();
					landedShots.add(l);
				}
			}
		}
		
		if(!explodedMeteors.isEmpty()){
			meteors.removeAll(explodedMeteors);
		}
		
		if(!newMeteors.isEmpty()){
			meteors.addAll(newMeteors);
		}
		
		if(!landedShots.isEmpty()){
			shots.removeAll(landedShots);
		}
		
		newMeteors.clear();
		explodedMeteors.clear();
		landedShots.clear();
		
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
		
		for(Meteor m : meteors){
			m.draw(batch);
		}
		
		for(Lazor l : shots){
			l.draw(batch);
		}
		
		deathstar.draw(batch);
		
		batch.end();
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume(){
		// TODO Auto-generated method stub
		
	}
	
	private void initializeMeteors(){
		meteors.add(new Meteor(
				r.nextInt(Gdx.graphics.getWidth()),
				r.nextInt(Gdx.graphics.getHeight()),
				new Texture(Gdx.files.internal("resources/img/meteors/met_med.png"))
				)
		);
	}
}
