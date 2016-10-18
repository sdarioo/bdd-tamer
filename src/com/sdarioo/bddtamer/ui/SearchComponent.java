package com.sdarioo.bddtamer.ui;


import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SearchComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchComponent.class);
    private static final int WIDTH = 350;

    private JComponent rootComponent;
    private SearchTextField searchField;

    public SearchComponent(JComponent parent, Project project) {
        initializeUI(parent, project);
    }

    public JComponent getComponent() {
        return rootComponent;
    }

    private void initializeUI(JComponent parent, Project project) {

        searchField = new SearchTextField(true);
        searchField.setOpaque(false);
        searchField.setEnabled(true);
        Utils.setSmallerFont(searchField);

        searchField.addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(DocumentEvent e) {
                String text = getText(e);
                // TODO
            }
        });
        JTextField editorTextField = searchField.getTextEditor();
        editorTextField.setMinimumSize(new Dimension(WIDTH, -1));

        editorTextField.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IdeFocusManager.getInstance(project).requestFocus(parent, true);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


        new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                searchField.requestFocusInWindow();
            }
        }.registerCustomShortcutSet(CommonShortcuts.getFind(), parent);

        rootComponent = new NonOpaquePanel(new BorderLayout());
        rootComponent.add(searchField, BorderLayout.WEST);
    }

    private static String getText(DocumentEvent e) {
        try {
            return e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException exc) {
            LOGGER.error(exc.toString());
            return "";
        }
    }

}
