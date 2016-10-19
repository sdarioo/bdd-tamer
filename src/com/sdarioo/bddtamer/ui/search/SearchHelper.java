package com.sdarioo.bddtamer.ui.search;



import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchHelper.class);

    public static void findNext(TreeTable tree, String text) {
        if (text.length() <= 0) {
            return;
        }
        TreeModel model = tree.getTreeModel();
        List<DefaultTreeTableNode> allNodes = new ArrayList<>();
        visit(model, model.getRoot(), allNodes);

        DefaultTreeTableNode current = toNode(model.getRoot());
        int row = tree.getSelectedRow();
        if (row >= 0) {
            current = toNode(tree.getPathForRow(row).getLastPathComponent());
        }
        int currentIndex = allNodes.indexOf(current);
        if (currentIndex < 0) {
            LOGGER.error("Cannot find current row!");
            return;
        }
        for (int i = currentIndex + 1; i < allNodes.size(); i++) {
            if (isMatching(allNodes.get(i), text)) {
                TreePath path = getPath(allNodes.get(i));
                tree.addSelectionPath(path);
                tree.expandPath(path);
                break;
            }
        }
    }

    public static void findAll(TreeTable tree, String text) {
        if (text.length() <= 0) {
            return;
        }
        TreeModel model = tree.getTreeModel();
        List<DefaultTreeTableNode> allNodes = new ArrayList<>();
        visit(model, model.getRoot(), allNodes);

        for (int i = 0; i < allNodes.size(); i++) {
            if (isMatching(allNodes.get(i), text)) {
                TreePath path = getPath(allNodes.get(i));
                tree.addSelectionPath(path);
                tree.expandPath(path);
            }
        }
    }

    private static void visit(TreeModel model, Object node, List<DefaultTreeTableNode> result) {
        result.add(toNode(node));
        int childCount = model.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            Object child = model.getChild(node, i);
            visit(model, child, result);
        }
    }

    private static boolean isMatching(Object nodeObj, String text) {
        DefaultTreeTableNode node = toNode(nodeObj);
        if (node != null) {
            for (int i = 0; i < node.getColumnCount(); i++) {
                String columnText = node.getValueAt(i).toString();
                if (columnText.contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static DefaultTreeTableNode toNode(Object nodeObj) {
        if (!(nodeObj instanceof DefaultTreeTableNode)) {
            LOGGER.error("Expected DefaultTreeTableNode. Got: " + nodeObj);
            return null;
        }
        return (DefaultTreeTableNode)nodeObj;
    }

    private static TreePath getPath(DefaultTreeTableNode node) {
        List<Object> path = new LinkedList<>();
        while (node != null) {
            path.add(0, node);
            node = (node.getParent() != null) ? toNode(node.getParent()) : null;
        }
        return new TreePath(path.toArray(new Object[0]));
    }
}
