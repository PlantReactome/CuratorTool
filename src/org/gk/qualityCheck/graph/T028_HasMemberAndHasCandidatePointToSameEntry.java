package org.gk.qualityCheck.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.gk.model.Instance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.AbstractClassBasedQualityCheck;

public class T028_HasMemberAndHasCandidatePointToSameEntry extends AbstractClassBasedQualityCheck {

    private static String DESCRIPTION =
            "Candidate sets with at least one physical entity which is both a candidate and a member";
    
    private static String SUBQUERY =
            "SELECT DISTINCT m.DB_ID " +
            "FROM EntitySet_2_hasMember m, CandidateSet_2_hasCandidate c " +
            "WHERE m.hasMember = c.hasCandidate AND m.DB_ID = c.DB_ID";

    public T028_HasMemberAndHasCandidatePointToSameEntry() {
        super(ReactomeJavaConstants.CandidateSet);
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void testCheck() {
        // There are two invalid CandidateSets in the test fixture.
        testCheck(2);
    }

    @Override
    protected Collection<Instance> fetchInvalid() {
        return fetchIn(SUBQUERY);
    }

    @Override
    protected List<Instance> createTestFixture() {
        // The default test fixture has one valid empty CandidateSet.
        List<Instance> fixture = super.createTestFixture();
        
        // A valid candidate with different candidates and members.
        Instance candidate = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(candidate);
        Instance member = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(member);
        Instance candidateSet = createInstance(ReactomeJavaConstants.CandidateSet);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasCandidate, candidate);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasMember, member);
        fixture.add(candidateSet);
        
        // An invalid candidate with a candidate which is also a member.
        candidate = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(candidate);
        candidateSet = createInstance(ReactomeJavaConstants.CandidateSet);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasCandidate, candidate);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasMember, candidate);
        fixture.add(candidateSet);
        
        // An invalid candidate with two candidates which are also members.
        Instance candidate1 = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(candidate1);
        Instance candidate2 = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(candidate2);
        Instance candidate3 = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(candidate3);
        member = createInstance(ReactomeJavaConstants.SimpleEntity);
        fixture.add(member);
        List<Instance> candidates = Arrays.asList(candidate1, candidate2, candidate3);
        List<Instance> members = Arrays.asList(candidate1, candidate2, member);
        candidateSet = createInstance(ReactomeJavaConstants.CandidateSet);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasCandidate, candidates);
        setAttributeValue(candidateSet, ReactomeJavaConstants.hasMember, members);
        fixture.add(candidateSet);
        
        return fixture;
    }

}
