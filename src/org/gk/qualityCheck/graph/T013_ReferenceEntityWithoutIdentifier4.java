package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T013_ReferenceEntityWithoutIdentifier4 extends MissingValueCheck {

    public T013_ReferenceEntityWithoutIdentifier4() {
        super(ReactomeJavaConstants.ReferenceEntity, ReactomeJavaConstants.identifier);
    }

}
