package com.travall.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.entities.Player;
import com.travall.game.generation.MapGenerator;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.tools.FirstPersonCameraController;
import com.travall.game.tools.SSAO;
import com.travall.game.tools.Skybox;

public class Main extends ApplicationAdapter {
    PerspectiveCamera camera;
    FirstPersonCameraController cameraController;
    ModelBatch modelBatch;
    AssetManager assetManager;
    Environment environment;
    ModelCache[][] tileCache;
    ModelInstance skyboxInstance;
    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    MapGenerator mapGenerator;
    ModelInstance shower;
    ModelInstance picker;
    int mapWidth = 128;
    int mapLength = 128;
    int mapHeight = 128;
    int waterLevel = mapHeight/4;
    int chunkSizeX = 8;
    int chunkSizeZ = 8;
    int xChunks = mapWidth/chunkSizeX;
    int zChunks = mapLength/chunkSizeZ;

    final Ray ray = new Ray();
    final Vector3 rayPos = new Vector3();
    final Vector3 rayDir = new Vector3();
    final Vector3 rayIntersection = new Vector3();
    final BoundingBox rayBox = new BoundingBox();
    final Vector3 rayBoxMin = new Vector3();
    final Vector3 rayBoxMax = new Vector3();

    Block blockType;

    Player player;

    Vector3 temp = new Vector3();


    Vector3 mouseTilePos = new Vector3();
    Vector3 targetPos = new Vector3();
    boolean target = false;

    float y = 0;

    SSAO ssao;
    SpriteBatch spriteBatch;
    Texture crosshair;


    @Override
    public void create () {
        assetManager = new AssetManager();
        tileCache = new ModelCache[xChunks][zChunks];

        DefaultShader.Config defaultConfig = new DefaultShader.Config();
        defaultConfig.numDirectionalLights = 2;
        defaultConfig.fragmentShader = Gdx.files.internal("Shaders/frag.glsl").readString();
        defaultConfig.vertexShader = Gdx.files.internal("Shaders/vert.glsl").readString();

        modelBatch = new ModelBatch(new DefaultShaderProvider(defaultConfig));

        Vector3 starting = new Vector3(mapWidth/2,mapHeight,mapLength/2);

        skyboxInstance = new Skybox().Generate();
        skyboxInstance.transform.scale(500,500,500);

        camera = new PerspectiveCamera(90,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 1500f;
        camera.update();

        cameraController = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat2 = new Material(ColorAttribute.createDiffuse(1,1,1,0.2f));
        mat2.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        Model model2 = modelBuilder.createBox(1.01f, 1.01f, 1.01f,mat2
                ,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        picker = new ModelInstance(model2);


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog,  0.5f, 0.5f, 0.5f, 1f));
//		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        environment.add((shadowLight = new DirectionalShadowLight(4096, 4096, 32, 32, 1f, 10f))
                .set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        environment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        mapGenerator = new MapGenerator(mapWidth,mapHeight,mapLength,waterLevel);

        for(int x = 0; x < xChunks; x++) {
            for(int z = 0; z < zChunks; z++) {
                tileCache[x][z] = new ModelCache();
                tileCache[x][z] = mapGenerator.generateShell(x * chunkSizeX,z * chunkSizeZ,chunkSizeX,chunkSizeZ);
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

        spriteBatch = new SpriteBatch();

        Gdx.input.setCursorCatched(true);

        crosshair = new Texture("crosshair.png");

    }

    @Override
    public void render () {
        update();

        ssao.begin();
        modelBatch.begin(camera);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.render(skyboxInstance);
        for(int x = 0; x < tileCache.length; x++) {
            for(int z = 0; z < tileCache[0].length; z++) {
                modelBatch.render(tileCache[x][z]);
            }
        }
        modelBatch.render(picker);
//        modelBatch.render(player.instance,environment);
        modelBatch.end();
        ssao.end();

//        spriteBatch.setShader(ssaoShaderProgram);
        spriteBatch.begin();
        ssao.draw(spriteBatch);
        spriteBatch.draw(crosshair,(Gdx.graphics.getWidth() / 2) - 8, (Gdx.graphics.getHeight() / 2) - 8);
        spriteBatch.end();
        spriteBatch.setShader(null);
    }

    private void update() {
        camera.fieldOfView = MathUtils.lerp(camera.fieldOfView,cameraController.targetFOV, 0.2f);
        camera.update();


        y = -0.15f;
        float speed = 0.025f;

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) y = 2f;
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) y = 32f;

        float angle = (float)Math.atan2(camera.direction.nor().x,camera.direction.nor().z);

//        player.instance.nodes.first().rotation.set(Vector3.Y,angle);
//        player.instance.calculateTransforms();

        Vector3 direction = new Vector3((float) Math.toDegrees(Math.sin(angle)),0,(float) Math.toDegrees(Math.cos(angle)));

        Vector3 add = new Vector3();

        temp.set(direction);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) add.add(temp.scl(speed * (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1.5f : 1)));
        if(Gdx.input.isKeyPressed(Input.Keys.S)) add.add(temp.scl(-speed));

