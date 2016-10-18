package com.sdarioo.bddtamer.ui;

import com.intellij.ui.SpeedSearchBase;
import de.sciss.treetable.j.TreeTable;
import org.jetbrains.annotations.Nullable;

// TODO - implement me
public class BddTreeSpeedSearch extends SpeedSearchBase<TreeTable> {

    public BddTreeSpeedSearch(TreeTable component) {
        super(component);
    }

    @Override
    protected int getSelectedIndex() {
        return 0;
    }

    @Override
    protected Object[] getAllElements() {
        return new Object[0];
    }

    @Nullable
    @Override
    protected String getElementText(Object o) {
        return null;
    }

    @Override
    protected void selectElement(Object o, String s) {

    }
}
