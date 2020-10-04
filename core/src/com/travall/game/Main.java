package com.travall.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.blocks.*;
import com.travall.game.entities.Player;
import com.travall.game.handles.FirstPersonCameraController;
import com.travall.game.handles.Raycast;
import com.travall.game.handles.VoxelTerrain;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.SSAO;
import com.travall.game.renderer.Skybox;
import com.travall.game.utils.BlockPos;
import com.travall.game.world.World;

public class Main extends ApplicationAdapter {
    PerspectiveCamera camera;
    FirstPersonCameraController cameraController;
    ModelBatch modelBatch;
    AssetManager assetManager;
    
    Skybox skybox;
    ModelBatch shadowBatch;
    World world;
    int mapWidth = 256; // changed from 128 to 256
    int mapLength = 256;
    int mapHeight = 128;
    int waterLevel = mapHeight / 5; // changed from 4 to 5
    public int chunkShift = 4; // 1 << 4 = 16. I set it back from 32 to 16 due to vertices limitations.
    public int chunkSizeX = 1<<chunkShift;
    public int chunkSizeY = 1<<chunkShift;
    public int chunkSizeZ = 1<<chunkShift;
    int xChunks = mapWidth/chunkSizeX;
    int yChunks = mapHeight/chunkSizeY;
    int zChunks = mapLength/chunkSizeZ;

    final GridPoint3 pickerHit = new GridPoint3();

    short blockType = Log.id;

    Player player;

    Vector3 temp = new Vector3();

    SSAO ssao;
    SpriteBatch spriteBatch;
    Texture crosshair;


    @Override
    public void create () {
    	VoxelTerrain.ints(); // Must ints it first.
        assetManager = new AssetManager();

        DefaultShader.Config defaultConfig = new DefaultShader.Config();
        defaultConfig.numDirectionalLights = 2;
        defaultConfig.fragmentShader = Gdx.files.internal("Shaders/frag.glsl").readString();
        defaultConfig.vertexShader = Gdx.files.internal("Shaders/vert.glsl").readString();

        modelBatch = new ModelBatch(new DefaultShaderProvider(defaultConfig));

        Vector3 starting = new Vector3(mapWidth/2,mapHeight,mapLength/2);

        skybox = new Skybox();;

        camera = new PerspectiveCamera(90,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 500f;
        camera.update();

        cameraController = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        shadowBatch = new ModelBatch(new DepthShaderProvider());

//        assetManager.load("Models/steve.g3dj",Model.class);
//        assetManager.finishLoading();
//
//        Model stevey = assetManager.get("Models/steve.g3dj", Model.class);
//        steve = new ModelInstance(stevey);
//        steve.transform.scale(0.3f,0.3f,0.3f);
//        steve.transform.setTranslation(starting.x,starting.y + 2,starting.z);

        world = new World();

        player = new Player(new Vector3(starting.x - 0.5f,starting.y + 3,starting.z - 0.5f));

        ssao = new SSAO(camera);
        ssao.setEnable(false); // Enable or disable the SSAO.

        spriteBatch = new SpriteBatch();

        Gdx.input.setCursorCatched(true);
        
        crosshair = new Texture("crosshair.png");
        
        Picker.ints();
    }

    Texture text;
    @Override
    public void render () {
        update();
        camera.update(); // Update the camera projection

        ssao.begin();
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        skybox.render(camera);
        
//      modelBatch.begin(camera);
//      modelBatch.render(player.instance,environment);
//      modelBatch.end();
        
        world.render(camera);
        Picker.render(camera, pickerHit);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        
        ssao.end();
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        ssao.render();

//        spriteBatch.setShader(ssaoShaderProgram);
        spriteBatch.begin();
        spriteBatch.draw(crosshair,(Gdx.graphics.getWidth() / 2) - 8, (Gdx.graphics.getHeight() / 2) - 8);
        spriteBatch.end();
        spriteBatch.setShader(null);
        
        BlockPos.reset(); // Always reset the pool.
        Gdx.gl.glUseProgram(0); // Fix some performance issues.
    }

    private void update() {

        player.update(world,camera,cameraController);

        //System.out.println(Gdx.graphics.getFramesPerSecond());
    	
        camera.fieldOfView = MathUtils.lerp(camera.fieldOfView,cameraController.targetFOV, 0.2f);

        if (Gdx.input.isKeyJustPressed(Keys.F11)){
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();

            if(Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(800,600);
            } else {
                Gdx.graphics.setFullscreenMode(currentMode);
            }
        }

        if (Gdx.input.isKeyJustPressed(Keys.F)) {
            player.isFlying = !player.isFlying;
        }

        if(Gdx.input.isKeyPressed(Keys.Q)) blockType = Log.id;
        if(Gdx.input.isKeyPressed(Keys.E)) blockType = Gold.id;

        if(Gdx.input.isKeyJustPressed(Keys.P)) VoxelTerrain.toggleAO();

        camera.position.set(player.instance.transform.getTranslation(temp).add(0,0.75f,0));

        cameraRaycast();
    }

    @Override
    public void resize(int width, int height) {
        spriteBatch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
        ssao.resize(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose () {
        ssao.dispose();
        spriteBatch.dispose();
        crosshair.dispose();
        world.dispose();
        skybox.dispose();

        modelBatch.dispose();
        assetManager.dispose();
        VisUI.dispose();
        VoxelTerrain.dispose();
        Picker.dispose();
    }
    
    // Fast, accurate, and simple ray-cast.
    private void cameraRaycast() {
    	RayInfo info = Raycast.Fastcast(camera, world);
    	if (info == null) {
    		pickerHit.y = -1; // -1 indicates there's no block been casted.
    		return;
    	}
    	
    	GridPoint3 in =  info.in;
    	GridPoint3 out = info.out;
    	
    	pickerHit.set(in);
    	if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
    		if(world.blockExists(in.x,in.y,in.z) && world.blocks[in.x][in.y][in.z] != Bedrock.id) {
                world.breakBlock(in.x, in.y, in.z);
                world.setMeshDirtyShellAt(in.x, in.y, in.z);
            }
    	} else if (!world.isOutBound(out.x, out.y, out.z) && Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
    		world.placeBlock(out.x, out.y, out.z, blockType);
    		world.setMeshDirtyShellAt(out.x, out.y, out.z);
    	}
    }
}
