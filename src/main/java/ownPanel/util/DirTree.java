package ownPanel.util;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;

public class DirTree extends JTree implements TreeSelectionListener {

    private StringBuilder path;
    private StringBuilder root;
    private DefaultTreeModel rootNode;

    public StringBuilder getPath() {
        return path;
    }

    public void setPath(StringBuilder path) {
        this.path = path;
    }

    public StringBuilder getRoot() {
        return root;
    }

    public void setRoot(StringBuilder root) {
        this.root = root;
    }

    public DefaultTreeModel getRootNode() {
        return rootNode;
    }

    public void setRootNode(DefaultTreeModel rootNode) {
        this.rootNode = rootNode;
    }

    public DirTree(DefaultTreeModel param, StringBuilder root){
        super(param);
        this.setRootNode(param);
        this.addTreeSelectionListener(this);
        this.setRoot(root);
        this.setPath(new StringBuilder());
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode selectedPathComponent =
                (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        if (selectedPathComponent == null){
            return;
        }
        StringBuilder path = this.getPath();
        if (path.length() != 0) {
            path.delete(0, path.length());
        }
        path.append(this.getRoot().append("\\"));
        TreeNode[] treeNodes = selectedPathComponent.getPath();
        for (int i = 1; i < treeNodes.length; i++) {
            path.append(treeNodes[i]);
            if (!treeNodes[i].isLeaf()) {
                path.append("\\");
            }
        }
        this.setPath(path);


    }

    /**
     * 递归构建文件树的根节点。不要用这个程序去获取某个盘（比如C、D）下文件树，因为一整个盘下的
     * 文件太多，运行时如果没有设置特殊的参数（JVM 栈 和 堆的大小）可能会导致溢出使程序崩溃
     *
     * @param rootFile 根文件
     * @return 文件树的根节点
     */
    public static DefaultMutableTreeNode buildDirTree(File rootFile) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFile.getName());

        // 如果是文件，即没有子文件
        if (rootFile.isFile()) { return rootNode; }

        File[] subFiles = rootFile.listFiles();

        // 列出文件出错，一般不会发生
        if (subFiles == null) { return rootNode; }

        for (File subFile : subFiles) {
            // 如果 subFile 是文件，则新建一个树节点
            if (subFile.isFile()) {
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subFile.getName());
                rootNode.add(subNode);
            }

            // 如果 subFile 是目录，那么构建一颗子树
            if (subFile.isDirectory()) {
                DefaultMutableTreeNode subNode = buildDirTree(subFile);
                rootNode.add(subNode);
            }
        }

        return rootNode;
    }


}
