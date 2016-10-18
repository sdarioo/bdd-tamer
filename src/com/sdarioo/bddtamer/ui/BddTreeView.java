package com.sdarioo.bddtamer.ui;


import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.sdarioo.bddtamer.StoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class BddTreeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeView.class);

    private final Project project;
    private final StoryProvider storyProvider;

    private JPanel rootComponent;

    public BddTreeView(Project project, StoryProvider storyProvider) {
        this.project = project;
        this.storyProvider = storyProvider;

        initializeUI();
    }

    private void initializeUI() {

        rootComponent = new JPanel(new BorderLayout());

        BddTree tree = new BddTree(project, storyProvider);
        JComponent searchField = createSearchField(rootComponent);
        JBScrollPane treePane = new JBScrollPane(tree.getTreeTable());

        rootComponent.add(searchField, BorderLayout.NORTH);
        rootComponent.add(treePane, BorderLayout.CENTER);
    }

    public JComponent getComponent() {
        return rootComponent;
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
}
