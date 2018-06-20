package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T013_ReferenceEntityWithoutIdentifier extends MissingValueCheck {

    public T013_ReferenceEntityWithoutIdentifier() {
        super(ReactomeJavaConstants.ReferenceEntity, ReactomeJavaConstants.identifier);
    }

}
