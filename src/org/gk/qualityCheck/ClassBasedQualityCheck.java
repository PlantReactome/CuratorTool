/*
 * Created on Aug 17, 2009
 *
 */
package org.gk.qualityCheck;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gk.model.GKInstance;
import org.gk.model.Instance;
import org.gk.model.InstanceUtilities;
import org.gk.model.ReactomeJavaConstants;
import org.gk.persistence.MySQLAdaptor;
import org.gk.schema.InvalidAttributeException;
import org.gk.schema.Schema;
import org.gk.schema.SchemaAttribute;
import org.gk.schema.SchemaClass;
import org.gk.util.GKApplicationUtilities;

/**
 * This class is used to organize QAs that can be run based on classes, e.g., ImbalanceChecker, CompartmentChecker, etc.
 * @author wgm
 *
 */
public abstract class ClassBasedQualityCheck extends AbstractQualityCheck {
    
    protected final int SIZE_TO_LOAD_ATTS = 10;
    
    private static final Map<String, Function<Instance, Object>> DEF_COLUMNS;
    
    static {
        DEF_COLUMNS = new HashMap<>(3);
        DEF_COLUMNS.put("DB_ID", inst -> inst.getDBID());
        DEF_COLUMNS.put("DisplayName", inst -> inst.getDisplayName());
        DEF_COLUMNS.put("LastAuthor", ClassBasedQualityCheck::getLastAuthor);
    }
    
    private static String getLastAuthor(Instance inst) {
        try {
            GKInstance ie = InstanceUtilities.getLatestIEFromInstance((GKInstance) inst);
            return ie == null ? null : ie.getDisplayName();
        } catch (Exception e) {
            throw new AttributeAccessException(
                    inst,
                    ReactomeJavaConstants.modified + "|" + ReactomeJavaConstants.edited,
                    e);
        }
    }
    
    /**
     * Gets the given instance attribute value.
     * 
     * @param inst the instance to access
     * @param attName the attribute to access
     * @return the access value
     * @throws AttributeAccessException if there is a data access error
     */
    protected static Object getAttributeValue(Instance inst, String attName) {
        try {
            return inst.getAttributeValue(attName);
        } catch (Exception e) {
            throw new AttributeAccessException(inst, attName, e);
        }
    }
    
    /**
     * Sets the given instance attribute value.
     * 
     * @param inst the instance to access
     * @param attName the attribute to set
     * @param value the attribute value
     * @throws AttributeAccessException if there is a data access error
     */
    protected static void setAttributeValue(Instance inst, String attName, Object value) {
        try {
            inst.setAttributeValue(attName, value);
        } catch (Exception e) {
            throw new AttributeAccessException(inst, attName, e);
        }
    }

