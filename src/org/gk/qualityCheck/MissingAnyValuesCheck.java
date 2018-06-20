package org.gk.qualityCheck;

import java.util.Collection;
import org.gk.model.Instance;

abstract public class MissingAnyValuesCheck extends MissingValuesCheck {

    public MissingAnyValuesCheck(String clsName, String ...attNames) {
        super(clsName, attNames);
    }

    @Override
    public String getDescription() {
        return getDescription("any");
    }

    @Override
    public void testCheck() {
        // All but one of the fixture instances is missing at least
        // one value.
        super.testCheck(fixture.size() - 1);
    }

    @Override
    protected Collection<Instance> fetchMissing() {
        return super.fetchInstancesMissingAnyAttributes(
                getSchemaClassName(),
                getAttributeNames());
    }

}
