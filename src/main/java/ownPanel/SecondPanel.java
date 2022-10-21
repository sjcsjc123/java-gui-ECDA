package ownPanel;

import ownPanel.util.CenterButtons;
import ownPanel.util.DirTree;
import ownPanel.util.Table;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SecondPanel extends JFrame implements ActionListener {

    private DirTree tree;
    private CenterButtons calculateButton;
    private StringBuilder root;
    private Table table;

    public CenterButtons getCalculateButton() {
        return calculateButton;
    }

    public void setCalculateButton(CenterButtons calculateButton) {
        this.calculateButton = calculateButton;
    }

    public StringBuilder getRoot() {
        return root;
    }

    public void setRoot(StringBuilder root) {
        this.root = root;
    }

    public DirTree getTree() {
        return tree;
    }

    public void setTree(DirTree tree) {
        this.tree = tree;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public SecondPanel(StringBuilder data){
        super("电化学分析程序");
        this.setRoot(data);
        DefaultMutableTreeNode rootNode =
                DirTree.buildDirTree(new File(String.valueOf(this.getRoot())));

        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);
        tree = new DirTree(defaultTreeModel, this.getRoot());
        JScrollPane dirTree = new JScrollPane(tree);
        calculateButton = new CenterButtons(tree.getPath(),this);

        table = new Table();
        JScrollPane jTable = new JScrollPane(table);

        jTable.setPreferredSize(new Dimension(600,800));
        dirTree.setPreferredSize(new Dimension(500,800));

        this.setTable(table);
        this.setTree(tree);
        this.setCalculateButton(calculateButton);

        JButton cancel = new JButton("返回重新输入根路径");
        cancel.addActionListener(this);
        calculateButton.add(cancel);

        this.setLayout(new FlowLayout());
        this.add(dirTree);
        this.add(calculateButton);
        this.add(jTable);

        this.setVisible(true);
        this.setSize(1600, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FirstPanel firstPanel = new FirstPanel();
        this.setVisible(false);
        firstPanel.setVisible(true);
    }
}
