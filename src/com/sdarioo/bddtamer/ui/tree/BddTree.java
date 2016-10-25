package com.sdarioo.bddtamer.ui.tree;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import com.sdarioo.bddtamer.launcher.*;
import com.sdarioo.bddtamer.provider.ProjectStoryProvider;
import com.sdarioo.bddtamer.provider.StoryParser;
import com.sdarioo.bddtamer.provider.StoryProvider;
import com.sdarioo.bddtamer.model.LocationHolder;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import com.sdarioo.bddtamer.ui.actions.ActionAdapter;
import com.sdarioo.bddtamer.ui.actions.BddActionManager;
import com.sdarioo.bddtamer.ui.util.IdeUtil;
import com.sdarioo.bddtamer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeColumnModel;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.DefaultTreeTableSorter;
import de.sciss.treetable.j.TreeTable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class BddTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTree.class);

    private final Project project;
    private final StoryProvider storyProvider;

    private TreeTable tree;
    private DefaultTreeModel treeModel;
    private BddActionManager actionManager;
    private final List<BddTreeColumns.ColumnInfo> columnInfos;

    public BddTree(Project project,
                   StoryProvider storyProvider,
                   SessionManager sessionManager) {
        this.project = project;
        this.storyProvider = storyProvider;
        this.columnInfos = BddTreeColumns.getColumns(sessionManager);
        initialize(sessionManager);

        addTreeListeners();
        addLauncherListener(sessionManager.getLauncher());
        addProjectListener();

        for (BddTreeColumns.ColumnInfo column : columnInfos) {
            tree.getColumn(column.getName()).setPreferredWidth(column.getPreferredWidth());
        }
    }

    public TreeTable getTreeTable() {
        return tree;
    }

    public BddActionManager getActionManager() {
        return actionManager;
    }

    public void setActionManager(BddActionManager actionManager) {
        this.actionManager = actionManager;
    }

    /**
     * @return tree model
     */
    public DefaultTreeModel getModel() {
        return treeModel;
    }

    /**
     * Reloads whole tree structure
     */
    public void reloadTree() {
        DefaultTreeTableNode root = buildRoot();
        treeModel = createTreeModel(root);
        tree.setTreeModel(treeModel);
    }

    /**
     * Refreshes visual appearance of given node
     * @param node
     */
    public void refreshNode(DefaultTreeTableNode node) {
        updateRowData(node);
        treeModel.nodeChanged(node);
    }

    private void initialize(SessionManager sessionManager) {
        DefaultTreeTableNode root = buildRoot();
        treeModel = createTreeModel(root);
        DefaultTreeColumnModel columnModel = new DefaultTreeColumnModel(root,
                columnInfos.stream().map(c -> c.toString()).collect(Collectors.toList()));
        columnModel.setAllColumnsEditable(false);

        tree = new TreeTable(treeModel, columnModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setIconMap(new BddIconMap(sessionManager));

        tree.setAutoCreateRowSorter(true);
        ((DefaultTreeTableSorter)tree.getRowSorter()).setSortsOnUpdates(true);

        tree.setDragEnabled(false);
        tree.setAutoCreateRowHeader(false);
        tree.setRowSelectionAllowed(true);
        tree.setCellSelectionEnabled(false);
        tree.setColumnFocusEnabled(false);
    }

    private DefaultTreeModel createTreeModel(DefaultTreeTableNode root) {
        return new DefaultTreeModel(root);
    }

    private DefaultTreeTableNode buildRoot() {
        DefaultTreeTableNode root = createNode(project);

        Map<Path, DefaultTreeTableNode> folderNodes = new HashMap<>();

        List<Story> stories = storyProvider.getStories(project);
        stories.forEach(story -> {
            DefaultTreeTableNode storyNode = createNode(story);
            Path dir = story.getLocation().getPath().getParent();
            DefaultTreeTableNode parent = root;
            if (!"stories".equals(dir.getFileName().toString())) {
                parent = folderNodes.get(dir);
                if (parent == null) {
                    parent = createNode(dir.getFileName());
                    folderNodes.put(dir, parent);
                    root.add(parent);
                }
            }

            parent.add(storyNode);
            story.getScenarios().forEach(scenario -> storyNode.add(createNode(scenario)));
        });
        return root;
    }

    private void reloadStoryNode(DefaultTreeTableNode node, Story story) {
        node.setUserObject(story);
        updateRowData(node);
        node.removeAllChildren();
        story.getScenarios().forEach(scenario -> node.add(createNode(scenario)));
        treeModel.reload(node);
    }

    private DefaultTreeTableNode createNode(Object modelObject) {
        DefaultTreeTableNode node = new DefaultTreeTableNode(getRowData(modelObject));
        node.setUserObject(modelObject);
        return node;
    }

    private Object[] getRowData(Object modelObject) {
        return columnInfos.stream()
                .map(c -> c.getValue(modelObject))
                .collect(Collectors.toList())
                .toArray(new Object[0]);
    }

    private void updateRowData(DefaultTreeTableNode node) {
        Object[] data = getRowData(node.getUserObject());
        for (int i = 0; i < data.length; i++) {
            node.setValueAt(data[i], i);
        }
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new ActionAdapter(actionManager.getRunSelectedAction()));
        return menu;
    }

    private void addTreeListeners() {
        MouseListener listener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int row = tree.rowAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (!tree.isRowSelected(row)) {
                        tree.setSelectionRow(row);
                    }
                    createPopupMenu().show(e.getComponent(), e.getX(), e.getY());

                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2) {
                        TreePath path = tree.getPathForRow(row);
                        tree.expandPath(path);
                        Object modelObject = ((DefaultTreeTableNode) path.getLastPathComponent()).getUserObject();
                        if (modelObject instanceof LocationHolder) {
                            IdeUtil.openInEditor(project, ((LocationHolder)modelObject).getLocation());
                        }
                    }
                }
            }
        };
        tree.addMouseListener(listener);
    }

    private void addLauncherListener(Launcher launcher) {
        launcher.addListener(new LauncherListener() {
            @Override
            public void scenarioStarted(Scenario scenario) {
                refreshScenario(scenario, true);
            }

            @Override
            public void scenarioFinished(Scenario scenario, TestResult result) {
                refreshScenario(scenario, false);
            }

            @Override
            public void sessionStarted(List<Scenario> scope) {
                scope.forEach( scenario -> refreshScenario(scenario, false));
            }

            @Override
            public void sessionFinished() {
            }

            private void refreshScenario(Scenario scenario, boolean scrollTo) {
                DefaultTreeTableNode node = TreeUtil.findNode(treeModel, scenario);
                if (node != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (scrollTo) {
                            tree.scrollPathToVisible(TreeUtil.pathToRoot(node));
                        }
                        refreshNode(node);
                    });
                } else {
                    LOGGER.warn("Cannot find node for scenario: " + scenario);
                }
            }
        });
    }

    private void addProjectListener() {
        MessageBusConnection connect = project.getMessageBus().connect();

        connect.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void before(@NotNull List<? extends VFileEvent> list) {
            }
            @Override
            public void after(@NotNull List<? extends VFileEvent> list) {
                for (VFileEvent e : list) {
                    if (!(e instanceof VFileContentChangeEvent)) {
                        continue;
                    }
                    if ((e.getFile() == null) || !ProjectStoryProvider.isStoryFile(e.getFile())) {
                        continue;
                    }
                    Path path = Paths.get(e.getPath());
                    DefaultTreeTableNode node = TreeUtil.findNode(treeModel, n -> isStoryNode(n, path));
                    if (node != null) {
                        try {
                            Story story = StoryParser.parse(path);
                            reloadStoryNode(node, story);
                        } catch (IOException exc) {
                            LOGGER.warn(e.toString());
                        }
                    }
                }
            }
            private boolean isStoryNode(DefaultTreeTableNode node, Path storyPath) {
                return (node.getUserObject() instanceof Story) &&
                        ((Story) node.getUserObject()).getLocation().getPath().equals(storyPath);
            }
        });
    }

}
