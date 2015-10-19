package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
	/*private OrthographicCamera camera;
	private SpriteBatch batch;*/
	private Texture img;
	private Texture bImg;
	//use TextureAtlas in reallife
	
	private Rectangle rct1;
	private Vector3 touchPos;
	private Array<Rectangle> bullets;
	private long lastSpawnTime;
	
	
	public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public AssetManager assets;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Environment environment;
    public boolean loading;

    public Array<ModelInstance> blocks = new Array<ModelInstance>();
    public Array<ModelInstance> invaders = new Array<ModelInstance>();
    public ModelInstance ship;
    public ModelInstance space;
    private Vector3 p;
	
	@Override
	public void create () {
		/*batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 560, 400);//put in one place
		
		this.createEnviron();
		*/
		p = new Vector3();
		modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 7f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        //camController = new CameraInputController(cam);
        //Gdx.input.setInputProcessor(camController);

        assets = new AssetManager();
        assets.load("data/ship.obj", Model.class);
        assets.load("data/block.obj", Model.class);
        assets.load("data/invader.obj", Model.class);
        assets.load("data/spacesphere.obj", Model.class);
        loading = true;
        //this.createEnviron();
      //Music music = Gdx.audio.newMusic(Gdx.files.getFileHandle("/media/sam/TOSHIBA EXT/Media/Music/Aerodynamic-Daft Punk.mp3", FileType.Internal));
      		//music.setVolume(0.5f);
      		//music.play();
      		//music.setLooping(true);
	}

	private void createEnviron() {
		this.touchPos = new Vector3();
		
		this.bImg = new Texture(Gdx.files.internal("goldstar.gif"));
		
		this.rct1 = new Rectangle();
		this.rct1.x = 560 / 2 - 64 / 2;
		this.rct1.y = 20;
		this.rct1.width = 64;
		this.rct1.height = 64;
		
		this.bullets = new Array<Rectangle>();
		spawnBullet();
	}

	private void spawnBullet() {
		Gdx.app.log("Event", "spawning");
		Rectangle b = new Rectangle();
		b.x = MathUtils.random(0, 800-64);
		b.y = 480;
		b.width = 64;
		b.height = 64;
		this.bullets.add(b);
		lastSpawnTime = TimeUtils.nanoTime();
	}
	
	private void renderAssets() {
        ship = new ModelInstance(assets.get("data/ship.obj", Model.class));
        ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
        instances.add(ship);

        Model blockModel = assets.get("data/block.obj", Model.class);
        for (float x = -5f; x <= 5f; x += 2f) {
            ModelInstance block = new ModelInstance(blockModel);
            block.transform.setToTranslation(x, 0, 3f);
            instances.add(block);
            blocks.add(block);
        }

        Model invaderModel = assets.get("data/invader.obj", Model.class);
        for (float x = -5f; x <= 5f; x += 2f) {
            for (float z = -8f; z <= 0f; z += 2f) {
                ModelInstance invader = new ModelInstance(invaderModel);
                invader.transform.setToTranslation(x, 0, z);
                instances.add(invader);
                invaders.add(invader);
            }
        }

        space = new ModelInstance(assets.get("data/spacesphere.obj", Model.class));

        loading = false;
    }
	
	@Override
	public void render () {
		if (loading && assets.update())
			renderAssets();
        //camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        if (space != null)
            modelBatch.render(space);
        modelBatch.end();
		
        /*
		int x=0,y=0;
		//probs want better listener
		if(Gdx.input.isTouched()) {
		      //these will be main ctrls
			x = Gdx.input.getX();
			y = Gdx.input.getY();
		    touchPos.set(x, y, 0);
		 }
		*/
        if( ship != null){
        float x = 0f, z = 0f, speed = 15;
        	        	
		    if(Gdx.input.isKeyPressed(Keys.LEFT)) x -= speed * Gdx.graphics.getDeltaTime();
		    
		    if(Gdx.input.isKeyPressed(Keys.RIGHT)) x += speed * Gdx.graphics.getDeltaTime();
		    if(Gdx.input.isKeyPressed(Keys.UP)) z += speed * Gdx.graphics.getDeltaTime();
		    if(Gdx.input.isKeyPressed(Keys.DOWN)) z -= speed * Gdx.graphics.getDeltaTime();
		    
		    ship.transform.translate(x, 0, z);
		    //how to get world coords
		    //ship.transform.getTranslation(p);
		   // Gdx.app.log("pos",p.x + "," + p.z );
        }
		    /*
		    cam.position.y
		    cam.lookAt(cam.position.x,cam.position.y - 7f,0);
		    cam.update();
		    */
		    /*
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, rct1.x, rct1.y);
		for(Rectangle raindrop: this.bullets) {
			batch.draw(bImg, raindrop.x, raindrop.y);
	    }
		batch.end();
		
		if(TimeUtils.nanoTime() - lastSpawnTime > 1000000000) spawnBullet();
		
		Iterator<Rectangle> iter = bullets.iterator();
	      while(iter.hasNext()) {
	         Rectangle b = iter.next();
	         b.y -= 200 * Gdx.graphics.getDeltaTime();
	         if(b.y + 64 < 0) iter.remove();
	         
	         //could be expensive
	         if(b.overlaps(rct1)) {
	        	 Gdx.app.log("Event", "Colliding");
	            iter.remove();
	         }
	      }
	      */
	}
	
	@Override
	   public void dispose() {
	      // dispose of all the native resources
		modelBatch.dispose();
        instances.clear();
        assets.dispose();
	   }

	   @Override
	   public void resize(int width, int height) {
	   }

	   @Override
	   public void pause() {
	   }

	   @Override
	   public void resume() {
	   }
}
