package jme3.test01;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Controls the AI for a turret. When loaded, it will scan the enemies list and
 * see if one is in range. If it is, it will quickly rotate towards and remain
 * "locked on" until it goes out of range (or is destroyed). It then resets and
 * starts looking again.
 * 
 * @author Richard Hawkes
 */
public class TurretControl extends AbstractControl implements Callable<Spatial> {
    
    private Spatial enemyTarget;
    
    private float turretFiringRange = 5.5f;
    private float turretRotationSpeed = 0.5f;
    
    private boolean lockedOnToTarget;
    private long firingIntervalMilliseconds;
    private long lastFireTimeMilliseconds = System.currentTimeMillis();
    private List<Spatial> enemies; 
    private Future<Spatial> enemySearchTask;
    private Spatial spatialCopy; // used for lookAt operations for rotational reference.
    
    private float currentSlerpAmount = 0f;
    private Quaternion spatialStartRotation;

    private Application mainApplication;
    private boolean debugMode = false;

    public TurretControl(List<Spatial> enemies) {
        assert enemies != null;
                
        this.enemies = enemies;
    }
    
    public TurretControl() {
        this(new ArrayList<Spatial>()); // empty list of enemies!
    }

    /**
     * Use this constructor if you wish to have debug mode on. Not required if
     * debug mode is off.
     * 
     * @param enemies The list of enemies.
     * @param mainApplication  The "main" JME application.
     */
    public TurretControl(List<Spatial> enemies, Application mainApplication) {
        this(enemies);
        this.mainApplication = mainApplication;
        this.debugMode = true;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (isEnabled()) {
            maybeShowDebugDetails();
            
            if (targettingAnEnemy()) {
                if (enemySpatialIsInRange(getEnemyTarget())) {
                    if (isLockedOnToTarget()) {
                        lookAtTarget(); // Once we're locked on, always stay locked on!
                        maybeFire();
                    } else {
                        rotateTowardsTarget(tpf);
                    }
                } else {
                    removeTarget();
                }
            } else {
                lookForTarget();
            }
        }
    }
    
    
    @Override
    public void setSpatial(Spatial spatial) {
        assert spatial != null;
        
        super.setSpatial(spatial);
        spatialCopy = new Node("spatialCopy");
        spatialCopy.setLocalTranslation(spatial.getLocalTranslation());        
    }
    
    /**
     * Copies the values from one Quaternion to another. This allows a
     * Quaternion instance to be re-used, rather than continually cloning one.
     * Although cloning may be quick, we are doing it A LOT (once per frame), so
     * this method should seriously reduce object count.
     *
     * @param from The source quaternion.
     * @param to The quaternion to copy the values to.
     */
    private void shallowCopyQuaternion(Quaternion from, Quaternion to) {
        to.set(from.getX(), from.getY(), from.getZ(), from.getW());
    }    
    
    
    public void setEnemies(List<Spatial> enemies) {
        assert enemies != null;
        this.setEnemies(enemies);
    }
    

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
    @Override
    public Control cloneForSpatial(Spatial sptl) {
        throw new UnsupportedOperationException("Cloning of TurretControl is not supported yet.");
    }

    private boolean targettingAnEnemy() {
        return getEnemyTarget() != null;
    }

    private boolean enemySpatialIsInRange(Spatial enemySpatial) {
        return distanceToEnemy(enemySpatial) < getTurretFiringRange();
    }

    private boolean isLockedOnToTarget() {
        return lockedOnToTarget;
    }

    // A pre-instantiated quaternion instance that is re-used in each call to
    // rotateTowardsTarget(...) below.
    private Quaternion newRotation = new Quaternion();
    
    private void rotateTowardsTarget(float tpf) {
        spatialCopy.lookAt(getEnemyTarget().getLocalTranslation(), Vector3f.UNIT_Y);

        if (currentSlerpAmount <= 1f) {
            currentSlerpAmount += ( tpf * getTurretRotationSpeed() );
            shallowCopyQuaternion(spatialStartRotation, newRotation);
//            Quaternion newRotation = spatialStartRotation.clone();
            newRotation.slerp(spatialCopy.getLocalRotation(), currentSlerpAmount);
            rotateSpatialOnlyOnYAxis(newRotation);
        } else {
            setLockedOnToTarget(true);
        }
    }

    private void lookAtTarget() {
        spatialCopy.lookAt(getEnemyTarget().getLocalTranslation(), Vector3f.UNIT_Y);
        rotateSpatialOnlyOnYAxis(spatialCopy.getLocalRotation());
    }
    
    // These variables are used by rotateSpatialOnlyOnYAxis (below). They exist
    // to stop the recreation of objects each time this is called (ie every
    // single frame).
    private float[] currentSpatialAngles = new float[3];
    private float[] newAngles = new float[3];
    private Quaternion newYAxisOnlyRotation = new Quaternion();
    
