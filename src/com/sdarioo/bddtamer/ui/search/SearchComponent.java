package com.sdarioo.bddtamer.ui.search;


import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
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
import java.awt.*;
import java.awt.event.*;


public class SearchComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchComponent.class);
    private static final String CARD_FIND = "find";
    private static final String CARD_GENERAL = "general";

    private final TreeTable tree;
    private final JComponent parent;

    private JComponent mainComponent;
    private MySearchTextField searchField;

    public SearchComponent(JComponent parent, TreeTable tree) {
        this.parent = parent;
        this.tree = tree;
        initializeUI();
    }

    public JComponent getComponent() {
        return mainComponent;
    }

    private void initializeUI() {

        searchField = new MySearchTextField();
        searchField.setPreferredSize(new Dimension(300, searchField.getPreferredSize().height));

        JLabel closeLabel = new JLabel(AllIcons.Actions.Cross);
        closeLabel.setToolTipText("Close");
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeSearchComponent();
            }
        });

        JLabel findAll = new JLabel(AllIcons.Actions.Find);
        findAll.setToolTipText("Find All");
        findAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SearchHelper.findAll(tree, searchField.getText());
            }
        });

        JPanel leftPanel = new NonOpaquePanel(new BorderLayout());
        leftPanel.add(findAll, BorderLayout.EAST);
        leftPanel.add(searchField, BorderLayout.CENTER);

        JPanel searchPanel = new NonOpaquePanel(new BorderLayout());
        searchPanel.add(leftPanel, BorderLayout.WEST);
        searchPanel.add(closeLabel, BorderLayout.EAST);

        JPanel generalPanel = new JPanel();
        generalPanel.setPreferredSize(new Dimension(0, 0));

        mainComponent = new JPanel(new CardLayout());
        mainComponent.add(generalPanel, CARD_GENERAL);
        mainComponent.add(searchPanel, CARD_FIND);
        showSearchComponent();
    }

    private void closeSearchComponent() {
        ((CardLayout)mainComponent.getLayout()).show(mainComponent, CARD_GENERAL);
        mainComponent.setPreferredSize(new Dimension(0, 0));
        parent.doLayout();
        tree.grabFocus();
        tree.requestFocus();
    }

    private void showSearchComponent() {
        ((CardLayout)mainComponent.getLayout()).show(mainComponent, CARD_FIND);
        mainComponent.setPreferredSize(new Dimension(-1, searchField.getPreferredSize().height));
        parent.doLayout();
        searchField.requestFocusInWindow();
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
        }

        @Override
        protected void onFocusGained() {
            super.onFocusGained();
        }

        protected void onTextEntered(String text) {
            SearchHelper.findNext(tree, text);
        }

        private void addListeners() {

            addDocumentListener(new DocumentAdapter() {
                @Override
                public void textChanged(DocumentEvent e) {
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
                    showSearchComponent();
                }
            }.registerCustomShortcutSet(CommonShortcuts.getFind(), tree);
        }

    }
}
