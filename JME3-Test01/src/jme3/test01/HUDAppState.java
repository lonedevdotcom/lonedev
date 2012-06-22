package jme3.test01;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class HUDAppState extends AbstractAppState implements ScreenController {
    SimpleApplication simpleApp;
    Nifty nifty;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        simpleApp = (SimpleApplication)app;
        
        initHUDDisplay();
    }

    private void initHUDDisplay() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(simpleApp.getAssetManager(), simpleApp.getInputManager(), simpleApp.getAudioRenderer(), simpleApp.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("gui2.xml", "start", this);
        // attach the nifty display to the gui view port as a processor
        simpleApp.getGuiViewPort().addProcessor(niftyDisplay);

        // disable the fly cam
//        simpleApp.getFlyByCamera().setEnabled(false);
        simpleApp.getFlyByCamera().setDragToRotate(true);
        simpleApp.getInputManager().setCursorVisible(true);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    @Override
    public void onStartScreen() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onEndScreen() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
