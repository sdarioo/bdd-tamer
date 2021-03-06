package com.sdarioo.bddviewer.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Adapts intellij AnAction into swing Action
 */
public class ActionAdapter extends AbstractAction {

    private final AnAction action;

    public ActionAdapter(AnAction action) {
        super(action.getTemplatePresentation().getText(), action.getTemplatePresentation().getIcon());
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(null); // how to create AnActionEvent?
    }
}
