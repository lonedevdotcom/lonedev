/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3.test01;

import com.jme3.scene.Spatial;

/**
 *
 * @author Richard
 */
public class EnemyTarget {
    private Spatial mesh;
    private int health = 100;

    /**
     * @return the mesh
     */
    public Spatial getMesh() {
        return mesh;
    }

    /**
     * @param mesh the mesh to set
     */
    public void setMesh(Spatial mesh) {
        this.mesh = mesh;
    }

    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(int health) {
        this.health = health;
    }
    
}
