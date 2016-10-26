package com.sdarioo.bddtamer.ui.tree;

import de.sciss.treetable.j.DefaultTreeColumnModel;
import de.sciss.treetable.j.DefaultTreeTableSorter;
import de.sciss.treetable.j.TreeColumnModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;


public class BddTreeSorter
        extends DefaultTreeTableSorter<TreeModel, TreeColumnModel, Object> {

    private SortOrder order = SortOrder.UNSORTED;

    public BddTreeSorter(DefaultTreeModel tm, DefaultTreeColumnModel cm) {
        super(tm, cm);

        setComparators();
    }

    @Override
    public void toggleSortOrder(int column) {
        super.toggleSortOrder(column);
        if (column == 0) {
            List<SortOrder> sortCycle = getSortCycle();
            int index = sortCycle.indexOf(order);
            index = (index + 1) % sortCycle.size();
            order = sortCycle.get(index);
        }
    }

    private void setComparators() {
        int count = getTreeColumnModel().getColumnCount();
        for (int i = 0; i < count; i++) {
            setSortable(i, true);
            setComparator(i, (n1, n2) -> {
                if (n1.getClass().equals(n2.getClass())) {
                    return Comparator.comparing(Object::toString, Comparator.naturalOrder()).compare(n1, n2);
                }
                // No matter what sort order is, we want folders be at the beginning
                if (order == SortOrder.ASCENDING) {
                    return (n1 instanceof Path) ? 1 : -1;
                }
                return (n1 instanceof Path) ? -1 : 1;
            });
        }
    }



}
