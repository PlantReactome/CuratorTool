package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T023_DatabaseIdentifierWithoutReferenceDatabase extends MissingValueCheck {

    public T023_DatabaseIdentifierWithoutReferenceDatabase() {
        super(ReactomeJavaConstants.DatabaseIdentifier, ReactomeJavaConstants.referenceDatabase);
    }

}
