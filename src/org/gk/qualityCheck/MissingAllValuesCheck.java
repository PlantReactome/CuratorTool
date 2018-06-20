package org.gk.qualityCheck;

import java.util.Collection;
import org.gk.model.Instance;

abstract public class MissingAllValuesCheck extends MissingValuesCheck {

    public MissingAllValuesCheck(String clsName, String ...attNames) {
        super(clsName, attNames);
    }

    @Override
    public String getDescription() {
        return super.getDescription("all");
    }

    @Override
    public void testCheck() {
        // Only one of the fixture instances is missing every value.
        super.testCheck(1);
    }

    @Override
    protected Collection<Instance> fetchMissing() {
        return super.fetchInstancesMissingAllAttributes(
                getSchemaClassName(),
                getAttributeNames());
    }

}