        temp.set(direction.rotate(Vector3.Y,-90));

        if(Gdx.input.isKeyPressed(Input.Keys.A)) add.add(temp.scl(-speed));
        if(Gdx.input.isKeyPressed(Input.Keys.D)) add.add(temp.scl(speed));

        if(!add.equals(Vector3.Zero) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.W)) cameraController.targetFOV = 110;
        else cameraController.targetFOV = 90;

        player.applyForce(add);
        player.applyForce(new Vector3(0,y,0));
        player.update(mapGenerator);
        camera.position.set(player.instance.transform.getTranslation(temp).add(0,0.9f,0));

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

        modelBatch.dispose();
        assetManager.dispose();
        VisUI.dispose();
    }

    private int nearestChunk(int i,int chunkSize) {
        return Math.round(i / chunkSize) * chunkSize;
    }

    private void regenerateShell(int x, int z) {
        tileCache[(nearestChunk(x,chunkSizeX)) / chunkSizeX][(nearestChunk(z,chunkSizeZ)) / chunkSizeZ] = mapGenerator.generateShell(nearestChunk(x,chunkSizeX),nearestChunk(z,chunkSizeZ),chunkSizeX,chunkSizeZ);

        System.out.println(x + " : " + z);

        if(x % chunkSizeX == 0 && x != 0) {
            int indexX = (nearestChunk(x,chunkSizeX)) / chunkSizeX;
            int indexZ = (nearestChunk(z,chunkSizeZ)) / chunkSizeZ;
            tileCache[indexX-1][indexZ] = mapGenerator.generateShell(nearestChunk(x-chunkSizeX,chunkSizeX),nearestChunk(z,chunkSizeZ),chunkSizeX,chunkSizeZ);
        }

        if((x+1) % (chunkSizeX) == 0 && x != mapWidth-1) {
            int indexX = (nearestChunk(x,chunkSizeX)) / chunkSizeX;
            int indexZ = (nearestChunk(z,chunkSizeZ)) / chunkSizeZ;
            tileCache[indexX+1][indexZ] = mapGenerator.generateShell(nearestChunk(x+chunkSizeX,chunkSizeX),nearestChunk(z,chunkSizeZ),chunkSizeX,chunkSizeZ);
        }

        if(z % chunkSizeZ == 0 && z != 0) {
            int indexX = (nearestChunk(x,chunkSizeX)) / chunkSizeX;
            int indexZ = (nearestChunk(z,chunkSizeZ)) / chunkSizeZ;
            tileCache[indexX][indexZ-1] = mapGenerator.generateShell(nearestChunk(x,chunkSizeX),nearestChunk(z-chunkSizeZ,chunkSizeZ),chunkSizeX,chunkSizeZ);
        }

        if((z+1) % (chunkSizeZ) == 0 && z != mapLength-1) {
            int indexX = (nearestChunk(x,chunkSizeX)) / chunkSizeX;
            int indexZ = (nearestChunk(z,chunkSizeZ)) / chunkSizeZ;
            tileCache[indexX][indexZ+1] = mapGenerator.generateShell(nearestChunk(x,chunkSizeX),nearestChunk(z+chunkSizeZ,chunkSizeZ),chunkSizeX,chunkSizeZ);
        }
    }


    private void regenerateShellLighting(int x, int y, int z) {

    }

    private void cameraRaycast() {
        ray.set(camera.position, camera.direction);
        rayPos.set(camera.position);
        rayDir.set(camera.direction).scl(0.1f);


        for (int steps = 0; steps < 800; steps++) {
            rayPos.add(rayDir);


            if (mapGenerator.blockExists((int)rayPos.x,(int)rayPos.y,(int)rayPos.z)) {
                mouseTilePos = rayPos;
                rayPos.x = (int)rayPos.x;
                rayPos.y = (int)rayPos.y;
                rayPos.z = (int)rayPos.z;


                picker.transform.setTranslation(rayPos.x + 0.5f,rayPos.y + 0.5f,rayPos.z + 0.5f);

                rayBoxMin.set(rayPos);
                rayBoxMax.set(rayPos).add(1);
                rayBox.set(rayBoxMin, rayBoxMax);

                int rayCastFace = -1;
                float rayCastClosest = 10;

                if (Intersector.intersectRayBounds(ray, rayBox, rayIntersection)) {
                    if (rayBoxMax.y - rayIntersection.y < rayCastClosest) {
                        rayCastClosest = rayBoxMax.y - rayIntersection.y;
                        rayCastFace = 0;
                    }

                    if (-(rayBoxMin.y - rayIntersection.y) < rayCastClosest) {
                        rayCastClosest = -(rayBoxMin.y - rayIntersection.y);
                        rayCastFace = 1;
                    }

                    if (rayBoxMax.x - rayIntersection.x < rayCastClosest) {
                        rayCastClosest = rayBoxMax.x - rayIntersection.x;
                        rayCastFace = 2;
                    }

                    if (-(rayBoxMin.x - rayIntersection.x) < rayCastClosest) {
                        rayCastClosest = -(rayBoxMin.x - rayIntersection.x);
                        rayCastFace = 3;
                    }

                    if (rayBoxMax.z - rayIntersection.z < rayCastClosest) {
                        rayCastClosest = rayBoxMax.z - rayIntersection.z;
                        rayCastFace = 4;
                    }

                    if (-(rayBoxMin.z - rayIntersection.z) < rayCastClosest) {
                        rayCastClosest = -(rayBoxMin.z - rayIntersection.z);
                        rayCastFace = 5;
                    }


                }

                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    mapGenerator.blocks[(int) (rayPos.x)][(int) rayPos.y][(int) (rayPos.z)] = 0;
                    regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                } else if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                    if(rayCastFace == 0) {
                        if(!mapGenerator.blockExists((int) (rayPos.x),(int) rayPos.y + 1,(int) (rayPos.z)) && (int) rayPos.y + 1 < mapHeight) {
                            mapGenerator.blocks[(int) (rayPos.x)][(int) rayPos.y + 1][(int) (rayPos.z)] = BlocksList.Grass;
                            mapGenerator.placeLight(temp.set((int) (rayPos.x),(int) rayPos.y + 1,(int) (rayPos.z)));
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                    if(rayCastFace == 1) {
                        if(!mapGenerator.blockExists((int) (rayPos.x),(int) rayPos.y - 1,(int) (rayPos.z)) && (int) rayPos.y - 1 > 0) {
                            mapGenerator.blocks[(int) (rayPos.x)][(int) rayPos.y - 1][(int) (rayPos.z)] = BlocksList.Grass;
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                    if(rayCastFace == 2) {
                        if(!mapGenerator.blockExists((int) (rayPos.x + 1),(int) rayPos.y,(int) (rayPos.z))) {
                            mapGenerator.blocks[(int) (rayPos.x + 1)][(int) rayPos.y][(int) (rayPos.z)] = BlocksList.Grass;
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                    if(rayCastFace == 3) {
                        if(!mapGenerator.blockExists((int) (rayPos.x) - 1,(int) rayPos.y,(int) (rayPos.z))) {
                            mapGenerator.blocks[(int) (rayPos.x - 1)][(int) rayPos.y][(int) (rayPos.z)] = BlocksList.Grass;
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                    if(rayCastFace == 4) {
                        if(!mapGenerator.blockExists((int) (rayPos.x),(int) rayPos.y,(int) (rayPos.z + 1))) {
                            mapGenerator.blocks[(int) (rayPos.x)][(int) rayPos.y][(int) (rayPos.z + 1)] = BlocksList.Grass;
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                    if(rayCastFace == 5) {
                        if(!mapGenerator.blockExists((int) (rayPos.x),(int) rayPos.y,(int) (rayPos.z - 1))) {
                            mapGenerator.blocks[(int) (rayPos.x)][(int) rayPos.y][(int) (rayPos.z - 1)] = BlocksList.Grass;
                            regenerateShell((int) (rayPos.x),(int) (rayPos.z));
                        }
                    }
                }

                break;
            }
        }

    }
}
