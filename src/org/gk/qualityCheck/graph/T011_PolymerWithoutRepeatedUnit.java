package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T011_PolymerWithoutRepeatedUnit extends MissingValueCheck {

    private static final String DESCRIPTION = "Polymers without a repeated unit";
 
    public T011_PolymerWithoutRepeatedUnit() {
        super(ReactomeJavaConstants.Polymer,
              ReactomeJavaConstants.repeatedUnit);
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
