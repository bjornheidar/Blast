import java.util.ArrayList;
import java.util.Random;

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
	private static SpriteBatch batch;
	private OrthographicCamera camera;
	private boolean showintro = true;
	
	private ArrayList<Meteor> meteors;
	private ArrayList<Meteor> newMeteors;
	private ArrayList<Meteor> explodedMeteors;
	
	private ArrayList<Lazor> shots;
	private ArrayList<Lazor> landedShots;
	
	private ArrayList<Explosion> explos;
	private ArrayList<Explosion> doneexplos;
	
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
	private int level;
	
	private int lives;
	private Texture livestex;
	private long deathtime;
	private Sound gameover;
	private boolean lost;
	private Sound deathsound;
	
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
		
		explos = new ArrayList<Explosion>();
		doneexplos = new ArrayList<Explosion>();
		   
		deathstar = new Spaceship(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		renderShip = true;
		lastShot =  0;
		lives = 3;
		deathsound = Gdx.audio.newSound(Gdx.files.internal("resources/sounds/ship_explode.mp3"));
		livestex = new Texture(Gdx.files.internal("resources/img/lives.png"));
		gameover = Gdx.audio.newSound(Gdx.files.internal("resources/sounds/lose.mp3"));
		lost = false;
		
		score = 0;
		font = new BitmapFont();
		largeFont = new BitmapFont();
		largeFont.setScale(3f);
		
		haswon = false;
		win =  Gdx.audio.newSound(Gdx.files.internal("resources/sounds/winning.mp3"));
		level = 1;
		
		this.initializeMeteors();
		
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
		if(showintro){
			if(deathstar.getX() != Gdx.graphics.getWidth()/2){
				showintro = false;
			}
		}
		
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
					death(m);
					explos.add(new Explosion(m.getX(), m.getY(), batch));
				}
			}
			
			//Check for shot/Meteor collision
			for(Lazor l : shots){
				if(Intersector.overlapRectangles(m.getBoundingRectangle(), l.getBoundingRectangle())){
					explodedMeteors.add(m);
					newMeteors.addAll(m.explode());
					landedShots.add(l);
					score++;
					explos.add(new Explosion(m.getX(), m.getY(), batch));
				}
			}
		}
		
		if(!explodedMeteors.isEmpty()){
			meteors.removeAll(explodedMeteors);
			explodedMeteors.clear();
		}
		
		if(!newMeteors.isEmpty()){
			meteors.addAll(newMeteors);
			newMeteors.clear();
		}
		
		if(!landedShots.isEmpty()){
			shots.removeAll(landedShots);
			landedShots.clear();
		}
		
		//Winning
		if(!haswon && meteors.isEmpty()){
			haswon = true;
			if(!lost){
				win.play();
			}
			wintime = System.currentTimeMillis();
			score += 50;
			level++;
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
		
		boolean drawn;
		for(Explosion e : explos){
			drawn = e.draw();
			if(!drawn){
				doneexplos.add(e);
			}
		}
		
		if(!doneexplos.isEmpty()){
			explos.removeAll(doneexplos);
			doneexplos.clear();
		}
		
		for(int i = 0; i < lives; i++){
			batch.draw(livestex, 10 + i * 30, Gdx.graphics.getHeight() - 40);
		}
		
		font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 45);
		font.draw(batch, "Level " + level, 10, Gdx.graphics.getHeight() - 60);
		
		if(showintro){
			largeFont.draw(batch, "Pilot! You are lost and your gun is wonky!", Gdx.graphics.getWidth()/2 -390, Gdx.graphics.getHeight()/2+100);
			largeFont.draw(batch, "Watch out for the incoming meteors!", Gdx.graphics.getWidth()/2 -350, Gdx.graphics.getHeight()/2+50);
		}
		
		if(haswon && !lost){
			largeFont.draw(batch, "Level " + (level-1) + " Complete", Gdx.graphics.getWidth()/2 -150, Gdx.graphics.getHeight()/2+50);
			largeFont.draw(batch, "More Asteroids incoming!", Gdx.graphics.getWidth()/2 -200, Gdx.graphics.getHeight()/2);
		}
		
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
		for(int i = 0; i < level + Asteroids.r.nextInt(3); i++){
            meteors.add(new Meteor(2));
		}
	}
	
	private void death(Meteor m){
		deathstar.setPos(-1000, -1000);
		renderShip = false;
		explodedMeteors.add(m);
		newMeteors.addAll(m.explode());
		
		deathsound.play();
		
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
