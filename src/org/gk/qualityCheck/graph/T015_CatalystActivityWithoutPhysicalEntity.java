package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T015_CatalystActivityWithoutPhysicalEntity extends MissingValueCheck {

    public T015_CatalystActivityWithoutPhysicalEntity() {
        super(ReactomeJavaConstants.CatalystActivity, ReactomeJavaConstants.physicalEntity);
    }

}
