package com.sdarioo.bddtamer.ui.util;

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
}
