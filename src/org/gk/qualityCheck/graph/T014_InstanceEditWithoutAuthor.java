package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T014_InstanceEditWithoutAuthor extends MissingValueCheck {

    public T014_InstanceEditWithoutAuthor() {
        super(ReactomeJavaConstants.InstanceEdit, ReactomeJavaConstants.author);
    }

}
