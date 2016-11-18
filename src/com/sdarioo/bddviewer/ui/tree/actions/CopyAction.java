package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import de.sciss.treetable.j.TreeColumnModel;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.MessageFormat;


public class CopyAction extends ActionBase {

    private static final String TEXT = "Copy {0}";
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyAction.class);

    private final String column;
    private final TreeTable tree;

    public CopyAction(TreeTable tree, String column) {
        super(MessageFormat.format(TEXT, column), AllIcons.Actions.Copy);
        this.tree = tree;
        this.column = column;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return;
        }
        int columnIndex = getColumnIndex();
        if (columnIndex < 0) {
            return;
        }

        Object node = path.getLastPathComponent();
        Object value = tree.getTreeColumnModel().getValueAt(node, columnIndex);

        StringSelection selection = new StringSelection(value.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    private int getColumnIndex() {
        TreeColumnModel model = tree.getTreeColumnModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (column.equals(model.getColumnName(i))) {
                return i;
            }
        }
        LOGGER.warn("Column not found: " + column);
        return -1;
    }

}
