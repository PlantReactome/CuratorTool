package org.gk.qualityCheck.graph;

import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T016_CatalystActivityWithoutActivity extends MissingValueCheck {

    public T016_CatalystActivityWithoutActivity() {
        super(ReactomeJavaConstants.CatalystActivity, ReactomeJavaConstants.activity);
    }

}
