package com.sdarioo.bddtamer.ui;


import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

public class BddTreeTableModel extends AbstractTreeTableModel implements TreeTableModel {


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

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int i) {
        return i == 0 ? "Name" : "Status";
    }

    @Override
    public Class getColumnClass(int i) {
        return String.class;
    }

    @Override
    public Object getValueAt(Object o, int i) {
        return o.toString();
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    @Override
    public void setValueAt(Object o, Object o1, int i) {

    }

    @Override
    public void setTree(JTree jTree) {

    }

    @Override
    public Object getRoot() {
        return getDefaultRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((Node)parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((Node)parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((Node)node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((Node)parent).getIndex((TreeNode)child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }

    private static class Node extends DefaultMutableTreeNode {
        public Node(String text) {
            super(text);
        }
    }
}
