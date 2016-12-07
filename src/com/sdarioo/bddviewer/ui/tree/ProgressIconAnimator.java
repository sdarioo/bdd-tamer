package com.sdarioo.bddviewer.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.util.Alarm;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;

import javax.swing.*;

public class ProgressIconAnimator {

    private static final Icon[] FRAMES = {
            AllIcons.RunConfigurations.TestInProgress1,
            AllIcons.RunConfigurations.TestInProgress2,
            AllIcons.RunConfigurations.TestInProgress3,
            AllIcons.RunConfigurations.TestInProgress4,
            AllIcons.RunConfigurations.TestInProgress5,
            AllIcons.RunConfigurations.TestInProgress6,
            AllIcons.RunConfigurations.TestInProgress7,
            AllIcons.RunConfigurations.TestInProgress8,
    };

    private int currentFrameIndex;
    private final Alarm alarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD);


    public Icon getProgressIcon() {
        return FRAMES[currentFrameIndex];
    }

    public void animate(BddTree tree, Scenario scenario) {
        currentFrameIndex = (currentFrameIndex + 1) % FRAMES.length;

        alarm.addRequest(() -> {
            DefaultTreeTableNode node = TreeUtil.findNode(tree.getModel(), scenario);
            if (node != null) {
                tree.refreshNode(node);
            }
        }, 300);
    }
}
