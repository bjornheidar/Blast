import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	
	private int score;
	private BitmapFont font;
	private BitmapFont largeFont;
	
	private long wintime;
	private boolean haswon;
	private Sound win;
	
	private int lives;
	private Texture livestex;
	private long deathtime;
	private Sound gameover;
	private boolean lost;
	
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
		lives = 0;
		livestex = new Texture(Gdx.files.internal("resources/img/lives.png"));
		gameover = Gdx.audio.newSound(Gdx.files.internal("resources/sounds/lose.mp3"));
		lost = false;
		
		score = 0;
		font = new BitmapFont();
		largeFont = new BitmapFont();
		largeFont.setScale(3f);
		
		haswon = false;
		win =  Gdx.audio.newSound(Gdx.files.internal("resources/sounds/winning.mp3"));
		
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
		
		if(renderShip){
			deathstar.update();
		}
		else if(!lost && System.currentTimeMillis() - deathtime > 3000){
			respawn();
		}
		else if(lost && System.currentTimeMillis() - deathtime > 10000){
			Gdx.app.exit();
		}
			
		//Check for collisions
		for(Meteor m : meteors){
			m.update();
			//Check for Ship/Meteor Collision
			if(Intersector.overlapRectangles(deathstar.getBoundingRectangle(), m.getBoundingRectangle())){
				if(System.currentTimeMillis() - deathtime > 5000){
					deathtime = System.currentTimeMillis();
					death();
				}
			}
			
			//Check for shot/Meteor collision
			for(Lazor l : shots){
				if(Intersector.overlapRectangles(m.getBoundingRectangle(), l.getBoundingRectangle())){
					explodedMeteors.add(m);
					newMeteors.addAll(m.explode());
					landedShots.add(l);
					score++;
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
		
		//Winning
		if(meteors.isEmpty() && !haswon){
			win.play();
			wintime = System.currentTimeMillis();
			haswon = true;
		}
		
		//After some time new level
		if(haswon && System.currentTimeMillis() - wintime > 4000){
			win.stop();
			haswon = false;
			initializeMeteors();
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
		
		for(Meteor m : meteors){
			m.draw(batch);
		}
		
		for(Lazor l : shots){
			l.draw(batch);
		}
		
		if(renderShip){
			deathstar.draw(batch);
		}
		
		for(int i = 0; i < lives; i++){
			batch.draw(livestex, 10 + i * 30, Gdx.graphics.getHeight() - 40);
		}
		
		font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 60);
		
		if(lost){
			largeFont.draw(batch, "Game Over", Gdx.graphics.getWidth()/2 -100, Gdx.graphics.getHeight()/2+50);
			largeFont.draw(batch, "Score: " + score, Gdx.graphics.getWidth()/2 -100, Gdx.graphics.getHeight()/2);
			if(score == 0){
				largeFont.draw(batch, "You suck!", Gdx.graphics.getWidth()/2 -100,  Gdx.graphics.getHeight()/2 -50);
			}
		}
		
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
		for(int i = 0; i < 2 + Asteroids.r.nextInt(4); i++){
            meteors.add(new Meteor(2));
		}
		//meteors.add(new Meteor(0));
	}
	
	private void death(){
		deathstar.setPos(-1000, -1000);
		renderShip = false;
		deathstar.explode();
		
		if(lives == 0){
			gameOver();
		}
		else{
			lives--;
		}
	}
	
	private void respawn(){
		renderShip = true;
		deathstar.setPos(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
	}
	
	private void gameOver(){
		gameover.play();
		lost = true;
	}
}
