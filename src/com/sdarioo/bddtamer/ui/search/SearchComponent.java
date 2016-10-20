package com.sdarioo.bddtamer.ui.search;


import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class SearchComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchComponent.class);

    private final TreeTable tree;
    private JComponent mainComponent;
    private MySearchTextField searchField;

    private boolean isFindAll = true;

    public SearchComponent(TreeTable tree) {
        this.tree = tree;
        initializeUI();
    }

    public JComponent getComponent() {
        return mainComponent;
    }

    private void initializeUI() {

        searchField = new MySearchTextField();

        JTextField editorTextField = searchField.getTextEditor();
        editorTextField.setMinimumSize(new Dimension(200, -1));
        editorTextField.setPreferredSize(new Dimension(350, editorTextField.getPreferredSize().height));

        mainComponent = new NonOpaquePanel(new BorderLayout());
        mainComponent.add(searchField, BorderLayout.CENTER);
    }

    private void closeSearchComponent() {
        tree.requestFocus();
        LOGGER.error("CLOSE");
    }

    /**
     * SearchTextField customization
     */
    private class MySearchTextField extends SearchTextField {

        MySearchTextField() {
            super(true);
            setOpaque(false);
            setEnabled(true);
            Utils.setSmallerFont(this);
            addListeners();
        }

        @Override
        protected void onFieldCleared() {
            super.onFieldCleared();
        }

        @Override
        protected void onFocusLost() {
            super.onFocusLost();
            closeSearchComponent();
        }

        @Override
        protected void onFocusGained() {
            super.onFocusGained();
        }

        protected void onTextChanged(String text) {
        }

        protected void onTextEntered(String text) {
            if (isFindAll) {
                SearchHelper.findAll(tree, text);
            } else {
                SearchHelper.findNext(tree, text);
            }
        }

        private void addListeners() {

            addDocumentListener(new DocumentAdapter() {
                @Override
                public void textChanged(DocumentEvent e) {
//                    try {
//                        String text = e.getDocument().getText(e.getOffset(), e.getLength());
//                        onTextChanged(text);
//                    } catch (BadLocationException exc) {
//                        LOGGER.error(exc.toString());
//                    }
                }
            });

            getTextEditor().registerKeyboardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onTextEntered(getText());
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            getTextEditor().registerKeyboardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closeSearchComponent();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            new AnAction() {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    requestFocusInWindow();
                }
            }.registerCustomShortcutSet(CommonShortcuts.getFind(), tree);
        }

    }
}
