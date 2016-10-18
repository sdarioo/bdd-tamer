package com.sdarioo.bddtamer.ui;

import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.impl.EditorHeaderComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.SearchTextFieldWithStoredHistory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.sciss.treetable.j.TreeTable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;


//http://www.programcreek.com/java-api-examples/index.php?source_dir=platform_tools_adt_idea-master/android/src/com/android/tools/idea/editors/vmtrace/TraceViewPanel.java
public class BddToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddToolWindowFactory.class);

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);


        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new DemoAction());
        actionGroup.addSeparator();

        ActionManager instance = ActionManager.getInstance();
        ActionToolbar actionToolbar = instance.createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true);
        panel.setToolbar(actionToolbar.getComponent());

        JPanel root = new JPanel(new BorderLayout());

        JComponent searchField = createSearchField(root);
        JBScrollPane treeView = new JBScrollPane(BddTree.createTree(project));

        root.add(searchField, BorderLayout.NORTH);
        root.add(treeView, BorderLayout.CENTER);

        panel.setContent(root);
    }


    private JComponent createSearchField(final JPanel root) {

        SearchTextField field = new SearchTextField(true);
        field.setOpaque(false);
        field.setEnabled(true);
        Utils.setSmallerFont(field);

        field.addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(javax.swing.event.DocumentEvent e) {
                String text = getText(e);
                // TODO
            }
            private String getText(DocumentEvent e) {
                try {
                    return e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException exc) {
                    LOGGER.error(exc.toString());
                    return "";
                }
            }
        });
        JTextField editorTextField = field.getTextEditor();
        editorTextField.setMinimumSize(new Dimension(200, -1));

        editorTextField.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //closeSearchComponent();
                root.requestFocus();

            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


        new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                field.requestFocusInWindow();
            }
        }.registerCustomShortcutSet(CommonShortcuts.getFind(), root);

        NonOpaquePanel findFieldWrapper = new NonOpaquePanel(new BorderLayout());
        findFieldWrapper.add(field);
        return findFieldWrapper;
    }


    private static class DemoAction extends AnAction {

        DemoAction() {
            super("REFRESH", "text", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {

        }
    }


}


