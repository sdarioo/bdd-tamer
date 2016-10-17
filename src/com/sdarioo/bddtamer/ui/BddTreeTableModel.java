package com.sdarioo.bddtamer.ui;


import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

public class BddTreeTableModel  {


    public static Node getDefaultRoot() {
        Node root = new Node("JTree");
        Node parent;

        parent = new Node("colors");
        root.add(parent);
        parent.add(new Node("blue"));
        parent.add(new Node("violet"));
        parent.add(new Node("red"));
        parent.add(new Node("yellow"));

        parent = new Node("sports");
        root.add(parent);
        parent.add(new Node("basketball"));
        parent.add(new Node("soccer"));
        parent.add(new Node("football"));
        parent.add(new Node("hockey"));

        parent = new Node("food");
        root.add(parent);
        parent.add(new Node("hot dogs"));
        parent.add(new Node("pizza"));
        parent.add(new Node("ravioli"));
        parent.add(new Node("bananas"));
        return root;
    }

    private static class Node extends DefaultMutableTreeTableNode {
        public Node(String text) {
            super(text);
        }

        @Override
        public boolean isEditable(int column) {
            return false;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int column) {
            if (column == 0) {
                return getUserObject();
            }
            return "column-2";
        }
    }
}
