package jme3.test01;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Manages the Y rotation of a spatial at a given target. The target can be
 * changed at any time, and this controller will reset as appropriate.
 *
 * @author Richard Hawkes
 */
public class TargetRotationControl extends AbstractControl {

    private Spatial targetSpatial;
    private Spatial spatialCopy; // = new Node("spatialCopy");
    private Quaternion startRotation = new Quaternion();
    private float currentSlerpAmount = 0f;
    private float rotationSpeed = 1f;
    private float closenessFactor; // This ensure that shorter rotations are quicker than rotations over a longer distance.
    private Quaternion newRotation = new Quaternion();
    private boolean lockedOnTarget = false;

    public TargetRotationControl() {
//        this.targetSpatial = targetSpatial;
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

    /**
     * This is the main legwork method that performs the rotation. There are a
     * few scenarios that this needs to cater for (eg no current target, whether
     * or not this control is enabled etc).
     *
     * @param tpf time per frame (ie framerate).
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (isEnabled() && targetSpatial != null) {
            // Get the copy to look directly at the target spatial.
            spatialCopy.lookAt(targetSpatial.getLocalTranslation(), Vector3f.UNIT_Y);

            if (!isLockedOnTarget() && currentSlerpAmount <= 1f) {
                // Update the slerp value based on the frame rate, speed, and "closeness factor".
                currentSlerpAmount += (tpf * rotationSpeed) * closenessFactor;

                // reset newRotation to the startRotation. This gets done every frame so that slerping can occur.
                shallowCopyQuaternion(startRotation, newRotation);

                // Now we can slerp (interpolate) between the starting rotation, 
                // and the desired end location (ie what the spatialCopy rotation is).
                newRotation.slerp(spatialCopy.getLocalRotation(), currentSlerpAmount);
                rotateSpatialOnlyOnYAxis(newRotation); // y-axis rotation only.
            } else {
                // If we've passed 1, then just aim it directly at the target spatial.
                lockedOnTarget = true;
                rotateSpatialOnlyOnYAxis(spatialCopy.getLocalRotation());
            }
        } else {
            lockedOnTarget = false;
        }
    }

    /**
     * Returns the distance as a float to the target rotation. This is intended
     * to be used with some sort of "range" feature.
     *
     * @return The distance of the current spatial to the targetSpatial we are
     * aiming at.
     */
    private float distanceToTargetSpatial() {
        return spatial.getWorldTranslation().distance(targetSpatial.getWorldTranslation());
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        lockedOnTarget = false; // Possibly unnecesary, but I don't exactly know how may times this gets called.
        spatialCopy = new Node(spatial.getName() + "-COPY");
        spatialCopy.setLocalTranslation(spatial.getLocalTranslation()); // Beware, this is copying the reference to the spatial location. If the spatial moves, so will the copy (which is actually what we're after here).
    }

    /**
     * Change the target that this spatial is to rotate towards. This will reset
     * the rotation again.
     * 
     * Note that null is a valid parameter here and is used when (say) its
     * current target is destroyed and we're in the process of looking for
     * another one.
     *
     * @param targetSpatial The spatial to rotate towards, or null to leave it twiddling its fingers!
     */
    public void setTargetSpatial(Spatial targetSpatial) {
        this.targetSpatial = targetSpatial;
        lockedOnTarget = false;

        if (targetSpatial != null) {
            shallowCopyQuaternion(spatial.getLocalRotation(), startRotation);
            currentSlerpAmount = 0f;


            // Here's that "dot" product calculation. If the rotation is very minor,
            // then the dot product (which I'm calling "closenessFactor"!) will be
            // nearly 1. If it's the complete opposite (ie more rotation), it will 
            // be near zero. By using this as part of the slerp above, I'm correctly 
            // seeing that something with more rotation required takes longer than 
            // something with very little rotation. I don't confess to fully 
            // understand this.
            spatialCopy.lookAt(this.targetSpatial.getLocalTranslation(), Vector3f.UNIT_Y); // look at the target first so we can see how "close" it is (next).
            closenessFactor = Math.abs(startRotation.dot(spatialCopy.getLocalRotation())); // use abs to ensure you don't get a negative value.
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        TargetRotationControl srt = new TargetRotationControl();
        srt.setSpatial(sptl);
        return srt;
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

    /**
     * @return the lockedOnTarget
     */
    public boolean isLockedOnTarget() {
        return lockedOnTarget;
    }
}
