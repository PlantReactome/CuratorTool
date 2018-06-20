package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T009_ComplexWithoutComponents extends MissingValueCheck {

    public T009_ComplexWithoutComponents() {
        super(ReactomeJavaConstants.Complex,
                ReactomeJavaConstants.hasComponent);
    }

}
