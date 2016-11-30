package com.sdarioo.bddviewer.ui.tree.search;


import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.ui.tree.BddTreeWalker;
import com.sdarioo.bddviewer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.TreeTable;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class SearchHelper {

    private static final Logger LOGGER = Logger.getInstance(SearchHelper.class);

    private final Predicate<DefaultTreeTableNode> matcher;

    public SearchHelper(String text, boolean matchCase) {
        this(node -> containsText(node, text, matchCase));
    }

    public SearchHelper(Predicate<DefaultTreeTableNode> matcher) {
        this.matcher = matcher;
    }

    /**
     * Finds, selects and expands next matching node
     * @param tree
     */
    public void findNext(TreeTable tree) {
        find(tree, Direction.Forward);
    }

    /**
     * Finds, selects and expands previous matching node
     * @param tree
     */
    public void findPrev(TreeTable tree) {
        find(tree, Direction.Backward);
    }

    private void find(TreeTable tree, Direction direction) {

        TreeModel model = tree.getTreeModel();
        List<DefaultTreeTableNode> allNodes = new ArrayList<>();
        BddTreeWalker.walkTree(model, (path, node) -> allNodes.add(node));

        DefaultTreeTableNode current = TreeUtil.asNode(model.getRoot());
        int row = tree.getSelectedRow();
        if (row >= 0) {
            current = TreeUtil.asNode(tree.getPathForRow(row).getLastPathComponent());
        }
        int currentIndex = allNodes.indexOf(current);
        if (currentIndex < 0) {
            LOGGER.error("Cannot find current row!");
            return;
        }
        tree.clearSelection();
        for (int i = 0; i < allNodes.size(); i++) {
            int idx = (currentIndex + i + 1) % allNodes.size();
            if (direction == Direction.Backward) {
                idx = currentIndex - (i + 1);
                if (idx < 0) {
                    idx = allNodes.size() + idx;
                }
            }
            DefaultTreeTableNode next = allNodes.get(idx);
            if (matcher.test(next)) {
                TreePath path = TreeUtil.pathToRoot(next);
                tree.addSelectionPath(path);
                tree.scrollPathToVisible(path);
                break;
            }
        }
    }

    /**
     * Finds, selects and expands all matching nodes.
     * @param tree
     */
    public void findAll(TreeTable tree) {
        TreeModel model = tree.getTreeModel();
        List<DefaultTreeTableNode> allNodes = new ArrayList<>();
        BddTreeWalker.walkTree(model, (path, node) -> allNodes.add(node));

        tree.clearSelection();
        for (int i = 0; i < allNodes.size(); i++) {
            DefaultTreeTableNode next = allNodes.get(i);
            if (matcher.test(next)) {
                TreePath path = TreeUtil.pathToRoot(next);
                tree.addSelectionPath(path);
                tree.expandPath(path);
                if (i == 0) {
                    tree.scrollPathToVisible(path);
                }
            }
        }
    }

    private static boolean containsText(DefaultTreeTableNode node, String text, boolean matchCase) {
        if (text.length() == 0) {
            return false;
        }
        for (int i = 0; i < node.getColumnCount(); i++) {
            String columnText = node.getValueAt(i).toString();
            boolean contains = matchCase ?
                    columnText.contains(text) :
                    columnText.toLowerCase().contains(text.toLowerCase());
            if (contains) {
                return true;
            }
        }
        return false;
    }

    public enum Direction {
        Forward,
        Backward
    }


}
