package com.lonedev.gtroot.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;

/**
 * @author Richard Hawkes
 */
public class RocketManagedObject implements ManagedObject, Serializable {
    private String name;

    private static final long serialVersionUID = 1L;

    public RocketManagedObject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        AppContext.getDataManager().markForUpdate(this);
        this.name = name;
    }
}
