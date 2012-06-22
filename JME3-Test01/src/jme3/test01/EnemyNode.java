package jme3.test01;

import com.jme3.scene.Node;

/**
 *
 * @author Richard Hawkes
 */
public class EnemyNode extends Node {
    private float speed;
    private float health;

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the health
     */
    public float getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(float health) {
        this.health = health;
    }
}
