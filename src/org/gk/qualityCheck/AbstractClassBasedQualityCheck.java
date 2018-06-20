package org.gk.qualityCheck;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import org.gk.database.InstanceListPane;
import org.gk.model.GKInstance;
import org.gk.model.Instance;
import org.gk.pathwaylayout.Utils;
import org.gk.persistence.MySQLAdaptor;
import org.gk.qualityCheck.ClassBasedQualityCheck;
import org.gk.qualityCheck.QAReport;
import org.gk.schema.GKSchemaClass;
import org.gk.schema.InvalidAttributeException;
import org.gk.schema.SchemaAttribute;
import org.gk.schema.SchemaClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.org.gk.qualityCheck.TestUtil;

/**
 * Class-based QA check that provides common functions for testing
 * and results display.
 * 
 * @author Fred Loney <loneyf@ohsu.edu>
 */
abstract public class AbstractClassBasedQualityCheck extends ClassBasedQualityCheck {

    private String clsName;

    // Testing cruft.
    protected List<Instance> fixture;
    private int preTestFailureCnt;
    
    protected AbstractClassBasedQualityCheck(String clsName) {
        this.clsName = clsName;
    }

    public String getSchemaClassName() {
        return this.clsName;
    }

    /**
     * @return the test description
     */
    abstract public String getDescription();
    
    @Before
    /**
     * Sets up testing for this QA check.
     * 
     * Testing requires a file <code>test/resources/auth.properties</code>
     * with entries <code>host</code> (default <code>localhost</code>),
     * <code>database</code>, <code>user</code> and <code>password</code>.
     * 
     * @throws FileNotFoundException if the test database authorization
     *      file is missing
     */
    public void setUp() throws Exception {
        MySQLAdaptor dba = TestUtil.getDataSource();
        setDatasource(dba);
        Collection<Instance> failures = fetchInvalid();
        preTestFailureCnt = failures.size();
        fixture = createTestFixture();
        dba.storeLocalInstances(fixture);
        dba.commit();
    }

    @Test
    /**
     * Tests this QA check.
     * 
     * @see #setUp()
     */
    public void testCheck() {
        this.testCheck(fixture.size());
    }
    
    @After
    /**
     * Deletes all test fixture instances.
     * 
     * @throws Exception if there is a database access error
     */
    public void tearDown() throws Exception {
        if (fixture == null) {
            return;
        }
        MySQLAdaptor dba = (MySQLAdaptor) getDatasource();
        for (Instance inst: fixture) {
            // Note: this is the one place where it is assumed
            // that the instance is a GKInstance.
            dba.deleteInstance((GKInstance) inst);
        }
        dba.commit();
        dba.cleanUp();
    }

    /**
     * Verifies whether the test fixture introduces
     * new {@link #validate()} check failures.
     * 
     * @param expectedCount the expected number of test fixture failures
     */
    protected void testCheck(int expectedCount) {
        Collection<Instance> failures = fetchInvalid();
        assertEquals(getDescription() + " count incorrect",
                     preTestFailureCnt + expectedCount,
                     failures.size());
    }

    @Override
    /**
     * Delegates to {@link ClassBasedQualityCheck#checkInCommand(Supplier<Collection<Instance>>)}
     * with a {@link #fetchInvalid} supplier.
     */
    public QAReport checkInCommand() throws Exception {
        return checkInCommand(new Supplier<Collection<Instance>>() {

            @Override
            public Collection<Instance> get() {
                return fetchInvalid();
            }
            
        });
    }

    @Override
    /**
     * Delegates to {@link ClassBasedQualityCheck#check(Supplier<Collection<Instance>>)}
     * with a {@link #fetchInvalid} supplier.
     */
    public void check() {
        check(getDescription(), new Supplier<Collection<Instance>>() {

         @Override
         public Collection<Instance> get() {
             return fetchInvalid();
         }

      });
    }

    @Override
    public void check(GKSchemaClass cls) {
        throw new UnsupportedOperationException("Instance checks are not yet supported");
    }

    @Override
    public void check(GKInstance instance) {
        throw new UnsupportedOperationException("Instance checks are not yet supported");
    }

    @Override
    public void check(List<GKInstance> instances) {
        throw new UnsupportedOperationException("Instance checks are not yet supported");
    }

    @Override
    public void checkProject(GKInstance event) {
        throw new UnsupportedOperationException("Instance checks are not yet supported");
    }

    @Override
    protected void showErrorMessage() {
        JOptionPane.showMessageDialog(parentComp,
                "Only " + getSchemaClassName() +  " instances should be checked.",
                getSchemaClassName()  + "error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected InstanceListPane getDisplayedList() {
        throw new UnsupportedOperationException("Test display not yet supported");
    }
    
    /**
     * Collects instances which do not pass this QA test.
     * 
     * @return the invalid check failure instances.
     */
    abstract protected Collection<Instance> fetchInvalid();
   
    /**
     * Creates a fixture consisting of a new empty instance
     * of this check's schema class.
     */
    protected List<Instance> createTestFixture() {
        List<Instance> fixture = new ArrayList<Instance>();
        Instance inst = createInstance(getSchemaClassName());
        fixture.add(inst);
        return fixture;
    }

    /**
     * Fetches instances of the given class which are not referenced
     * by the given attribute.
     * 
     * @see MySQLAdaptor#fetchUnreferencedInstances(String, SchemaAttribute)
     * @param className the origin class name
     * @param attName the origin attribute name
     * @return the target instances
     * @throws ClassCastException if the data source is not a {@link MySQLAdaptor}
     * @throws AttributeAccessException if there is a query failure
     */
    protected Set<Instance> fetchUnreferenced(String className, String attName) {
        MySQLAdaptor dba = (MySQLAdaptor)getDatasource();
        SchemaClass originCls =
                dba.getSchema().getClassByName(className);
        SchemaAttribute originAtt;
        try {
            originAtt = originCls.getAttribute(attName);
        } catch (InvalidAttributeException e) {
            throw new AttributeAccessException(originCls, attName, e);
        }
        try {
            return dba.fetchUnreferencedInstances(getSchemaClassName(), originAtt);
        } catch (Exception e) {
            SchemaClass cls =
                    dba.getSchema().getClassByName(getSchemaClassName());
            throw new AttributeAccessException(cls, attName, e);
        }
    }

    /**
     * Fetches instances of the given class filtered by the given
     * subquery SQL.
     * 
     * @see MySQLAdaptor#fetchInstancesIn(String, String)
     * @param className the origin class name
     * @param subquery the SQL condition subquery
     * @return the target instances
     * @throws ClassCastException if the data source is not a {@link MySQLAdaptor}
     * @throws AttributeAccessException if there is a query failure
     */
    protected Set<Instance> fetchIn(String subquery) {
        MySQLAdaptor dba = (MySQLAdaptor)getDatasource();
        try {
            return dba.fetchInstancesIn(getSchemaClassName(), subquery);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    protected Set<GKInstance> getTopLevelPathways() {
        MySQLAdaptor dba = (MySQLAdaptor) getDatasource();
        // Make a set, consistent with other instance fetch methods.
        Set<GKInstance> tles;
        try {
            tles = new HashSet<GKInstance>(Utils.getTopLevelPathways(dba));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        return tles;
    }

}
