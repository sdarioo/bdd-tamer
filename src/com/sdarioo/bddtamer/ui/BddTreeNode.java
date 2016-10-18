package com.sdarioo.bddtamer.ui;

import de.sciss.treetable.j.DefaultTreeTableNode;

public class BddTreeNode extends DefaultTreeTableNode {

    private final Object modelObject;

    public BddTreeNode(Object modelObject, String[] rowData) {
        super(rowData);
        this.modelObject = modelObject;
    }

    public Object getModelObject() {
        return modelObject;
    }
}
