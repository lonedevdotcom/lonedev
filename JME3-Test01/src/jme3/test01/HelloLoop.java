package jme3.test01;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Sample 4 - how to trigger repeating actions from the main update loop. In
 * this example, we make the player character rotate.
 */
public class HelloLoop extends SimpleApplication {

    protected Spatial player1, player2;

    public static void main(String[] args) {

        // If you want to use anti-aliasing, set the nVidia control panel to 
        // always use the graphics card (rather than the intel gpu).
        HelloLoop app = new HelloLoop();
        app.start();
    }

    @Override
    public void simpleInitApp() {

//        Box b1 = new Box(Vector3f.ZERO, 1, 1, 1);
//        player1 = new Geometry("blue cube", b1);
        player1 = assetManager.loadModel("basic_gun.obj");
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        mat1.setTexture("ColorMap", assetManager.loadTexture("diffuse.png"));

//        mat1.setColor("Color", ColorRGBA.Blue);
        player1.setMaterial(mat1);
        rootNode.attachChild(player1);

        Box b2 = new Box(Vector3f.ZERO, 1, 1, 1);
        player2 = new Geometry("red cube", b2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        player2.setMaterial(mat2);
        player2.setLocalTranslation(3, 3, -3);
        rootNode.attachChild(player2);

        //        player1.addControl(new SpatialRotatationControl(player2));
//        System.out.println(((Node)player1).getChild(0).getName());
//        ((Node)player1).getChild(0).addControl(new SpatialRotatationControl(player2));
//        player2.addControl(new SpatialRotatationControl(player1));
        
    }

    /*
     * This is the update loop
     */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate
//        player1.rotate(0, 2 * tpf, 0);
    }
}