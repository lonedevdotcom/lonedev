package com.lonedev.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/** Sample 2 - How to use nodes as handles to manipulate objects in the scene graph.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
public class HelloNode extends SimpleApplication {

    public static void main(String[] args){
        HelloNode app = new HelloNode();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // create a blue box at coordinates (1,-1,1)
        Box box1 = new Box( new Vector3f(1,-1,1), 1,1,1);
        Geometry blue = new Geometry("Box", box1);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
        mat1.setColor("m_Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);

        // create a red box straight above the blue one at (1,3,1)
        Box box2 = new Box( new Vector3f(1,3,1), 1,1,1);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
        mat2.setColor("m_Color", ColorRGBA.Red);
        red.setMaterial(mat2);

        // create a pivot node at (0,0,0) and attach it to root
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot);

        // attach the two boxes to the *pivot* node!
        pivot.attachChild(blue);
        pivot.attachChild(red);
        // rotate pivot node: Both boxes have rotated!
        pivot.rotate( 0.4f , 0.4f , 0.0f );

    }
}
