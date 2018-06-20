package org.gk.qualityCheck.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gk.database.InstanceListPane;
import org.gk.model.GKInstance;
import org.gk.model.Instance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.persistence.MySQLAdaptor;
import org.gk.qualityCheck.ClassBasedQualityCheck;
import org.gk.qualityCheck.MissingValueCheck;
import org.gk.qualityCheck.QAReport;
import org.gk.schema.GKSchemaClass;
import org.gk.schema.InvalidAttributeException;
import org.gk.schema.InvalidAttributeValueException;
import org.gk.schema.Schema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.org.gk.qualityCheck.TestUtil;

public class T007_EntitiesWithoutStdId extends MissingValueCheck {
    
    public T007_EntitiesWithoutStdId() {
        super(ReactomeJavaConstants.PhysicalEntity,
                ReactomeJavaConstants.stableIdentifier);
    }

    private static final String DESCRIPTION = "Entities without a stable id";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
    @Override
    protected Collection<Instance> fetchMissing() {
        Collection<Instance> missingEntities = super.fetchMissing();
        Collection<Instance> missingEvents = fetchInstancesMissingAttribute(
                ReactomeJavaConstants.Event,
                ReactomeJavaConstants.stableIdentifier);
        return Stream.concat(missingEntities.stream(), missingEvents.stream())
                     .collect(Collectors.toList());
    }

    @Override
    protected List<Instance> createTestFixture() {
        List<Instance> fixture = new ArrayList<Instance>();
        Instance entity = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(entity);
        Instance event = createInstance(ReactomeJavaConstants.BlackBoxEvent);
        fixture.add(event);
        return fixture;
    }

}
