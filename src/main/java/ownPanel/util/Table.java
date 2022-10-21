package ownPanel.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Table extends JTable {

    private DefaultTableModel tableModel;

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public Table(){
        super();
        tableModel = new DefaultTableModel(50,8);
        this.setTableModel(tableModel);
        this.setModel(tableModel);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
}
