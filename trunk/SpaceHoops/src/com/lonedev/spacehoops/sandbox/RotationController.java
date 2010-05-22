package com.lonedev.spacehoops.sandbox;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

public class RotationController extends Controller {
    private Spatial model;
    private Vector3f axis;
    private float currentRotation = 0f;

    public RotationController(Spatial model, Vector3f axis) {
        this.model = model;
        this.axis = axis;
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            currentRotation += tpf * getSpeed(); // The controller class has a speed variable.
            model.getLocalRotation().fromAngleNormalAxis(currentRotation, axis);
            model.updateModelBound();

            if (currentRotation >= FastMath.PI*2) {
                currentRotation = 0f;
            }
        }
    }
}
