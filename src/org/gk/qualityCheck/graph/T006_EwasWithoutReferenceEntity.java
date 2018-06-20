package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T006_EwasWithoutReferenceEntity extends MissingValueCheck {
    public T006_EwasWithoutReferenceEntity() {
        super(ReactomeJavaConstants.EntityWithAccessionedSequence,
                ReactomeJavaConstants.referenceEntity);
    }
}
