import com.badlogic.gdx.backends.lwjgl.LwjglApplication;


public class Blast {
	public static void main(String[] args){
		new LwjglApplication(new Asteroids(), "Blast!", 800, 600, false);
	}
}
