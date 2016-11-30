package com.sdarioo.bddviewer.ui.tree.search;


import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import de.sciss.treetable.j.TreeTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;


public class SearchComponent {

    private static final Logger LOGGER = Logger.getInstance(SearchComponent.class);
    private static final String CARD_FIND = "find";
    private static final String CARD_GENERAL = "general";

    private final TreeTable tree;
    private final JComponent parent;

    private JComponent mainComponent;
    private MySearchTextField searchField;
    private JCheckBox matchCaseBox;

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
        searchField.setPreferredSize(new Dimension(400, searchField.getPreferredSize().height));

        JLabel closeLabel = new JLabel(AllIcons.Actions.Cross);
        closeLabel.setToolTipText("Close");
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeSearchComponent();
            }
        });

        JLabel findPrev = new JLabel(AllIcons.Actions.PreviousOccurence);
        findPrev.setToolTipText("Find Previous");
        findPrev.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SearchHelper helper = new SearchHelper(searchField.getText(), matchCaseBox.isSelected());
                helper.findPrev(tree);
            }
        });

        JLabel findNext = new JLabel(AllIcons.Actions.NextOccurence);
        findNext.setToolTipText("Find Next");
        findNext.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SearchHelper helper = new SearchHelper(searchField.getText(), matchCaseBox.isSelected());
                helper.findNext(tree);
            }
        });

        JLabel findAll = new JLabel(AllIcons.Actions.Find);
        findAll.setToolTipText("Find All");
        findAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SearchHelper helper = new SearchHelper(searchField.getText(), matchCaseBox.isSelected());
                helper.findAll(tree);
            }
        });


        JPanel leftPanel = new NonOpaquePanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        leftPanel.add(searchField);
        leftPanel.add(findPrev);
        leftPanel.add(findNext);
        leftPanel.add(findAll);
        leftPanel.add(new JSeparator());
        matchCaseBox = new JCheckBox("Match Case");
        leftPanel.add(matchCaseBox);

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
            SearchHelper helper = new SearchHelper(text, matchCaseBox.isSelected());
            helper.findNext(tree);
        }

        private void addListeners() {

            addDocumentListener(new DocumentAdapter() {
                @Override
                public void textChanged(DocumentEvent e) {
                }
            });

            getTextEditor().registerKeyboardAction(e -> onTextEntered(getText()),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            getTextEditor().registerKeyboardAction(e -> closeSearchComponent(),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            new AnAction() {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    showSearchComponent();
                }
            }.registerCustomShortcutSet(CommonShortcuts.getFind(), tree);
        }

    }
}
