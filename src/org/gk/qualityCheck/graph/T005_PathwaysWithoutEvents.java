package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T005_PathwaysWithoutEvents extends MissingValueCheck {
    
    public T005_PathwaysWithoutEvents() {
        super(ReactomeJavaConstants.Pathway,
                ReactomeJavaConstants.hasEvent);
    }

}
