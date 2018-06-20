package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T022_PhysicalEntityWithoutCompartment extends MissingValueCheck {

    public T022_PhysicalEntityWithoutCompartment() {
        super(ReactomeJavaConstants.PhysicalEntity, ReactomeJavaConstants.compartment);
    }

}
