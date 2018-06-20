package org.gk.qualityCheck.graph;

import java.util.ArrayList;
import java.util.List;

import org.gk.model.Instance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.qualityCheck.MissingValueCheck;

public class T019_PublicationsWithoutAuthor extends MissingValueCheck {

    public T019_PublicationsWithoutAuthor() {
        super(ReactomeJavaConstants.Publication, ReactomeJavaConstants.author);
    }

    @Override
    protected List<Instance> createTestFixture() {
        List<Instance> fixture = new ArrayList<Instance>();
        Instance book = createInstance(ReactomeJavaConstants.Book);
        fixture.add(book);
        Instance url = createInstance(ReactomeJavaConstants.URL);
        fixture.add(url);
        
        return fixture;
    }

}
