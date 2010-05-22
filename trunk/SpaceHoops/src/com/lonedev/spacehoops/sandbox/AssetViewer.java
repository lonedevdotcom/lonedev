package com.lonedev.spacehoops.sandbox;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.util.export.xml.XMLImporter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author Richard Hawkes
 */
public class AssetViewer extends SimpleGame {
    protected XMLImporter xmlImporter = XMLImporter.getInstance();
    private Logger logger = Logger.getLogger(AssetViewer.class.getName());

    public static void main(String[] args) {
        AssetViewer av = new AssetViewer();
        av.samples = 4; // Smooths things out, but lowers the framerate
        av.depthBits = 4;
        av.setConfigShowMode(ConfigShowMode.AlwaysShow);
        av.start();
    }

    @Override
    protected void simpleInitGame() {
        loadModel("/artefacts/spacefighter01/spacefighter01-jme.xml", Vector3f.ZERO, 1f);
        loadModel("/artefacts/spacefighter01/hoop-jme.xml", Vector3f.ZERO, 1f);
        loadSpaceStation();
    }

    private void loadModel(String spatialURL, Vector3f translation, float scale) {
        try {
            Spatial model = (Spatial) xmlImporter.load(this.getClass().getResource(spatialURL));
            model.setLocalScale(scale);
            model.setModelBound(new BoundingBox());
            model.updateModelBound();
            rootNode.attachChild(model);
            model.setLocalTranslation(translation);
            rootNode.updateRenderState();
        } catch (IOException ex) {
            logger.severe("Unable to load spatial: " + ex);
        }
    }

    private void loadSpaceStation() {
        loadModel("/artefacts/spacestation/spacestation-jme.xml", new Vector3f(0, 0, -600), 2f);

        Spatial spaceStation = rootNode.getChild("spacestation");
        Controller rotationController = new RotationController(spaceStation, Vector3f.UNIT_Y);
        rotationController.setSpeed(0.02f);
        spaceStation.addController(rotationController);

        // Adding a cull state to ignore the "back" side of the vertexes. This
        // squeezes a slightly better frame rate :)
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        spaceStation.setRenderState(cs);
    }
}
