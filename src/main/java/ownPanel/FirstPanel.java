package ownPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FirstPanel extends JFrame implements ActionListener {

    private StringBuilder root;
    private JButton button;

    public FirstPanel(){
        super("电化学分析程序");
        button = new JButton("点击输入根路径");
        button.addActionListener(this);
        JPanel jPanel = new JPanel();
        jPanel.add(button);
        jPanel.setVisible(true);

        this.add(button);
        this.setVisible(true);
        this.setSize(1600, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        root = new StringBuilder();
        String str = JOptionPane.showInputDialog("输入基本路径:");
        if (!new File(str).exists()){
            return;
        }
        root.append(str);
        this.setVisible(false);
        SecondPanel secondPanel = new SecondPanel(root);
        secondPanel.setVisible(true);
    }
}
