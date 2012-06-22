package jme3.test01;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;

public class MainMenuAppState extends AbstractAppState {

    private Nifty nifty;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        TerrainSplatTest tstApp = (TerrainSplatTest)app;
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(tstApp.getAssetManager(), tstApp.getInputManager(), tstApp.getAudioRenderer(), tstApp.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
       
        ScreenBuilder mainMenuScreenBuilder = new ScreenBuilder("mainMenu");

        LayerBuilder lb = new LayerBuilder("mainLayer");
        lb.backgroundColor("#333333ff");
        lb.childLayoutCenter();
        
        PanelBuilder pb = new PanelBuilder("mainPanel");
        pb.backgroundColor("#0000");
        pb.childLayoutVertical();
        
        TextBuilder tb1 = new TextBuilder("newGameText");
        tb1.font("space-age-48.fnt"); // without a font, nothing is displayed!!
        tb1.text("New Game");
        tb1.alignCenter();
        tb1.height("44");
        tb1.color("#cc00ffff");
        
        TextBuilder tb2 = new TextBuilder("continueGameText");
        tb2.font("space-age-48.fnt"); // without a font, nothing is displayed!!
        tb2.text("Continue Game");
        tb2.alignCenter();
        tb2.height("44");
        tb2.color("#cc00ffff");
        
        TextBuilder tb3 = new TextBuilder("optionsText");
        tb3.font("space-age-48.fnt"); // without a font, nothing is displayed!!
        tb3.text("Options");
        tb3.alignCenter();
        tb3.height("44");
        tb3.color("#cc00ffff");
        
        TextBuilder tb4 = new TextBuilder("quitGameText");
        tb4.font("space-age-48.fnt"); // without a font, nothing is displayed!!
        tb4.text("Quit Game");
        tb4.alignCenter();
        tb4.height("44");
        tb4.color("#cc00ffff");
        
        pb.text(tb1);
        pb.text(tb2);
        pb.text(tb3);
        pb.text(tb4);
        
        lb.panel(pb);
        
        mainMenuScreenBuilder.layer(lb);
        Screen mainMenuScreen = mainMenuScreenBuilder.build(nifty);
        nifty.addScreen("startMenuScreen", mainMenuScreen);

        
        // disable the fly cam
//        simpleApp.getFlyByCamera().setEnabled(false);
        // attach the nifty display to the gui view port as a processor
        tstApp.getGuiViewPort().addProcessor(niftyDisplay);

//        tstApp.getFlyByCamera().setDragToRotate(true);
//        tstApp.getInputManager().setCursorVisible(true);
        nifty.gotoScreen("startMenuScreen");
    }
}
