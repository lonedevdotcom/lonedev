package jme3.test01;

import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerrainSplatTest extends SimpleApplication {

//    private static final float[] heightMap = new float[] {
//        50f, 25f, 25f, 50f,
//        25f, 35f, 30f, 25f,
//        16f, 19f, 30f, 22f,
//        19f, 23f, 25f, 29f
//    };
    Geometry terrain;
    static ExecutorService executor = Executors.newFixedThreadPool(2); // low thread count deliberately.
    private static Logger logger = Logger.getLogger(TerrainSplatTest.class.getName());
//    Nifty nifty;


    public static void main(String[] args) {
        TerrainSplatTest tst = new TerrainSplatTest();
        tst.start();
    }

    @Override
    public void simpleInitApp() {
//        setupMainMenuScreen();
        setupGameScreen();
    }

    private void setupMainMenuScreen() {
        stateManager.attach(new MainMenuAppState());
    }

    private void setupGameScreen() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
//        rootNode.attachChild(SkyFactory.createSky(assetManager, assetManager.loadTexture("skybox.jpg"), false));

//        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
//        AbstractHeightMap heightMap = new ImageBasedHeightMap(heightMapImage.getImage());

        Material mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
//        mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        mat_terrain.setTexture("Alpha", assetManager.loadTexture("alphamap-rdh.png"));

        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 16f);

        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 16f);

        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 32);

//        float startingPoint = 0f;
//        float[] hm2 = new float[512];
//        Random r = new Random();
//        for (int i = 0; i < hm2.length; i++) {
//            startingPoint = startingPoint + (r.nextInt(3));
//            hm2[i] = startingPoint;
//        }

        // As far as I can tell, the first value is the tile size. This will NOT
        // affect the size of the terrain, just how big each tile is in it. The
        // second value adjusts the size of the terrain itself.
//        TerrainQuad terrainQuad = new TerrainQuad("terrain map", 65, 33, heightMap.getHeightMap());
//        TerrainQuad terrain = new TerrainQuad("terrain map", 65, 513, hm2);

        Quad terrainQuad = new Quad(10, 10);
        terrain = new Geometry("terrai`n", terrainQuad);
        terrain.rotate(0 - (FastMath.PI / 2), 0f, 0f); // rotate the terrain so it's horizontal rather than vertical.
        terrain.setMaterial(mat_terrain);

        rootNode.attachChild(terrain);

        Spatial player1 = assetManager.loadModel("basic_gun.obj");
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        mat1.setTexture("ColorMap", assetManager.loadTexture("turret_diffuse.png"));

//        mat1.setColor("Color", ColorRGBA.Blue);
        player1.setMaterial(mat1);
//        rootNode.attachChild(player1);

        Spatial enemy = createEnemyTest(player1); // Creates an enemy copy of player 1.
        List<Spatial> enemies = Collections.synchronizedList(new ArrayList<Spatial>());
        enemies.add(enemy);
        
        for (int i = 1; i < 5; i++) {
            Spatial p1Clone = player1.clone();
            p1Clone.setLocalTranslation(i + 0.5f, 0f, 0f);
//            p1Clone.addControl(new TargetRotationControl());
//            p1Clone.getControl(TargetRotationControl.class).setTargetSpatial(enemy);
//            ((TargetRotationControl)p1Clone.getControl()).setTargetSpatial(enemy);
//            p1Clone.addControl(new TurretControl(enemies, this)); // debug mode
            p1Clone.addControl(new TurretControl(enemies)); // normal mode
            rootNode.attachChild(p1Clone);
        }
        
//        Node camPivotNode = new Node("camera pivot node");
//        camPivotNode.setLocalTranslation(new Vector3f(5f,0f,-5f));
//        camPivotNode.attachChild(cam); // Doesnt work :(
        
        cam.setLocation(new Vector3f(-3.3f, 5.0f, 5.4f));
        cam.lookAt(new Vector3f(5f,0f,-5f), Vector3f.UNIT_Y);
        
        HUDAppState hudAppState = new HUDAppState();
        stateManager.attach(hudAppState);
    }

    @Override
    public void simpleUpdate(float tpf) {
//        cam.lookAt(new Vector3f(5f,0f,-5f), Vector3f.UNIT_Y);
//        System.out.println("location=" + this.cam.getLocation() + ". direction=" + this.cam.getDirection());
        // make the player rotate
//        terrain.rotate(tpf, 0, 0);
//        System.out.println(terrain.getLocalRotation());
    }

    private Spatial createEnemyTest(Spatial enemySpatial) {
        Spatial enemyClone = enemySpatial.clone();
        rootNode.attachChild(enemyClone);
        
        MotionPath p = new MotionPath();
        p.addWayPoint(new Vector3f(5f,0.2f,-5f));
        p.addWayPoint(new Vector3f(0f,0.2f,0f));
        p.addWayPoint(new Vector3f(10f,0.2f,0f));
        p.addWayPoint(new Vector3f(10f,0.2f,-5f));
        p.addWayPoint(new Vector3f(5f,0.2f,-5f));
        p.enableDebugShape(assetManager, rootNode);
//        p.addWayPoint(new Vector3f(5f,0.2f,-4f));
//        p.addWayPoint(new Vector3f(4f,0.2f,-4f));
//        p.addWayPoint(new Vector3f(4f,0.2f,-3f));
//        p.addWayPoint(new Vector3f(3f,0.2f,-3f));
//        p.addWayPoint(new Vector3f(3f,0.2f,-2f));
//        p.addWayPoint(new Vector3f(2f,0.2f,-2f));
//        p.addWayPoint(new Vector3f(2f,0.2f,-1f));
//        p.addWayPoint(new Vector3f(1f,0.2f,-1f));
//        p.addWayPoint(new Vector3f(1f,0.2f,0f));
//        p.addWayPoint(new Vector3f(0f,0.2f,0f));
        p.setPathSplineType(Spline.SplineType.CatmullRom);
        p.setCurveTension(0.15f);
        
        MotionTrack enemyPathTrack = new MotionTrack(enemyClone, p);
        enemyPathTrack.setDirectionType(MotionTrack.Direction.PathAndRotation);
        enemyPathTrack.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.PI*2, Vector3f.UNIT_Y)); // NO IDEA??
        enemyPathTrack.setInitialDuration(10f);
        enemyPathTrack.setSpeed(0.25f);
        enemyPathTrack.play();
        
        return enemyClone;
    }
    
    @Override
    public void destroy() {
        if (executor != null) {
            logger.log(Level.INFO, "Shutting down executor thread pool.");
            executor.shutdown();
        }
    }

//    @Override
//    public void bind(Nifty nifty, Screen screen) {
////        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void onStartScreen() {
////        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void onEndScreen() {
////        throw new UnsupportedOperationException("Not supported yet.");
//    }
//    
//    public void quit() {
//        logger.log(Level.INFO, "Quitting!");
//    }


}
