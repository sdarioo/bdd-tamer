package com.sdarioo.bddviewer.ui.tree;

import com.sdarioo.bddviewer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class BddTreeWalker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeWalker.class);

    private BddTreeWalker() {}

    public static void walkTree(TreeModel model, Visitor visitor) {
        Object root = model.getRoot();
        visit(model, root, visitor);
    }

    private static boolean visit(TreeModel model, Object nodeObj, Visitor visitor) {

        DefaultTreeTableNode node = TreeUtil.asNode(nodeObj);
        TreePath path = TreeUtil.pathToRoot(node);
        if (!visitor.visit(path, node)) {
            return false;
        }

        int childCount = model.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            Object child = model.getChild(node, i);
            if (!visit(model, child, visitor)) {
                return false;
            }
        }
        return true;
    }

    @FunctionalInterface
    public interface Visitor {

        /**
         * @param path
         * @param node
         * @return TRUE if walking should continue, FALSE to cancel walking
         */
        boolean visit(TreePath path, DefaultTreeTableNode node);
    }
}
