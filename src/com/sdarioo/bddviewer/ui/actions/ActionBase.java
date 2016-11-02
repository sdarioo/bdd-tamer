package com.sdarioo.bddviewer.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

public abstract class ActionBase extends AnAction {

    private boolean isEnabled = true;

    public ActionBase(String text, Icon icon) {
        super(text, text, icon);
    }

    @Override
    public void update (AnActionEvent e) {
        e.getPresentation().setEnabled(isEnabled);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
