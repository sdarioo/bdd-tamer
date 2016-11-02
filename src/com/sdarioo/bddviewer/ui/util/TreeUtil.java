package com.sdarioo.bddviewer.ui.util;

import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.BddTreeWalker;
import de.sciss.treetable.j.DefaultTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class TreeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTree.class);

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
     * Converts node object into DefaultTreeTableNode.
     * @param node
     * @return
     */
    public static DefaultTreeTableNode asNode(Object node) {
        if (!(node instanceof DefaultTreeTableNode)) {
            throw new IllegalArgumentException("Expected DefaultTreeTableNode. Got: " + node);
        }
        return (DefaultTreeTableNode)node;
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

    public static DefaultTreeTableNode findNode(TreeModel model,
                                                Predicate<? super DefaultTreeTableNode> predicate) {

        AtomicReference<DefaultTreeTableNode> result = new AtomicReference<>();
        BddTreeWalker.walkTree(model, (path, node) -> {
            if (predicate.test(node)) {
                result.set(node);
                return false;
            }
            return true;
        });
        return result.get();
    }

    public static DefaultTreeTableNode findNode(TreeModel model, Object userObject) {
        return findNode(model, node -> userObject.equals(node.getUserObject()));
    }
}
