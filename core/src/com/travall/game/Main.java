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
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.blocks.*;
import com.travall.game.entities.Player;
import com.travall.game.generation.MapGenerator;
import com.travall.game.tools.ChunkMesh;
import com.travall.game.tools.FirstPersonCameraController;
import com.travall.game.tools.Picker;
import com.travall.game.tools.Raycast;
import com.travall.game.tools.Raycast.RayInfo;
import com.travall.game.tools.SSAO;
import com.travall.game.tools.Skybox;
import com.travall.game.tools.VoxelTerrain;

public class Main extends ApplicationAdapter {
    PerspectiveCamera camera;
    FirstPersonCameraController cameraController;
    ModelBatch modelBatch;
    AssetManager assetManager;
    ChunkMesh[][][] chunkMeshes;
    Skybox skybox;
    ModelBatch shadowBatch;
    MapGenerator mapGenerator;
    int mapWidth = 256; // changed from 128 to 256
    int mapLength = 256;
    int mapHeight = 128;
    int waterLevel = mapHeight/5; // changed from 4 to 5
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
        camera.near = 0.15f; // changed from 0.1f to 0.15f
        camera.far = 1000f; // changed from 1500f to 1000f
        camera.update();

        cameraController = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        chunkMeshes = new ChunkMesh[xChunks][yChunks][zChunks];
        mapGenerator = new MapGenerator(this,mapWidth,mapHeight,mapLength,waterLevel);
        for(int x = 0; x < xChunks; x++) {
            for(int y = 0; y < yChunks; y++) {
                for(int z = 0; z < zChunks; z++) {
                    chunkMeshes[x][y][z] = mapGenerator.generateShell(x * chunkSizeX,y * chunkSizeY, z * chunkSizeZ, chunkSizeX, chunkSizeY, chunkSizeZ, null);
                }
            }
        }

//        assetManager.load("Models/steve.g3dj",Model.class);
//        assetManager.finishLoading();
//
//        Model stevey = assetManager.get("Models/steve.g3dj", Model.class);
//        steve = new ModelInstance(stevey);
//        steve.transform.scale(0.3f,0.3f,0.3f);
//        steve.transform.setTranslation(starting.x,starting.y + 2,starting.z);


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
        
        mapGenerator.getTexture().bind();
        VoxelTerrain.begin(camera);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        for(int x = 0; x < chunkMeshes.length; x++) {
            for(int y = 0; y < chunkMeshes[0].length; y++) {
                for(int z = 0; z < chunkMeshes[0][0].length; z++) {
                    ChunkMesh mesh = chunkMeshes[x][y][z];
                    if (mesh == null) continue;
                    if (mesh.isDirty) {
                        mapGenerator.generateShell(x * chunkSizeX, y * chunkSizeY, z * chunkSizeZ, chunkSizeX, chunkSizeY, chunkSizeZ, mesh);
                    }

                    if(camera.frustum.boundsInFrustum(x * chunkSizeX, y * chunkSizeY, z * chunkSizeZ, chunkSizeX,chunkSizeY,chunkSizeZ))
                        mesh.render();
                }
            }
        }
        Gdx.gl30.glBindVertexArray(0);
        VoxelTerrain.end();
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
        
        Gdx.gl.glUseProgram(0); // Fix some performance issues.
    }

    final Vector3 add = new Vector3(), direction = new Vector3(), noam = new Vector3();
    private void update() {

        System.out.println(Gdx.graphics.getFramesPerSecond());
    	
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

        float y = player.isFlying ? 0 : -0.015f;
        float speed = 0.0175f;

        if(Gdx.input.isKeyPressed(Keys.SPACE)) {
            if(player.onGround) {
                y = 0.2f;
            } else if(player.isFlying) {
                y = 0.03f;
            }
        }

        if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && player.isFlying) {
            y = -0.03f;
        }

        noam.set(camera.direction).nor();
        float angle = MathUtils.atan2(noam.x, noam.z);

