package jme3.test01;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * For any given enemy, this control moves them along a Path instance. Once
 * reached, an alert will be triggered.
 *
 * It's TBD on whether this controller should hold the enemies health details.
 * If it does, then it will need to alert the parent spatial about the various
 * events that could occur (namely being destroyed).
 *
 * @author Richard Hawkes
 */
public class EnemyControlDontUse extends AbstractControl {

    private MotionPath enemyPath;

    private enum State {
        NOT_STARTED, IN_MOTION, COMPLETED
    }
    private State currentControlState = State.NOT_STARTED;

    public EnemyControlDontUse(MotionPath enemyPath) {
        assert enemyPath != null;
        assert enemyPath.getNbWayPoints() > 1;
        
        this.enemyPath = enemyPath;
    }

    @Override
    protected void controlUpdate(float tpf) {
        switch (currentControlState) {
            case NOT_STARTED:
                initializePosition();
                break;
            case IN_MOTION:
                // Nothing yet!
                break;
            default:
                throw new UnsupportedOperationException("Unsupported State!?!? " + currentControlState);
        }
        // TODO Implement moving this enemy around.
        // pseudo-code:
        // Place enemy at starting point along path.
        // At turning points, rotate to face new direction.
        // Continue along that path etc etc until destination reached.
        // If reached, alert parent spatial that we have reached our destination!
    }

    private void initializePosition() {
//        spatial.setLocalTranslation(enemyPath.getPathPoints().get(0));
        
        // Not happy with just using "lookAt". This is probably going to make it
        // point slightly up or down, unless we constantly fiddle with the Y 
        // value on the path points.
        // TODO Create a clone of the current spatial at its position, then get that to look at the next path point. We can then rotate the spatials x and z values to match the cloned ones... Hey presto !!
//        spatial.lookAt(enemyPath.getPathPoints().get(1), Vector3f.UNIT_Y);
        MotionTrack enemyPathTrack = new MotionTrack(spatial, enemyPath);
        enemyPathTrack.setDirectionType(MotionTrack.Direction.PathAndRotation);
        enemyPathTrack.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.PI*2, Vector3f.UNIT_Y)); // NO IDEA??
        enemyPathTrack.setInitialDuration(10f);
        enemyPathTrack.setSpeed(1f);
        enemyPathTrack.play();
        enemyPathTrack.addListener(new CinematicEventListener() {

            @Override
            public void onPlay(CinematicEvent ce) { }

            @Override
            public void onPause(CinematicEvent ce) { }

            @Override
            public void onStop(CinematicEvent ce) {
                System.out.println("I have reached my destination!!");
            }
            
        });
        
        this.currentControlState = State.IN_MOTION;
    }
    
    private void rotateOnYAxisOnly(Spatial spatialToRotate, Vector3f pointToRotateTowards) {
        // pseudo:
        //      create spatial copy
        //      spatial copy to look at next path point
        //      get it's y co-ordinate (getLocalRotation.toAngles(null)
        //      create a new quaternion with the x and z co-ordinates of the real spatial and the y co-ordinates of the copy.
        //      rotate the real spatial to this quaternion.        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Nothing to do here (as far as I know).
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        // TODO Sort out cloning. I'm assuming there will be a fair bit of 
        // cloning going on, so we should ensure it's working well.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpatial(Spatial spatial) {
        assert spatial instanceof EnemyNode;
        
        super.setSpatial(spatial);
    }
}
