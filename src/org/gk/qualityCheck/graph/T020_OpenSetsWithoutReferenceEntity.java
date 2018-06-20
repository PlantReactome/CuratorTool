package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T020_OpenSetsWithoutReferenceEntity extends MissingValueCheck {

    public T020_OpenSetsWithoutReferenceEntity() {
        super(ReactomeJavaConstants.OpenSet, ReactomeJavaConstants.referenceEntity);
    }

}
