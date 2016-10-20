package com.sdarioo.bddtamer.ui.util;

import de.sciss.treetable.j.DefaultTreeTableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;

public class TreeUtil {

    private TreeUtil() {}

    /**
     * Creates tree path from root to given node.
     * @param node
     * @return
     */
    public static TreePath pathToRoot(TreeNode node) {
        List<Object> path = new LinkedList<>();
        while (node != null) {
            path.add(0, node);
            node = (node.getParent() != null) ? node.getParent() : null;
        }
        return new TreePath(path.toArray(new Object[0]));
    }

    /**
     * Return user object from last component of given tree path
     * @param path
     * @return
     */
    public static Object getUserObject(TreePath path) {
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            return ((DefaultMutableTreeNode) node).getUserObject();
        }
        return null;
    }

    public static DefaultTreeTableNode findNode(DefaultTreeTableNode root, Object modelObject) {
        if (modelObject.equals(root.getUserObject())) {
            return root;
        }
        DefaultTreeTableNode result = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            result = findNode((DefaultTreeTableNode)root.getChildAt(i), modelObject);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
