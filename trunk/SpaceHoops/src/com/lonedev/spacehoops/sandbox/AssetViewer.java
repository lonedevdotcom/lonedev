package com.lonedev.spacehoops.sandbox;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
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
        av.samples = 0; // Smooths things out, but lowers the framerate
        av.depthBits = 0;
        av.setConfigShowMode(ConfigShowMode.AlwaysShow);
        av.start();
    }

    @Override
    protected void simpleInitGame() {
        loadModel(new File("artefacts/spacefighter01/spacefighter01-jme.xml"), Vector3f.ZERO, 1f);
        loadModel(new File("artefacts/spacefighter01/hoop-jme.xml"), Vector3f.ZERO, 1f);
        loadModel(new File("artefacts/spacestation/spacestation-jme.xml"), new Vector3f(0, 0, -600), 2f);

        Spatial spacestation = rootNode.getChild("spacestation");
        Controller rotationController = new RotationController(spacestation, Vector3f.UNIT_Y);
        rotationController.setSpeed(0.02f);
        spacestation.addController(rotationController);
    }

    private void loadModel(File spatialFile, Vector3f translation, float scale) {
        try {
            Spatial model = (Spatial) xmlImporter.load(spatialFile);
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
}
