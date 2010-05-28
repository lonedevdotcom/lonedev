package com.lonedev.spacehoops.sandbox;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.xml.XMLImporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Richard Hawkes
 */
public class SpaceHoops1 extends BasicGameState {
    protected XMLImporter xmlImporter = XMLImporter.getInstance();
    private static final Logger logger = Logger.getLogger(SpaceHoops1.class.getName());
    private ChaseCamera chaseCamera;
    private Skybox skybox;

    private Node hudNode;
    private int hudTextureWidth, hudTextureHeight;

    public static void main(String[] args) {
        StandardGame standardGame = new StandardGame("GameControl", StandardGame.GameType.GRAPHICAL, null);
        standardGame.getSettings().setSamples(0);
        standardGame.getSettings().setWidth(1024);
        standardGame.getSettings().setHeight(600);
        standardGame.getSettings().setFullscreen(true);

        standardGame.start();

        GameState client = new SpaceHoops1();
        GameStateManager.getInstance().attachChild(client);
        client.setActive(true);

        logger.info("SpaceHoops1.main complete");
    }

    public SpaceHoops1() {
        super("Main Client");
        init();
    }

    protected void init() {
        createSkybox();
        loadSpaceShip();
//        loadModel("/artefacts/spacefighter01/hoop-jme.xml", Vector3f.ZERO, 1f);
        loadSpaceStation();
//        testDrawOrtho();
        testHudTutorial();
        
//        SceneMonitor.getMonitor().registerNode(rootNode, "Root Node");
//        SceneMonitor.getMonitor().showViewer(true);
    }

    private Spatial loadModel(String spatialURL, Vector3f translation, float scale) {
        try {
            Spatial model = (Spatial) xmlImporter.load(this.getClass().getResource(spatialURL));
            model.setLocalScale(scale);
            model.setModelBound(new BoundingBox());
            model.updateModelBound();
            model.updateWorldBound();
            rootNode.attachChild(model);
            model.setLocalTranslation(translation);
            rootNode.updateRenderState();
            return model;
        } catch (IOException ex) {
            logger.severe("Unable to load spatial: " + ex);
            return null;
        }
    }

    private void loadSpaceShip() {
        Spatial spaceShip = loadModel("/artefacts/spacefighter01/spacefighter01-jme.xml", new Vector3f(0, 0, 0), 1f);
        spaceShip.addController(new SpaceShipController(spaceShip));

        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox)spaceShip.getWorldBound()).yExtent * 3F;
        targetOffset.z = ((BoundingBox)spaceShip.getWorldBound()).zExtent * 1F;

        Map props = new HashMap();
        props.put(ChaseCamera.PROP_TARGETOFFSET, targetOffset);
        props.put(ChaseCamera.PROP_STAYBEHINDTARGET, "true");
        props.put(ChaseCamera.DEFAULT_MAINTAINAZIMUTH, "true");

        chaseCamera = new ChaseCamera(DisplaySystem.getDisplaySystem().getRenderer().getCamera(), spaceShip, props);
    }

    private void loadSpaceStation() {
        Spatial spaceStation = loadModel("/artefacts/spacestation/spacestation-jme.xml", new Vector3f(0, 0, -600), 2f);

        Controller rotationController = new RotationController(spaceStation, Vector3f.UNIT_Y);
        rotationController.setSpeed(0.02f);
        spaceStation.addController(rotationController);

        // Adding a cull state to ignore the "back" side of the vertexes. This
        // squeezes a slightly better frame rate :)
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        spaceStation.setRenderState(cs);
    }

    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        // Keep the chase camera behaving itself :)
        chaseCamera.update(tpf);

        //we want to keep the skybox around our eyes, so move it with the camera.
        skybox.setLocalTranslation(chaseCamera.getCamera().getLocation());
        skybox.updateRenderState();

//        SceneMonitor.getMonitor().updateViewer(tpf);
        hudNode.updateRenderState();
    }

    @Override
    public void render(float tpf) {
        super.render(tpf);
//        SceneMonitor.getMonitor().renderViewer(DisplaySystem.getDisplaySystem().getRenderer());
    }

    public float getUForPixel(int xPixel) {
        return (float) xPixel / hudTextureWidth;
    }

    public float getVForPixel(int yPixel) {
        return 1f - (float) yPixel / hudTextureHeight;
    }

    private void testHudTutorial() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();

        hudNode = new Node("HUD node");
        hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        hudNode.setLocalTranslation(new Vector3f((float)display.getWidth() / 2f, 40, 0f));

        Quad hudQuad = new Quad("Ortho Q1", 64f, 64f);
//        hudQuad.setLocalScale(2f);
//        hudQuad.setLocalTranslation(new Vector3f(300f, 220f, 0f));
//        hudQuad.setZOrder(0);
        hudQuad.setLightCombineMode(Spatial.LightCombineMode.Off);

        // create the texture state to handle the texture
        final TextureState ts = display.getRenderer().createTextureState();
        // load the image bs a texture (the image should be placed in the same directory bs this class)
        final Texture texture = TextureManager.loadTexture(
                getClass().getResource("/artefacts/hud/targetscannerbackground.png"),
                Texture.MinificationFilter.Trilinear, // of no use for the quad
                Texture.MagnificationFilter.Bilinear, // of no use for the quad
                1.0f, true);
        // set the texture for this texture state
        ts.setTexture(texture);

        // initialize texture width
//        hudTextureWidth = ts.getTexture().getImage().getWidth();
        hudTextureWidth = texture.getImage().getWidth();
        // initialize texture height