    /**
     * Check if a shell instance is contained in the passed instances.
     * @param instances
     * @return
     */
    protected boolean containShellInstances(Collection instances) {
        GKInstance instance = null;
        for (Iterator it = instances.iterator(); it.hasNext();) {
            instance = (GKInstance) it.next();
            if (instance.isShell()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * A way to dispaly error message regarding the checking.
     */
    protected abstract void showErrorMessage();
    
    protected void loadAttributes(String clsName,
                                  String attName,
                                  MySQLAdaptor dba) throws Exception {
        Collection instances = dba.fetchInstancesByClass(clsName);
        loadAttributes(instances, clsName, attName, dba);
    }
    
    protected void loadAttributes(Collection instances,
                                  String clsName,
                                  String attName,
                                  MySQLAdaptor dba) throws Exception {
        SchemaAttribute att = dba.getSchema().getClassByName(clsName).getAttribute(attName);
        dba.loadInstanceAttributeValues(instances, att);
    }
    
    protected ListSelectionListener generateListSelectionListener(final ResultPane resultPane, 
                                                                  final JSplitPane jsp,
                                                                  final JButton checkOutBtn, 
                                                                  final String titlePrefix) {
        ListSelectionListener l = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                List selected = getDisplayedList().getSelection();
                if (selected.size() > 0)
                    checkOutBtn.setEnabled(true);
                else
                    checkOutBtn.setEnabled(false);
                if (selected.size() != 1)
                    return;
                if (!resultPane.isVisible()) {
                    resultPane.setVisible(true);
                    jsp.setDividerLocation((int)(jsp.getHeight() * 0.75));
                }
                GKInstance reaction = (GKInstance) selected.get(0);
                resultPane.setInstance(reaction);
                resultPane.setText(titlePrefix + " for \"" + reaction.getDisplayName() + " [" + reaction.getDBID() + "]\"");
            }
        };
        return l;
    }
    
    @SuppressWarnings("unchecked")
    /**
     * Returns the instances for which the given attribute is null.
     * This QA check's data source must be set to an adapter which
     * supports the <code>IS NULL</code> operator.
     * 
     * @param clsName the search class simple name
     * @param attName the search attribute name
     * @return the instances which satisfy the criterion
     * @throws NullPointerException if the data source is not set
     * @throws AttributeAccessException if there is a data access error
     */
    protected Collection<Instance> fetchInstancesMissingAttribute(
            String clsName, String attName) {
        try {
            return getDatasource().fetchInstanceByAttribute(
                    clsName, attName, "IS NULL", null);
        } catch (Exception e) {
            String message = "Error accessing " + clsName + "," + attName;
            throw new AttributeAccessException(message, e);
        }
    }
    
    /**
     * Returns the instances for which any of the given attributes
     * are null.
     * 
     * @see #fetchInstancesMissingAttribute(String, String) fetchInstancesMissingAttribute
     */
    protected Collection<Instance> fetchInstancesMissingAnyAttributes(
            String clsName, String ...attNames) {
        // Collect the instances into a set rather than a list
        // in order to avoid duplicate reporting.
        Set<Instance> missing = new HashSet<Instance>();
        for (String attName: attNames) {
            Collection<Instance> instances = fetchInstancesMissingAttribute(
                    clsName, attName);
            for (Instance inst: instances) {
                missing.add(inst);
            }
        }
        return missing;
    }
    
    /**
     * Returns the instances for which all of the given attributes
     * are null.
     * 
     * @see #fetchInstancesMissingAttribute(String, String) fetchInstancesMissingAttribute
     */
    protected Collection<Instance> fetchInstancesMissingAllAttributes(
            String clsName, String ...attNames) {
        // Map the id to the instance. Use a map rather than a list
        // in order to avoid duplicate reporting.
        List<Instance> missing = new ArrayList<Instance>();
        // Split the attributes into the first and the others.
        // The database query is performed on the first.
        // Each fetched instance is then examined for the remaining
        // attributes.
        String first = attNames[0];
        String[] rest = Arrays.copyOfRange(attNames, 1, attNames.length);
        Collection<Instance> instances = fetchInstancesMissingAttribute(
                    clsName, first);
        for (Instance inst: instances) {
            if (isMissingValues(inst, rest)) {
                missing.add(inst);
            }
        }
        return missing;
    }

    /**
     * Creates an instance of the given schema class.
     * The instance {@link GKInstance#getDbAdaptor() persistence adaptor}
     * is set to this quality check's {@link #getDatasource() data source}.
     * The instance has no attributes besides the schema class and is not
     * yet saved.
     * 
     * @param clsName the {@link SchemaClass} name
     * @return the new instance
     */
    protected Instance createInstance(String clsName) {
        Schema schema = getDatasource().getSchema();
        GKInstance instance = new GKInstance(schema.getClassByName(clsName));
        instance.setDbAdaptor(getDatasource());
        return instance;
    }

    /**
     * Makes a {@link QAReport} listing the given instances.
     * 
     * @param instances the instances with a QA issue
     * @return the new report
     * @throws Exception
     * @throws InvalidAttributeException
     */
    protected QAReport createReport(Collection<Instance> instances) {
        QAReport report = new QAReport();
        fillReport(report, instances);
        return report;
    }

    /**
     * Adds one line for each invalid instance to the report.
     * The line consists of the DB id, display name, last author
     * and the given attribute values.
     * 
     * @param report the report to fill
     * @param invalid the instances to format as report lines
     * @param attNames additional attributes to add to the report
     */
    protected void fillReport(QAReport report, Collection<Instance> invalid,
            String ...attNames) {
        // The sorted default column headers as a stream.
        Stream<String> defColHdrs = DEF_COLUMNS.keySet().stream().sorted();
        // All column names.
        List<String> colHdrs =
                Stream.concat(defColHdrs, Stream.of(attNames))
                      .collect(Collectors.toCollection(ArrayList::new));
        report.setColumnHeaders(colHdrs);
        // The {attribute: value} column value accessors.
        Map<String, Function<Instance, Object>> accessors =
                new HashMap<String, Function<Instance, Object>>(DEF_COLUMNS);
        for (String attName: attNames) {
            accessors.put(attName, inst -> getAttributeValue(inst, attName));
        }
        // Add a line to the report for each instance.
        for (Instance inst : invalid) {
            List<String> values = colHdrs.stream()
                    .map(col -> accessors.get(col).apply(inst))
                    // Print a null value as a blank rather than "null".
                    .map(value -> value == null ? "" : value.toString())
                    .collect(Collectors.toList());
            report.addLine(values);
        }
    }

    /**
     * Formats the QA check result.
     * If there are no reported instances, then this method returns
     * a message to that effect. Otherwise, the instance count is
     * printed along with a {@link QAReport}.
     * 
     * @param instances the instances which fail this QA check
     * @param description the check description
     * @return the formatted result
     */
    protected String formatResult(Collection<Instance> instances, String description) {
        if (instances.isEmpty()) {
            return "There were no " + description + ".";
        }
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        try {
            QAReport report = createReport(instances);
            printWriter.println("There were " + instances.size() +
                    description + ":");
            report.output(printWriter);
            return writer.toString();
        } catch (Exception e) {
            printWriter.println(getClass().getSimpleName() + " check error: ");
            printWriter.println(e.getMessage());
            e.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    protected void check(String description, Supplier<Collection<Instance>> supplier) {
        validateDataSource();
        if (!checkIsNeedEscape())
            return;
        // Use a new thread so that the progress can be monitored
        Thread t = new Thread() {
            public void run() {
                initProgressPane(description);
                progressPane.setText("Checking for " + description + "...");
                progressPane.setIndeterminate(true);
                String content;
                try {
                    Collection<Instance> missing = supplier.get();
                    content = formatResult(missing, description);
                } catch (Exception e) {
                    StringWriter writer = new StringWriter();
                    PrintWriter pw = new PrintWriter(writer);
                    pw.println(getClass().getSimpleName() + " check error: ");
                    pw.println(e.getMessage());
                    e.printStackTrace(pw);
                    content = writer.toString();
                } finally {
                    hideProgressPane();
                }
                if (progressPane.isCancelled())
                    return;
    
                JFrame frame = new JFrame(description + " Results");
                JTextArea text = new JTextArea(content);
                frame.add(text);
                showResultFrame(frame);
            }
        };
        t.start();
    }

    protected QAReport checkInCommand(Supplier<Collection<Instance>> supplier) throws Exception {
        QAReport report = super.checkInCommand();
        if (report == null)
            return report;
        Collection<Instance> invalid = supplier.get();
        fillReport(report, invalid);
        return report;
    }

    /**
     * Determines whether all of the given instance attributes are null-valued.
     * 
     * @param inst the instance to check
     * @param attNames the attributes to check
     * @return whether all attribute values are null
     */
    private static boolean isMissingValues(Instance inst, String[] attNames) {
        for (String attName: attNames) {
            if (getAttributeValue(inst, attName) != null) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isNotInferred(Instance rle) {
        return getAttributeValue(rle, ReactomeJavaConstants.inferredFrom) == null;
    }

    protected class ResultPane extends JPanel {
        
        private JTable resultTable;
        private JLabel resultLabel;
        
        public ResultPane() {
            init();
        }
        
        private void init() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());
            resultLabel = GKApplicationUtilities.createTitleLabel("Imbalance");
            resultTable = new JTable();
            add(new JScrollPane(resultTable), BorderLayout.CENTER);
            add(resultLabel, BorderLayout.NORTH);
        }
        
        public void setTableModel(ResultTableModel model) {
            resultTable.setModel(model);
        }
        
        public void setInstance(GKInstance instance) {
            ResultTableModel model = (ResultTableModel) resultTable.getModel();
            model.setInstance(instance);
        }
        
        public void setText(String text) {
            resultLabel.setText(text);
        }
    }

}
