package jme3.test01;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class GunController extends AbstractControl {

    private EnemyTarget currentTarget = null;
    
    @Override
    protected void controlUpdate(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Unused at the moment.
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        // TODO: Allow clone creation. This will be important because we'll be cloning these guys a lot to save memory.
        return null;
    }
    
    public void setSpatial(Spatial shootingTower) {
        if (! (shootingTower instanceof ShootingTower)) {
            throw new RuntimeException("");
        }
        this.spatial = shootingTower;
    }
    
}