    /**
     * Rotates the spatial on its Y axis only. We don't want the turret spinning
     * in odd directions which would happen if the target got close. This long-
     * winded effort creates a new quaternion with the x and z of the original,
     * and the y of the new one.
     *
     * @param newRotation The new rotation to spin to (y axis only remember!).
     */
    private void rotateSpatialOnlyOnYAxis(Quaternion newRotation) {
        Quaternion currentSpatialRotation = this.spatial.getLocalRotation();
        currentSpatialAngles = currentSpatialRotation.toAngles(null); // float[3] array returned.
        newAngles = newRotation.toAngles(null); // float[3] array returned.
        currentSpatialAngles[1] = newAngles[1]; // copy only the y axis of the new angles to the current angles.

        // Updating the quaternions rotation settings using "fromAngles". This
        // method takes the xyz float and does the maths to conver it to a wxyz
        // quaternion. I could have created a new quaternion with the float[], 
        // but this means a new object every frame. As you've probably guessed 
        // by now, I'm not keen on this!
        newYAxisOnlyRotation.fromAngles(currentSpatialAngles);

        spatial.setLocalRotation(newYAxisOnlyRotation);
    }    

    private void maybeFire() {
        if (System.currentTimeMillis() > (lastFireTimeMilliseconds+firingIntervalMilliseconds)) {
            fire();
            lastFireTimeMilliseconds = System.currentTimeMillis();
        }
    }

    private void removeTarget() {
        setEnemyTarget(null);
    }

    private void lookForTarget() {
        if (enemySearchTask == null) {
            // We haven't started the thread to look for the nearest enemy yet.
            enemySearchTask = TerrainSplatTest.executor.submit(this);
        } else if (enemySearchTask.isDone()) {
            try {
                setEnemyTarget(enemySearchTask.get()); // The get might return null if no enemy target was found.
            } catch (Exception ex) {
                System.err.println("Weird. The future.get task failed: " + ex);
            } finally {
                enemySearchTask = null; // nullify if it's complete (or throws exception).
            }
        } else if (enemySearchTask.isCancelled()) {
            // cancelled? why?
            enemySearchTask = null;
        } else {
            // If we're here, then the task must still be in progress. Nothing
            // to do.
        }
    }

    private void fire() {
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Spatial call() {
        Spatial nearestSpatial = null;
        float nearestDistance = 10000f;
        
        for (Spatial enemy : getEnemies()) {
            if (enemySpatialIsInRange(enemy) && distanceToEnemy(enemy) < nearestDistance) {
                nearestSpatial = enemy;
                nearestDistance = distanceToEnemy(enemy);
            }
        }
        
        return nearestSpatial;
    }

    private float distanceToEnemy(Spatial enemy) {
        return spatial.getWorldTranslation().distance(enemy.getWorldTranslation());
    }

    /**
     * @return the enemyTarget
     */
    public Spatial getEnemyTarget() {
        return enemyTarget;
    }

    /**
     * @param enemyTarget the enemyTarget to set
     */
    public void setEnemyTarget(Spatial enemyTarget) {
        this.enemyTarget = enemyTarget;
        currentSlerpAmount = 0f;
        setLockedOnToTarget(false);
        spatialStartRotation = spatial.getLocalRotation().clone();
    }

    /**
     * @return the turretFiringRange
     */
    public float getTurretFiringRange() {
        return turretFiringRange;
    }

    /**
     * @param turretFiringRange the turretFiringRange to set
     */
    public void setTurretFiringRange(float turretFiringRange) {
        this.turretFiringRange = turretFiringRange;
    }

    /**
     * @return the turretRotationSpeed
     */
    public float getTurretRotationSpeed() {
        return turretRotationSpeed;
    }

    /**
     * @param turretRotationSpeed the turretRotationSpeed to set
     */
    public void setTurretRotationSpeed(float turretRotationSpeed) {
        this.turretRotationSpeed = turretRotationSpeed;
    }

    /**
     * @param lockedOnToTarget the lockedOnToTarget to set
     */
    public void setLockedOnToTarget(boolean lockedOnToTarget) {
        this.lockedOnToTarget = lockedOnToTarget;
    }

    /**
     * @return the enemies
     */
    public List<Spatial> getEnemies() {
        return enemies;
    }

    // vars to use for debug. 
    private Cylinder rangeDisplay;
    
    private void maybeShowDebugDetails() {
        // note that the spatial may not yet have a parent (perhaps it hasn't
        // been attached to the root node yet), so we have to check for this.
        if (isDebugMode() && spatial.getParent() != null) {
            if (rangeDisplay == null) {
                Material debugMaterial = new Material(mainApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                debugMaterial.setColor("Color", new ColorRGBA(1,0,0,0.5f));
                debugMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                debugMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
                rangeDisplay = new Cylinder(24, 12, turretFiringRange, 0.5f, false, true);

                Geometry gg = new Geometry("range", rangeDisplay);
                gg.setCullHint(Spatial.CullHint.Dynamic);
                gg.setMaterial(debugMaterial);
                gg.setQueueBucket(RenderQueue.Bucket.Transparent);
                spatial.getParent().attachChild(gg);
                gg.setLocalTranslation(spatial.getLocalTranslation());
                gg.rotate(FastMath.PI/2f, 0f, 0f);
            }
        }
    }
    
    
//    private static Material debugMaterial;
//    
//    private static Material getDebugMaterial(AssetManager assetManager) {
//        if (debugMaterial == null) {
//                debugMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//                debugMaterial.setColor("Color", new ColorRGBA(1,0,0,0.5f));
//                debugMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//                debugMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
//        }
//        
//        return debugMaterial;
//    }

    /**
     * @return the debugMode
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * @param debugMode the debugModeOn to set
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
}
