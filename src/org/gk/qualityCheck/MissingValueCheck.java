package org.gk.qualityCheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.gk.model.Instance;

public class MissingValueCheck extends AbstractClassBasedQualityCheck {

    private String attName;

    protected MissingValueCheck(String clsName, String attName) {
        super(clsName);
        this.attName = attName;
    }

    public String getSchemaAttributeName() {
        return attName;
    }

    @Override
    public String getDescription() {
        return getSchemaClassName() + " instances without a " + getSchemaAttributeName();
    }

    @Override
    protected Collection<Instance> fetchInvalid() {
        return fetchMissing();
    }
    
    protected Collection<Instance> fetchMissing() {
        return fetchInstancesMissingAttribute(getSchemaClassName(), getSchemaAttributeName());
    }

}