//        player.instance.nodes.first().rotation.set(Vector3.Y,angle);
//        player.instance.calculateTransforms();

        direction.set(MathUtils.sin(angle),0,MathUtils.cos(angle));
        add.setZero();

        temp.set(direction);
        
        if(Gdx.input.isKeyPressed(Keys.W)) add.add(temp.scl(speed * (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? (player.isFlying ? 3f : 1.5f) : 1)));
        if(Gdx.input.isKeyPressed(Keys.S)) add.add(temp.scl(-speed));

        temp.set(direction.rotate(Vector3.Y,-90));

        if(Gdx.input.isKeyPressed(Keys.A)) add.add(temp.scl(-speed * (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? (player.isFlying ? 1.5f : 1f) : 1)));
        if(Gdx.input.isKeyPressed(Keys.D)) add.add(temp.scl(speed * (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? (player.isFlying ? 1.5f : 1f) : 1)));

        if(!add.equals(Vector3.Zero) && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.W)) cameraController.targetFOV = 90; // changed from 110 to 90
        else cameraController.targetFOV = 80; // changed from 90 to 80

        add.y = y;
        player.applyForce(add);
        player.update(mapGenerator);
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
        mapGenerator.dispose();
        skybox.dispose();

        modelBatch.dispose();
        assetManager.dispose();
        VisUI.dispose();
        VoxelTerrain.dispose();
        Picker.dispose();
    }

    public void regenerateShell(int x, int y, int z) {
        final int indexX = x >> chunkShift;
        final int indexY = y >> chunkShift;
        final int indexZ = z >> chunkShift;
        setMeshDirtyAt(indexX, indexY, indexZ);

        if(x % chunkSizeX == 0 && x > 0) {
        	setMeshDirtyAt(indexX-1, indexY, indexZ);
        }

        if((x+1) % (chunkSizeX) == 0 && x < mapWidth-1) {
        	setMeshDirtyAt(indexX+1, indexY, indexZ);
        }

        if(y % chunkSizeY == 0 && y > 0) {
            setMeshDirtyAt(indexX, indexY-1, indexZ);
        }

        if((y+1) % (chunkSizeY) == 0 && y < mapHeight-1) {
            setMeshDirtyAt(indexX, indexY+1, indexZ);
        }

        if(z % chunkSizeZ == 0 && z > 0) {
        	setMeshDirtyAt(indexX, indexY, indexZ-1);
        }

        if((z+1) % (chunkSizeZ) == 0 && z < mapLength-1) {
        	setMeshDirtyAt(indexX, indexY, indexZ+1);
        }
    }
    
    public void setMeshDirtyAt(int indexX, int indexY, int indexZ) {
    	if (indexX < 0 || indexX >= xChunks || indexY < 0 || indexY >= yChunks || indexZ < 0 || indexZ >= zChunks)
    		return;

        if(chunkMeshes[indexX][indexY][indexZ] != null) chunkMeshes[indexX][indexY][indexZ].isDirty = true;
    }
    
    // Fast, accurate, and simple ray-cast.
    private void cameraRaycast() {
    	RayInfo info = Raycast.Fastcast(camera, mapGenerator);    	
    	if (info == null) {
    		pickerHit.y = -1; // -1 indicates there's no block been casted.
    		return;
    	}
    	
    	GridPoint3 in =  info.in;
    	GridPoint3 out = info.out;
    	
    	pickerHit.set(in);
    	if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
    		if(mapGenerator.blockExists(in.x,in.y,in.z) && mapGenerator.blocks[in.x][in.y][in.z] != Bedrock.id) {
                mapGenerator.breakBlock(in.x, in.y, in.z);
                regenerateShell(in.x, in.y, in.z);
            }
    	} else if (!mapGenerator.isOutBound(out.x, out.y, out.z) && Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
    		mapGenerator.placeBlock(out.x, out.y, out.z, blockType);
    		regenerateShell(out.x, out.y, out.z);
    	}
    }
}
