package org.gk.qualityCheck.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.gk.model.GKInstance;
import org.gk.model.Instance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.AbstractClassBasedQualityCheck;

public class T047_OrphanEvents extends AbstractClassBasedQualityCheck {

    public T047_OrphanEvents() {
        super(ReactomeJavaConstants.Event);
    }

    @Override
    public String getDescription() {
        return "Events that cannot be reached through the events hierarchy";
    }

    @Override
    protected Collection<Instance> fetchInvalid() {
        Set<Instance> unreferencedByPathway = fetchUnreferenced(
                ReactomeJavaConstants.Pathway,
                ReactomeJavaConstants.hasEvent);
        Set<Instance> unreferencedByBBE = fetchUnreferenced(
                ReactomeJavaConstants.BlackBoxEvent,
                ReactomeJavaConstants.hasEvent);
        //The top-level events.
        Set<GKInstance> tles = getTopLevelPathways();

        Predicate<Instance> isInvalid =
                event -> !(tles.contains(event) || unreferencedByBBE.contains(event));
        return unreferencedByPathway.stream()
                .filter(isInvalid)
                .collect(Collectors.toList());
    }

    @Override
    public void testCheck() {
        // There is one invalid fixture instance.
        testCheck(1);
    }

    @Override
    protected List<Instance> createTestFixture() {
        List<Instance> fixture = new ArrayList<Instance>();

        // An invalid empty event.
        Instance event = createInstance(ReactomeJavaConstants.Reaction);
        fixture.add(event);
        
        // A valid event referenced by only a pathway.
        event = createInstance(ReactomeJavaConstants.Reaction);
        fixture.add(event);
        Instance pathway = createInstance(ReactomeJavaConstants.Pathway);
        setAttributeValue(pathway, ReactomeJavaConstants.hasEvent, event);
        fixture.add(pathway);
        
        // A valid event referenced by only a BBE.
        event = createInstance(ReactomeJavaConstants.Reaction);
        fixture.add(event);
        Instance bbe = createInstance(ReactomeJavaConstants.BlackBoxEvent);
        setAttributeValue(bbe, ReactomeJavaConstants.hasEvent, event);
        fixture.add(bbe);
        
        // A valid event referenced by both a pathway and a BBE.
        event = createInstance(ReactomeJavaConstants.Reaction);
        fixture.add(event);
        pathway = createInstance(ReactomeJavaConstants.Pathway);
        setAttributeValue(pathway, ReactomeJavaConstants.hasEvent, event);
        fixture.add(pathway);
        bbe = createInstance(ReactomeJavaConstants.BlackBoxEvent);
        setAttributeValue(bbe, ReactomeJavaConstants.hasEvent, event);
        fixture.add(bbe);
        
        return fixture;
    }

}
