/*
 * Created on Jan 30, 2016
 *
 */
package org.gk.scripts;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gk.graphEditor.PathwayEditor;
import org.gk.model.GKInstance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.pathwaylayout.PathwayDiagramGeneratorViaAT;
import org.gk.persistence.DiagramGKBReader;
import org.gk.persistence.DiagramGKBWriter;
import org.gk.persistence.MySQLAdaptor;
import org.gk.render.Node;
import org.gk.render.NodeAttachment;
import org.gk.render.Renderable;
import org.gk.render.RenderablePathway;

/**
 * This class is used to auto layout modifications displayed in nodes in pathway diagrams
 * against a database.
 * @author gwu
 *
 */
public class ModificationDisplayUpdate {
    
    /**
     * Default constructor.
     */
    public ModificationDisplayUpdate() {
    }
    
    public void performLayout(MySQLAdaptor dba) throws Exception {
        long time1 = System.currentTimeMillis();
        // Make sure if these static variable values are used
        Node.setWidthRatioOfBoundsToText(1.0d);
        Node.setHeightRatioOfBoundsToText(1.0d);
        
        Set<GKInstance> pdsToUpdate = getPDsWithModifications(dba);
        System.out.println("Total PathwayDiagrams having modifications: " + pdsToUpdate.size());
        performLayout(pdsToUpdate, dba);
        long time2 = System.currentTimeMillis();
        System.out.println("Total time: " + (time2 - time1) / 1000.0d + " seconds");
    }
    
    private void performLayout(Set<GKInstance> pds,
                               MySQLAdaptor dba) throws Exception {
        DiagramGKBReader diagramReader = new DiagramGKBReader();
        DiagramGKBWriter diagramWriter = new DiagramGKBWriter();
        PathwayEditor editor = new PathwayEditor(); // To validate bounds
        PathwayDiagramGeneratorViaAT generator = new PathwayDiagramGeneratorViaAT();
        int count = 0;
        // Now for the update
        boolean isTransactionSupported = dba.supportsTransactions();
        try {
            if (isTransactionSupported)
                dba.startTransaction();
            Long defaultPersonId = 140537L; // For Guanming Wu at CSHL
            GKInstance ie = ScriptUtilities.createDefaultIE(dba, defaultPersonId, true);
            System.out.println("Default IE: " + ie);
            
            for (GKInstance pd : pds) {
                System.out.println(count + ": " + pd);
                RenderablePathway pathway = diagramReader.openDiagram(pd);
                editor.setRenderable(pathway);
                // Just to make the tightNodes() work, have to do an extra paint
                // to make textBounds correct
                generator.paintOnImage(editor);
                editor.tightNodes(true);
                generator.paintOnImage(editor); // Second draw
                List<Renderable> comps = pathway.getComponents();
                int total = 0;
                for (Renderable r : comps) {
                    if (r instanceof Node) {
                        Node node = (Node) r;
                        if (node.getNodeAttachments() != null)
                            total += node.getNodeAttachments().size();
                        node.layoutNodeAttachemtns();
                    }
                }
                updateDbDiagram(dba,
                                diagramWriter,
                                ie, 
                                pd,
                                pathway);
                System.out.println("Updated: " + total  + " modifications.");
                count ++;
            }
            System.out.println("Total PathwayDiagram updated: " + count);
            dba.commit();
        }
        catch(Exception e) {
            if (isTransactionSupported)
                dba.rollback();
            e.printStackTrace();
        }
    }
    
    private void updateDbDiagram(MySQLAdaptor dba,
                                 DiagramGKBWriter diagramWriter,
                                 GKInstance newIE, 
                                 GKInstance pd,
                                 RenderablePathway diagram) throws Exception {
        String xml = diagramWriter.generateXMLString(diagram);
        pd.setAttributeValue(ReactomeJavaConstants.storedATXML, xml);
        // have to get all modified attributes first. Otherwise, the original
        // values will be lost
        pd.getAttributeValue(ReactomeJavaConstants.modified);
        pd.addAttributeValue(ReactomeJavaConstants.modified, newIE);
        dba.updateInstanceAttribute(pd, ReactomeJavaConstants.storedATXML);
        dba.updateInstanceAttribute(pd, ReactomeJavaConstants.modified);
    }
    
    /**
     * Get a set of PathwayDiagram instances that show modifications.
     * @param dba
     * @return
     * @throws Exception
     */
    private Set<GKInstance> getPDsWithModifications(MySQLAdaptor dba) throws Exception {
        Collection<GKInstance> pds = dba.fetchInstancesByClass(ReactomeJavaConstants.PathwayDiagram);
        DiagramGKBReader reader = new DiagramGKBReader();
        Set<GKInstance> rtn = new HashSet<GKInstance>();
        for (GKInstance pd : pds) {
            GKInstance pathway = (GKInstance) pd.getAttributeValue(ReactomeJavaConstants.representedPathway);
            if (pathway == null)
                continue;
            GKInstance species = (GKInstance) pathway.getAttributeValue(ReactomeJavaConstants.species);
            if (!species.getDisplayName().equals("Homo sapiens")) // Update for human only
                continue;
            RenderablePathway diagram = reader.openDiagram(pd);
            List<Renderable> components = diagram.getComponents();
            for (Renderable r : components) {
                if (r instanceof Node) {
                    Node node = (Node) r;
                    List<NodeAttachment> attachments = node.getNodeAttachments();
                    if (attachments != null && attachments.size() > 0) {
                        rtn.add(pd);
                        break;
                    }
                }
            }
        }
        return rtn;
    }
    
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Java -Xmx4G org.gk.scripts.ModificationDisplayUpdate dbHost dbName dbUser dbPwd");
            System.exit(1);
        }
        try {
            MySQLAdaptor dba = new MySQLAdaptor(args[0],
                                                args[1],
                                                args[2],
                                                args[3]);
            ModificationDisplayUpdate update = new ModificationDisplayUpdate();
            update.performLayout(dba);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
