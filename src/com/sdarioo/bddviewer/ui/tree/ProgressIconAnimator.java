package com.sdarioo.bddviewer.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.util.Alarm;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressIconAnimator {

    private static long INTERVAL = 500L;
    private final AtomicBoolean scheduled = new AtomicBoolean();

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
        if (!scheduled.compareAndSet(false, true)) {
            return;
        }
        currentFrameIndex = (currentFrameIndex + 1) % FRAMES.length;
        alarm.addRequest(() -> {
            scheduled.set(false);
            DefaultTreeTableNode storyNode = TreeUtil.findNode(tree.getModel(), scenario.getStory());
            DefaultTreeTableNode scenarioNode = TreeUtil.findNode(tree.getModel(), scenario);
            if (storyNode != null) {
                tree.refreshNode(storyNode);
            }
            if (scenarioNode != null) {
                tree.refreshNode(scenarioNode);
            }
        }, INTERVAL);
    }
}
