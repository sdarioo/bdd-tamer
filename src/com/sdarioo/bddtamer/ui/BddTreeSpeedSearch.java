package com.sdarioo.bddtamer.ui;

import com.intellij.ui.SpeedSearchBase;
import de.sciss.treetable.j.TreeTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class BddTreeSpeedSearch extends SpeedSearchBase<TreeTable> {

    public BddTreeSpeedSearch(TreeTable component) {
        super(component);
    }

    @Override
    protected int getSelectedIndex() {
        int[] selectionRows = myComponent.getSelectionRows();
        return ((selectionRows != null) && (selectionRows.length != 0)) ? selectionRows[0] : -1;
    }

    @Override
    protected Object[] getAllElements() {
        TreePath[] paths = new TreePath[myComponent.getRowCount()];
        for(int i = 0; i < paths.length; ++i) {
            paths[i] = myComponent.getPathForRow(i);
        }
        return paths;
    }

    @Nullable
    @Override
    protected String getElementText(Object element) {
        TreePath path = (TreePath)element;
        return toString(path);
    }

    @Override
    protected void selectElement(Object element, String selectedText) {
        TreePath treePath = (TreePath)element;
        myComponent.clearSelection();
        myComponent.addSelectionPath(treePath);
        myComponent.scrollPathToVisible(treePath);
    }

    private static String toString(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        Object userObject = node.getUserObject();
        return userObject.toString();
    }

}
