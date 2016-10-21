package com.sdarioo.bddtamer.ui.search;


import com.sdarioo.bddtamer.ui.tree.BddTreeWalker;
import com.sdarioo.bddtamer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;


public class SearchHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchHelper.class);

    private static final boolean IGNORE_CASE = true;

    /**
     * Finds, selects and expands next matching node
     * @param tree
     * @param text
     */
    public static void findNext(TreeTable tree, String text) {
        if (text.length() <= 0) {
            return;
        }
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
            DefaultTreeTableNode next = allNodes.get(idx);
            if (isMatching(next, text)) {
                TreePath path = TreeUtil.pathToRoot(next);
                tree.addSelectionPath(path);
                tree.expandPath(path);
                tree.scrollPathToVisible(path);
                break;
            }
        }
    }

    /**
     * Finds, selects and expands all matching nodes.
     * @param tree
     * @param text
     */
    public static void findAll(TreeTable tree, String text) {
        if (text.length() <= 0) {
            return;
        }
        TreeModel model = tree.getTreeModel();
        List<DefaultTreeTableNode> allNodes = new ArrayList<>();
        BddTreeWalker.walkTree(model, (path, node) -> allNodes.add(node));

        tree.clearSelection();
        for (int i = 0; i < allNodes.size(); i++) {
            DefaultTreeTableNode next = allNodes.get(i);
            if (isMatching(next, text)) {
                TreePath path = TreeUtil.pathToRoot(next);
                tree.addSelectionPath(path);
                tree.expandPath(path);
                if (i == 0) {
                    tree.scrollPathToVisible(path);
                }
            }
        }
    }

    private static boolean isMatching(DefaultTreeTableNode node, String text) {
        for (int i = 0; i < node.getColumnCount(); i++) {
            String columnText = node.getValueAt(i).toString();
            if (MATCHER.test(columnText, text)) {
                return true;
            }
        }
        return false;
    }

    private static final BiPredicate<String, String> MATCHER = (col, text) -> IGNORE_CASE ?
            col.toLowerCase().contains(text.toLowerCase()) :
            col.contains(text);

}
