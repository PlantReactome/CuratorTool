package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T012_SimpleEntityWithoutReferenceEntity extends MissingValueCheck {

    public T012_SimpleEntityWithoutReferenceEntity() {
        super(ReactomeJavaConstants.SimpleEntity,
              ReactomeJavaConstants.referenceEntity);
    }

}
