import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jmex.angelfont.Rectangle;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapFontLoader;
import com.jmex.angelfont.BitmapText;
//import com.lonedev.spacehoops.sandbox.RotationController;
import com.lonedev.spacehoops.sandbox.TextTickerController;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitmapFontExample extends SimpleGame {
    private String fullText = "Richard Hawkes is really cool, really very cool in fact!";
    private BitmapText txt;

    public static void main(String[] args) {
        SimpleGame sg = new BitmapFontExample();
        sg.setConfigShowMode(ConfigShowMode.AlwaysShow);
        sg.start();
    }

    @Override
    public void simpleUpdate() {

    }

    protected void simpleInitGame() {
        addCube();
        testBitmapFont();
    }

    private void addCube() {
        Box b = new Box("Cube", new Vector3f(0f, 0f, -100f), 20f, 20f, 20f);
        rootNode.attachChild(b);
        rootNode.updateRenderState();
    }

    private void testBitmapFont() {
        File fontFile = new File("artefacts/fonts/battlefield16.fnt");
        File textureFile = new File("artefacts/fonts/battlefield16_0.png");

        BitmapFont fnt = null;

        try {
            fnt = BitmapFontLoader.load(fontFile.toURI().toURL(), textureFile.toURI().toURL());
        } catch(Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Unable to load font: " + ex);
            System.exit(1);
        }

        txt = new BitmapText(fnt, false);
        txt.setBox(new Rectangle(0, 0, 320, 100));
        txt.setSize(16);

        // Setting the text to green, and alpha so it blends a bit with the background
        txt.setDefaultColor(new ColorRGBA(0f, 1f, 0f, .8f));
        txt.setLightCombineMode(LightCombineMode.Off);

        txt.addController(new TextTickerController(txt, fullText));
//        txt.setText(fullText);
//        txt.update();

        Node orthoNode = new Node("Ortho Node for Fonts");
        orthoNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        orthoNode.setLocalTranslation(10, 100, 0);
        orthoNode.setCullHint(CullHint.Never);

        orthoNode.attachChild(txt);
        rootNode.attachChild(orthoNode);
        rootNode.updateRenderState();
    }
}