//        hudTextureHeight = ts.getTexture().getImage().getHeight();
        hudTextureHeight = texture.getImage().getHeight();
        // activate the texture state
        ts.setEnabled(true);

//        // correct texture application:
//        final FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
//        // coordinate (0,0) for vertex 0
//        texCoords.put(getUForPixel(0)).put(getVForPixel(0));
//        // coordinate (0,40) for vertex 1
//        texCoords.put(getUForPixel(0)).put(getVForPixel(40));
//        // coordinate (40,40) for vertex 2
//        texCoords.put(getUForPixel(40)).put(getVForPixel(40));
//        // coordinate (40,0) for vertex 3
//        texCoords.put(getUForPixel(40)).put(getVForPixel(0));
//        // assign texture coordinates to the quad
//        hudQuad.setTextureCoords(new TexCoords(texCoords));

        // apply the texture state to the quad
        hudQuad.setRenderState(ts);

        // I was hoping to create some alpha blending using a MaterialState and/or
        // a LightState. However, the below render states are not making any
        // difference.
        MaterialState hudMaterialState = display.getRenderer().createMaterialState();
        hudMaterialState.setEnabled(true);
        hudMaterialState.setDiffuse(new ColorRGBA(1, 0, 0, .5f));
//        hudMaterialState.setAmbient(new ColorRGBA(1, 0, 0, .5f));
//        hudMaterialState.setEmissive(new ColorRGBA(1, 0, 0, .5f));
//        hudMaterialState.setSpecular(new ColorRGBA(1, 0, 0, .5f));
//        hudMaterialState.setShininess(128);
        hudNode.setRenderState(hudMaterialState);

//        LightState hudLightState = display.getRenderer().createLightState();
//        hudLightState.setEnabled(true);
//        hudNode.setRenderState(hudLightState);

        BlendState hudBlendState = display.getRenderer().createBlendState();
        hudBlendState.setEnabled(true);
        hudBlendState.setTestEnabled(true);
        hudBlendState.setBlendEnabled(true);
        hudBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        hudBlendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        hudBlendState.setBlendEquation(BlendState.BlendEquation.Subtract);
        hudNode.setRenderState(hudBlendState);

        hudNode.attachChild(hudQuad);
        rootNode.attachChild(hudNode);
    }

    public static Logger getLogger() {
        return logger;
    }

    private void createSkybox() {
        skybox = new Skybox("skybox", 64, 64, 64);

//        Texture skyboxTextureNorth = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_left.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
//        Texture skyboxTextureSouth = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_right.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
//        Texture skyboxTextureEast = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_back.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
//        Texture skyboxTextureWest = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_front.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
//        Texture skyboxTextureUp = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_top.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
//        Texture skyboxTextureDown = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/easter/easter_bottom.png"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);

        Texture skyboxTextureNorth = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_90.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        Texture skyboxTextureSouth = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_270.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        Texture skyboxTextureEast = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_180.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        Texture skyboxTextureWest = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_0.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        Texture skyboxTextureUp = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_top.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        Texture skyboxTextureDown = TextureManager.loadTexture(this.getClass().getResource("/artefacts/skyboxes/hot_nebula/hot_nebula_bottom.tga"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        skybox.setTexture(Skybox.Face.North, skyboxTextureNorth);
        skybox.setTexture(Skybox.Face.South, skyboxTextureSouth);
        skybox.setTexture(Skybox.Face.East, skyboxTextureEast);
        skybox.setTexture(Skybox.Face.West, skyboxTextureWest);
        skybox.setTexture(Skybox.Face.Up, skyboxTextureUp);
        skybox.setTexture(Skybox.Face.Down, skyboxTextureDown);

        rootNode.attachChildAt(skybox, 0);
    }


//    private void testDrawOrtho() {
//        hud = new Node("HUD");
//        hud.setRenderQueueMode(Renderer.QUEUE_ORTHO);
//
//        BlendState hudBlendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
//        hudBlendState.setEnabled(true);
//        hudBlendState.setTestEnabled(true);
//        hudBlendState.setBlendEnabled(true);
//        hudBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
//        hudBlendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
//        hudBlendState.setBlendEquation(BlendState.BlendEquation.Subtract);
//        hud.setRenderState(hudBlendState);
//
////        hud.setRenderState(Renderer.defaultStateList[RenderState.StateType.Light.ordinal()]);
//
//        Quad q1 = new Quad("Ortho Q1", 40f, 40f);
//        q1.setLocalTranslation(new Vector3f(100f, 100f, 0f));
//        q1.setZOrder(0);
////        hudQuad.setDefaultColor(ColorRGBA.blue.clone());
//        q1.setLightCombineMode(Spatial.LightCombineMode.Off);
//
//        MaterialState ms3 = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
//        ms3.setEnabled(true);
//        ms3.setDiffuse(new ColorRGBA(1, 0, 1, .5f));
////        ms3.setAmbient(new ColorRGBA(1, 0, 0, .5f));
////        ms3.setEmissive(new ColorRGBA(1, 0, 0, .5f));
////        ms3.setSpecular(new ColorRGBA(1, 0, 0, .5f));
////        ms3.setShininess(128);
//        q1.setRenderState(ms3);
//
////        ZBufferState zstate = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
////        zstate.setWritable(true);
////        zstate.setEnabled(true);
////        hud.setRenderState(zstate);
//
//        hud.attachChild(q1);
//
//        rootNode.attachChild(hud);
//        rootNode.updateRenderState();
//    }
}
