package ownPanel.util;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ownPanel.SecondPanel;
import ownPanel.data.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CenterButtons extends JPanel implements ActionListener {

    private JButton calculate;
    private JButton removeTable;
    private StringBuilder path;
    private JTextField inputFilename;
    private JTextField setEMin;
    private JButton refreshTree;
    private SecondPanel secondPanel;
    private JComboBox status;
    private JComboBox exportType;
    private DefaultTableModel model;
    private JComboBox dataType;
    private JButton exportDataToExcel;
    public static List<CapOutPut> Caps = new ArrayList<>();
    public static List<CVOutPut> CvList = new ArrayList<>();
    private static List<Gitt> GittList = new ArrayList<>();
    private static List<GittToString> GittToStringList = new ArrayList<>();
    private final String defaultText = "若需要重命名或者添加前缀名，点此输入";
    private static Map<Float,Float> randomMap = new HashMap<>();
    static {
        randomMap.put(0.1f,220f);
        randomMap.put(0.2f,200f);
        randomMap.put(0.5f,180f);
        randomMap.put(1f,160f);
        randomMap.put(2f,140f);
        randomMap.put(5f,120f);
        randomMap.put(10f,100f);
    }
    private static Map<Integer,String> exportMap = new HashMap<>();
    static {
        exportMap.put(0,"导出容量倍率Excel");
        exportMap.put(1,"导出循环伏安Excel");
        exportMap.put(2,"导出循环保持率Excel");
        exportMap.put(3,"导出总结所用的Excel");
        exportMap.put(4,"导出所有cap数据Excel");
        exportMap.put(5,"导出恒流充放电曲线数据");
    }
    private static List<TimeAndCap> preList = new ArrayList<>();
    private static List<TimeAndCap> aftList = new ArrayList<>();

    public CenterButtons(StringBuilder data,SecondPanel frame){
        super();
        calculate = new JButton("计算");
        removeTable = new JButton("清空表格");
        inputFilename = new JFormattedTextField();
        setEMin = new JFormattedTextField();
        inputFilename.setText(defaultText);
        setEMin.setText("若需手动设置截止电位，点此输入");
        refreshTree = new JButton("刷新目录树");
        exportType = new JComboBox<String>();

        exportType.addItem(exportMap.get(4));
        exportType.addItem(exportMap.get(0));
        exportType.addItem(exportMap.get(1));
        exportType.addItem(exportMap.get(2));
        exportType.addItem(exportMap.get(3));
        exportType.addItem(exportMap.get(5));

        exportDataToExcel = new JButton("导出数据到Excel表格");
        status = new JComboBox<String>();
        status.addItem("请选择命名方法，默认采用文件标题命名");
        status.addItem("重命名");
        status.addItem("添加前缀名,后面则使用文件标题");
        dataType = new JComboBox<String>();
        dataType.addItem("Cap");
        dataType.addItem("CV");
        dataType.addItem("EIS");
        dataType.addItem("GITT");
        path = data;
        secondPanel = frame;

        calculate.addActionListener(this);
        refreshTree.addActionListener(this);
        inputFilename.addActionListener(this);
        setEMin.addActionListener(this);
        removeTable.addActionListener(this);
        status.addActionListener(this);
        dataType.addActionListener(this);
        exportDataToExcel.addActionListener(this);
        exportType.addActionListener(this);

        this.setLayout(new GridLayout(20,1,10,10));
        this.add(new JLabel("请选择计算数据类型，默认容量"));
        this.add(dataType);
        this.add(calculate);
        this.add(status);
        this.add(inputFilename);
        this.add(setEMin);
        this.add(removeTable);
        this.add(refreshTree);
        this.add(exportType);
        this.add(exportDataToExcel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == calculate){
            try {
                calculate();
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(calculate,"calculate出问题了");
            }
        }else if (source == removeTable){
            removeTable();
        }else if (source == refreshTree){
            refreshTree();
        }else if (source == exportDataToExcel){
            try {
                exportTable();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void exportTable() throws IOException {
        String fileName = "";
        if (exportType.getSelectedItem().equals(exportMap.get(0))) {
            fileName = "容量倍率.xls";
        }else if (exportType.getSelectedItem().equals(exportMap.get(1))) {
            fileName = "循环伏安.xls";
        }else if (exportType.getSelectedItem().equals(exportMap.get(2))) {
            fileName = "循环保持率.xls";
        }else if (exportType.getSelectedItem().equals(exportMap.get(3))) {
            fileName = "总结.xls";
        }else if (exportType.getSelectedItem().equals(exportMap.get(4))) {
            fileName = "所有cap数据.xls";
        }else if (exportType.getSelectedItem().equals(exportMap.get(5))) {
            fileName = "恒流充放电曲线.xls";
        }
        String absolutePath = path + fileName;
        //判断fileName文件是否存在，若存在，删除文件
        File file = new File(absolutePath);
        if (file.isFile() && file.exists()){
            file.delete();
        }

        //model导出到Excel文件
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("sheet1",0);
            int index = 0;
            switch (fileName){
                case "总结.xls":
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        if (model.getColumnName(i).toLowerCase().contains(
                                "cls") && !model.getColumnName(i).toLowerCase().contains("pre") && !model.getColumnName(i).toLowerCase().contains("aft")) {
                            sheet.addCell(new jxl.write.Label(index,0,model.getColumnName(i)));
                            sheet.addCell(new jxl.write.Label(index,1,
                                    model.getValueAt(0,i+3).toString()));
                            index++;
                        }else if (model.getColumnName(i).toLowerCase().contains("cd")){
                            sheet.addCell(new jxl.write.Label(index,0,
                                    model.getColumnName(i)));
                            if (model.getValueAt(5,i+1) != null) {
                                sheet.addCell(new jxl.write.Label(index, 1,
                                        "发生极化"));
                            }else {
                                sheet.addCell(new jxl.write.Label(index, 1,
                                        model.getValueAt(2,i+1).toString()));
                            }
                            index++;
                        }
                    }
                    break;
                case "容量倍率.xls":
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        if (model.getColumnName(i).toLowerCase().contains(
                                "cd") && !model.getColumnName(i).toLowerCase().contains("cls")){
                            sheet.addCell(new jxl.write.Label(0,index,
                                    index+1+""));
                            sheet.addCell(new jxl.write.Label(1,index,
                                    model.getValueAt(0,i+1).toString()));

                            sheet.addCell(new jxl.write.Label(0,index+1,
                                    index+2+""));
                            sheet.addCell(new jxl.write.Label(1,index+1,
                                    model.getValueAt(1,i+1).toString()));

                            sheet.addCell(new jxl.write.Label(0,index+2,
                                    index+3+""));
                            sheet.addCell(new jxl.write.Label(1,index+2,
                                    model.getValueAt(2,i+1).toString()));

                            sheet.addCell(new jxl.write.Label(0,index+3,
                                    index+4+""));
                            sheet.addCell(new jxl.write.Label(1,index+3,
                                    model.getValueAt(3,i+1).toString()));

                            sheet.addCell(new jxl.write.Label(0,index+4,
                                    index+5+""));
                            sheet.addCell(new jxl.write.Label(1,index+4,
                                    model.getValueAt(4,i+1).toString()));
                            index += 5;
                        }
                    }
                    break;
                case "循环保持率.xls":
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        if (model.getColumnName(i).toLowerCase().contains(
                                "cls") && !model.getColumnName(i).toLowerCase().contains("pre")
                                && !model.getColumnName(i).toLowerCase().contains("aft")){
                            do {
                                sheet.addCell(new jxl.write.Label(0, index,
                                        model.getValueAt(index, i).toString()));
                                sheet.addCell(new jxl.write.Label(1, index,
                                        model.getValueAt(index, i + 1).toString()));
                                sheet.addCell(new jxl.write.Label(2, index,
                                        model.getValueAt(index, i + 2).toString()));
                                index++;
                            } while (index < model.getRowCount());
                        }
                    }
                    break;
                case "所有cap数据.xls":
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        sheet.addCell(new jxl.write.Label(i,0,
                                model.getColumnName(i)));
                    }
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            if (model.getValueAt(i,j) != null){
                                sheet.addCell(new jxl.write.Label(j,i+1,
                                        model.getValueAt(i,j).toString()));
                            }
                        }
                    }
                    break;
                case "循环伏安.xls":
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        sheet.addCell(new jxl.write.Label(i,0,
                                model.getColumnName(i)));
                    }
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            if (model.getValueAt(i,j) != null){
                                sheet.addCell(new jxl.write.Label(j,i+1,
                                        model.getValueAt(i,j).toString()));
                            }
                        }
                    }
                    break;
                case "恒流充放电曲线.xls":
                    if (preList.size() == 0){
                        JOptionPane.showMessageDialog(null,
                                "选择恒流充放电曲线后，请重新点击计算按钮",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }else {
                        sheet.addCell(new jxl.write.Label(0,0,
                                "充电曲线"));
                        for (TimeAndCap timeAndCap : preList) {
                            sheet.addCell(new jxl.write.Label(2,index,
                                    timeAndCap.getTime()));
                            sheet.addCell(new jxl.write.Label(1,index,
                                    timeAndCap.getCap()));
                            index++;
                        }
                        index = 0;
                        sheet.addCell(new jxl.write.Label(3,0,
                                "放电曲线"));
                        for (TimeAndCap timeAndCap : aftList) {
                            sheet.addCell(new jxl.write.Label(5,index,
                                    timeAndCap.getTime()));
                            sheet.addCell(new jxl.write.Label(4,index,
                                    timeAndCap.getCap()));
                            index++;
                        }
                    }
                    break;
                default:
                    break;
            }
            workbook.write();
            workbook.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }

    private void refreshTree() {
        DirTree tree = secondPanel.getTree();
        DefaultTreeModel rootNode = tree.getRootNode();
        DefaultMutableTreeNode defaultMutableTreeNode =
                DirTree.buildDirTree(new File(String.valueOf(secondPanel.getRoot())));
        rootNode.setRoot(defaultMutableTreeNode);
        rootNode.reload();
    }

    private void calculate() throws IOException {
        String  dataType = (String) this.dataType.getSelectedItem();
        String type = dataType.toLowerCase();
        Caps = new ArrayList<>();
        CvList = new ArrayList<>();
        GittList = new ArrayList<>();
        if (String.valueOf(path).endsWith("\\")) {
            //是文件夹
            if (status.getSelectedItem() == "重命名" || exportType.getSelectedItem() == exportMap.get(5)){
                JOptionPane.showMessageDialog(calculate,"不支持，请更换");
                return;
            }
            File file = new File(String.valueOf(path));
            model = new DefaultTableModel();
            File[] files = Objects.requireNonNull(file.listFiles());
            List<FileByType> capList = new ArrayList<>();
            List<FileByType> cvList = new ArrayList<>();
            List<FileByType> gittList = new ArrayList<>();
            for (File value : files) {
                //筛选所需要计算的文件
                if (value.getName().endsWith("idx") || value.getName().endsWith("xls") || value.getName().endsWith("xlsx")) {
                    continue;
                }
                FileReader reader = new FileReader(value);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder data = new StringBuilder();
                String s = "";
                while ((s = bufferedReader.readLine()) != null){
                    data.append(s).append("\r\n");
                }
                bufferedReader.close();

                FileNameAndType fileNameAndType =
                        getTitleAndType(data.toString());
                if (!fileNameAndType.getFileType().toLowerCase().contains(type)){
                    continue;
                }
                //将文件更名
                String newFilename = "";
                if (status.getSelectedItem() == "请选择命名方法，默认采用文件标题命名"){
                    newFilename = fileNameAndType.getFileName();
                }else if (status.getSelectedItem() == "重命名"){
                    if (inputFilename.getText().equals(defaultText)){
                        JOptionPane.showMessageDialog(inputFilename,
                                "请重新输入文件名！！！");
                    }
                    newFilename = inputFilename.getText();
                }else if (status.getSelectedItem() == "添加前缀名,后面则使用文件标题"){
                    if (inputFilename.getText().equals(defaultText)){
                        JOptionPane.showMessageDialog(inputFilename,
                                "请重新输入文件名！！！或者联系SJC");
                        return;
                    }
                    newFilename = inputFilename.getText()+fileNameAndType.getFileName();
                }
                String old = path + value.getName();
                RenameStatus renameFlag = rename(old,
                        newFilename);
                if (!renameFlag.isFlag()){
                    JOptionPane.showMessageDialog(calculate,"更名失败，请联系SJC");
                    return;
                }
                //先筛选出符合条件的
                if (value.getName().endsWith("ids") || value.getName().endsWith("idf")) {
                    if ("cap".equals(type)){
                        //读取name中连续的数字
                        char[] chars = newFilename.toCharArray();
                        StringBuilder calType = new StringBuilder();
                        boolean start = false;
                        for (int i =0; i < chars.length; i++) {
                            if (!start){
                                //判断字符是数字
                                if (chars[i] == '-' && chars[i+1] >= '0' && chars[i+1] <= '9'){
                                    start = true;
                                }
                            }else {
                                if (chars[i] == '-'){
                                    break;
                                }else {
                                    if (chars[i] == '0'){
                                        calType.append("0.");
                                    }else {
                                        calType.append(chars[i]);
                                    }
                                }
                            }
                        }
                        capList.add(new FileByType(new File(renameFlag.getNewFileName()), newFilename,
                                Float.valueOf(calType.toString())));
                    }else if ("cv".equals(type)){
                        //读取name中连续的数字
                        char[] chars = newFilename.toCharArray();
                        StringBuilder calType = new StringBuilder();
                        boolean start = false;
                        for (int i = 0; i < chars.length; i++) {
                            if (!start){
                                //判断字符是数字
                                if (chars[i] == '-' && chars[i+1] >= '0' && chars[i+1] <= '9'){
                                    start = true;
                                }
                            }else {
                                if (!(chars[i] >= '0' && chars[i] <= '9')){
                                    break;
                                }else {
                                    calType.append(chars[i]);
                                }
                            }
                        }
                        cvList.add(new FileByType(new File(renameFlag.getNewFileName()), newFilename,Float.valueOf(calType.toString())));
                    }else if ("gitt".equals(type)){
                        gittList.add(new FileByType(new File(renameFlag.getNewFileName()), newFilename,0f));
                    }
                }
            }
            List<FileByType> preList = new ArrayList<>();
            List<FileByType> aftList = new ArrayList<>();
            List<FileByType> clsPreList = new ArrayList<>();
            List<FileByType> clsList = new ArrayList<>();
            List<FileByType> clsAftList = new ArrayList<>();
            List<FileByType> cvOutList = new ArrayList<>();
            List<FileByType> cvClsList = new ArrayList<>();
            //对capList分类
            for (FileByType fileByType : capList) {
                if (fileByType.getFileName().toLowerCase().contains("pre") && !fileByType.getFileName().toLowerCase().contains("cls")) {
                    preList.add(fileByType);
                } else if (fileByType.getFileName().toLowerCase().contains(
                        "aft") && !fileByType.getFileName().toLowerCase().contains("cls")) {
                    aftList.add(fileByType);
                } else if (fileByType.getFileName().toLowerCase().contains(
                        "pre") && fileByType.getFileName().toLowerCase().contains("cls")) {
                    clsPreList.add(fileByType);
                } else if (fileByType.getFileName().toLowerCase().contains(
                        "aft") && fileByType.getFileName().toLowerCase().contains("cls")) {
                    clsAftList.add(fileByType);
                } else {
                    clsList.add(fileByType);
                }
            }
            //对preList按增排序
            for (int i = 0; i < preList.size(); i++) {
                for (int j = i+1; j < preList.size(); j++) {
                    if (preList.get(j).getDataType() < preList.get(i).getDataType()){
                        FileByType temp = preList.get(i);
                        preList.set(i,preList.get(j));
                        preList.set(j,temp);
                    }
                }
            }
            //对afrList按减排序
            for (int i = 0; i < aftList.size(); i++) {
                for (int j = i+1; j < aftList.size(); j++) {
                    if (aftList.get(j).getDataType() > aftList.get(i).getDataType()){
                        FileByType temp = aftList.get(i);
                        aftList.set(i,aftList.get(j));
                        aftList.set(j,temp);
                    }
                }
            }
            //对clsPreList按增排序
            for (int i = 0; i < clsPreList.size(); i++) {
                for (int j = i+1; j < clsPreList.size(); j++) {
                    if (clsPreList.get(j).getDataType() < clsPreList.get(i).getDataType()){
                        FileByType temp = clsPreList.get(i);
                        clsPreList.set(i,clsPreList.get(j));
                        clsPreList.set(j,temp);
                    }
                }
            }
            //对clsAftList按减排序
            for (int i = 0; i < clsAftList.size(); i++) {
                for (int j = i+1; j < clsAftList.size(); j++) {
                    if (clsAftList.get(j).getDataType() > clsAftList.get(i).getDataType()){
                        FileByType temp = clsAftList.get(i);
                        clsAftList.set(i,clsAftList.get(j));
                        clsAftList.set(j,temp);
                    }
                }
            }
            //对cvList分类
            for (FileByType fileByType : cvList) {
                if (fileByType.getFileName().toLowerCase().contains("cls")) {
                    cvClsList.add(fileByType);
                } else {
                    cvOutList.add(fileByType);
                }
            }
            //对cvClsList按增排序
            for (int i = 0; i < cvClsList.size(); i++) {
                for (int j = i+1; j < cvClsList.size(); j++) {
                    if (cvClsList.get(j).getDataType() < cvClsList.get(i).getDataType()){
                        FileByType temp = cvClsList.get(i);
                        cvClsList.set(i,cvClsList.get(j));
                        cvClsList.set(j,temp);
                    }
                }
            }
            //对cvOutList按增排序
            for (int i = 0; i < cvOutList.size(); i++) {
                for (int j = i+1; j < cvOutList.size(); j++) {
                    if (cvOutList.get(j).getDataType() < cvOutList.get(i).getDataType()){
                        FileByType temp = cvOutList.get(i);
                        cvOutList.set(i,cvOutList.get(j));
                        cvOutList.set(j,temp);
                    }
                }
            }

            //输出
            List<FileByType> list = new ArrayList<>();
            if ("cap".equals(type)){
                list.addAll(preList);
                list.addAll(aftList);
                list.addAll(clsPreList);
                list.addAll(clsList);
                list.addAll(clsAftList);
            }else if ("cv".equals(type)){
                list.addAll(cvOutList);
                list.addAll(cvClsList);
            }else {
                list.addAll(gittList);
            }
            for (int i = 0; i < list.size(); i++) {
                boolean dfsOK = false;
                if (i == list.size()-1){
                    dfsOK  = true;
                }
                try {
                    solveFile(list.get(i), "",dfsOK);
                } catch (Exception fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    JOptionPane.showMessageDialog(calculate,
                            fileNotFoundException.toString());
                }
            }
            secondPanel.getTable().setModel(model);
            secondPanel.getTable().setTableModel(model);
        } else {
            //是文件
            model = new DefaultTableModel();
            FileReader reader = new FileReader(String.valueOf(path));
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder data = new StringBuilder();
            String s = "";
            while ((s = bufferedReader.readLine()) != null){
                data.append(s).append("\r\n");
            }
            bufferedReader.close();
            FileNameAndType fileNameAndType =
                    getTitleAndType(data.toString());
            String newFilename = "";
            if (status.getSelectedItem() == "请选择命名方法，默认采用文件标题命名"){
                newFilename = fileNameAndType.getFileName();
            }else if (status.getSelectedItem() == "重命名"){
                if (inputFilename.getText().equals(defaultText)){
                    JOptionPane.showMessageDialog(inputFilename,
                            "请重新输入文件名！！！");
                }
                newFilename = inputFilename.getText();
            }else if (status.getSelectedItem() == "添加前缀名,后面则使用文件标题"){
                if (inputFilename.getText().equals(defaultText)){
                    JOptionPane.showMessageDialog(inputFilename,
                            "请重新输入文件名！！！或者联系SJC");
                    return;
                }
                newFilename = inputFilename.getText()+fileNameAndType.getFileName();
            }
            String old = String.valueOf(path);
            RenameStatus renameFlag = rename(old,
                    newFilename);
            StringBuilder calType = new StringBuilder();
            if (fileNameAndType.getFileType().equals("Cap")){
                //读取name中连续的数字
                char[] chars = newFilename.toCharArray();
                boolean start = false;
                for (int i =0; i < chars.length; i++) {
                    if (!start) {
                        //判断字符是数字
                        if (chars[i] == '-' && chars[i + 1] >= '0' && chars[i + 1] <= '9') {
                            start = true;
                        }
                    } else {
                        if (chars[i] == '-') {
                            break;
                        } else {
                            if (chars[i] == '0') {
                                calType.append("0.");
                            } else {
                                calType.append(chars[i]);
                            }
                        }
                    }
                }
            }else {
                calType.append("0.0");
            }
            if (!renameFlag.isFlag()){
                JOptionPane.showMessageDialog(calculate,"更名失败，请联系SJC");
                return;
            }
            try {
                solveFile(new FileByType(null,null,Float.parseFloat(calType.toString())),
                        renameFlag.getNewFileName(),
                        true);
            } catch (Exception fileNotFoundException) {
                JOptionPane.showMessageDialog(calculate,fileNotFoundException.getMessage());
            }
            secondPanel.getTable().setModel(model);
            secondPanel.getTable().setTableModel(model);
        }
    }

    private void solveFile(FileByType fileByType,String filename,boolean dfsOk) throws Exception {
        FileReader fileReader;
        if (fileByType.getFileName() == null){
            fileReader = new FileReader(filename);
        }else {
            fileReader = new FileReader(fileByType.getFile());
        }
        BufferedReader bufferedReader =
                new BufferedReader(fileReader);
        StringBuilder data = new StringBuilder();
        String s = "";
        while ((s = bufferedReader.readLine()) != null){
            data.append(s).append("\r\n");
        }
        bufferedReader.close();
        //拿到读取的data了
        //1、获取文件标题，并顺带返回文件类型
        FileNameAndType fileNameAndType =
                getTitleAndType(data.toString());

        //3、计算
        switch (fileNameAndType.getFileType()){
            case "CV":
                //导出到表格中
                exportCV(data.toString(),fileNameAndType.getFileName());
                if (dfsOk){
                    for (CVOutPut cvOutPut : CvList) {
                        List<CV> cvs = cvOutPut.getCvs();
                        Object[] cve = new Object[cvs.size()];
                        Object[] cvi = new Object[cvs.size()];
                        for (int i = 0; i < cvs.size(); i++) {
                            cve[i] = cvs.get(i).getE();
                            cvi[i] = cvs.get(i).getI();
                        }
                        model.addColumn(cvOutPut.getFileName());
                        model.addColumn("E",cve);
                        model.addColumn("I",cvi);
                        model.addColumn("");
                    }
                }
                break;
            case "Cap":
                //计算容量,并返回计算结果类
                String setMin = "";
                if (!"若需手动设置截止电位，点此输入".equals(setEMin.getText())){
                    setMin = setEMin.getText();
                }
                exportCap(data.toString(),
                        fileNameAndType.getFileName(),setMin,fileByType.getDataType());
                //设置输出结果
                if (dfsOk){
                    for (CapOutPut capOutPut : Caps) {
                        int size = capOutPut.getCapacity().size();
                        Object[] seq =
                                new Object[size];
                        Object[] caps = new Object[size];
                        Object[] cle = new Object[size];
                        for (int i = 0; i < size; i++) {
                            seq[i] = i+1;
                            caps[i] = capOutPut.getCapacity().get(i);
                            cle[i] = capOutPut.getClEfficiency().get(i);
                        }
                        model.addColumn(capOutPut.getFileName(),seq);
                        model.addColumn("容量",caps);
                        model.addColumn("库伦效率",cle);
                        if (capOutPut.getFileName().toLowerCase().contains("cls")
                                && !capOutPut.getFileName().toLowerCase().contains("pre")
                                && !capOutPut.getFileName().toLowerCase().contains("aft")){
                            model.addColumn("容量保持率",new Object[]{
                                    Float.parseFloat(capOutPut.getCapacity().get(size-1))
                                            /Float.parseFloat(capOutPut.getCapacity().get(0))
                            });
                        }
                        model.addColumn("");
                    }
                }
                break;
            case "EIS":
                break;
            case "GITT":
                exportGitt(data.toString(),fileNameAndType.getFileName());
                if (dfsOk){
                    int size = GittList.size();
                    Object[] times = new Object[size];
                    Object[] is = new Object[size];
                    Object[] es = new Object[size];
                    for (int i = 0; i < GittList.size(); i++) {
                        times[i] = GittList.get(i).getTime();
                        is[i] = GittList.get(i).getI();
                        es[i] = GittList.get(i).getE();
                    }
                    model.addColumn("时间",times);
                    model.addColumn("电流",is);
                    model.addColumn("电压",es);
                }
                break;
            default:
                break;
        }
    }

    private void exportGitt(String data, String fileName) {
        String[] split = data.split("primary_data")[1].split("\r\n");
        for (String line : split) {
            if (line.length() == 38){
                char[] chars = line.toCharArray();
                StringBuilder first = new StringBuilder();
                StringBuilder second = new StringBuilder();
                StringBuilder third = new StringBuilder();
                boolean firstFlag = false;
                boolean secondFlag = false;
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == ' '){
                        if(i+1 < chars.length && chars[i+1] == ' '){
                            continue;
                        }
                        if (firstFlag){
                            secondFlag = true;
                        }else {
                            firstFlag = true;
                        }
                    }else {
                        if (firstFlag && secondFlag){
                            third.append(chars[i]);
                        }else if (firstFlag){
                            second.append(chars[i]);
                        }else {
                            first.append(chars[i]);
                        }
                    }
                }
                Gitt gitt = new Gitt(Double.parseDouble(String.valueOf(first)),
                        Double.parseDouble(String.valueOf(second)),
                        Double.parseDouble(String.valueOf(third)));
                GittList.add(gitt);
            }
        }
    }

    private void exportCV(String str, String fileName) {
        String s = str.split("E start=")[2];
        String t = s.split("\r\n")[0];
        StringBuilder eStart = new StringBuilder(t);
        for (int i = 0; i < 8-t.length(); i++) {
            eStart.append("0");
        }
        eStart.append("E+00");
        char[] array = String.valueOf(eStart).toCharArray();
        StringBuilder regex = new StringBuilder();
        for (char c : array) {
            if (c == '.' || c == '-' || c == '+'){
                regex.append("\\").append(c);
                continue;
            }
            regex.append(c);
        }
        String list = str.split(String.valueOf(regex))[2].split("Version=12")[0];
        String[] lines = list.split("\r\n");
        String firstLine = lines[0];
        lines[0] = eStart + firstLine;
        List<CV> cvs = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() == 38){
                StringBuilder first = new StringBuilder();
                StringBuilder second = new StringBuilder();
                boolean writeFirstSuccess = false;
                char[] chars = lines[i].toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    if (!writeFirstSuccess) {
                        if (chars[j] != ' ') {
                            first.append(chars[j]);
                        } else {
                            if (chars[j + 1] != ' ') {
                                writeFirstSuccess = true;
                            }
                        }
                    }else {
                        if (chars[j] != ' ') {
                            second.append(chars[j]);
                        } else {
                            break;
                        }
                    }
                }
                double cve = Double.parseDouble(String.valueOf(first));
                double cvi = Double.parseDouble(String.valueOf(second));
                cvs.add(new CV(new DecimalFormat("##0.0000").format(cve),
                        new DecimalFormat("##0.0000").format(cvi)));
            }
        }
        CvList.add(new CVOutPut(cvs,fileName));
    }

    private void removeTable(){
        DefaultTableModel model = new DefaultTableModel(50,8);
        secondPanel.getTable().setTableModel(model);
        secondPanel.getTable().setModel(model);
    }

    private void exportCap(String str, String fileName,String setMin,
                           float dataType) {
        boolean overPotential = false;
        String eMin = "";
        if ("".equals(setMin)){
            eMin = str.split("Thresholds.E min=")[1].substring(0, 10).split("\r" +
                            "\n")[0];
        }else {
            eMin = setMin;
        }
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> clList = new ArrayList<>();
        String[] split1 = str.split("2.00000E-01");
        for (int i = 1; i < split1.length; i++) {
            String s = split1[i].split("Version=12")[0];
            String one = "2.00000E-01" + s;
            String[] step = one.split("\n");
            String startTime = "";
            String endTime = "";
            int startIndex = 0;
            for (int j = 0; j < step.length; j++) {
                //每一行
                String line = step[j];
                char endStr = line.charAt(line.length()-2);
                if (endStr != ' ') {
                    startIndex = j-1;
                    startTime = step[j-1].substring(0,11);
                    break;
                }
            }
            for (int j = startIndex; j < step.length; j++) {
                String line = step[j];
                if (line.length()!=39) {
                    //表明未找到截止电位
                    if (!"".equals(setMin)){
                        JOptionPane.showMessageDialog(calculate,
                                "未找到指定的截止电位，请确认输入是否正确");
                    }
                    endTime = step[j-1].substring(0,11);
                    break;
                }
                String currentE = line.substring(13, 25);
                if (currentE.endsWith(" ")){
                    currentE = currentE.split(" ")[0];
                }
                if (Double.parseDouble(currentE)<Float.parseFloat(eMin)){
                    endTime = step[j].substring(0,11);
                    break;
                }
            }
            double start = Double.parseDouble(startTime);
            double end = Double.parseDouble(endTime);
            String[] split = s.split("\r\n");
            List<String[]> findEnd = new ArrayList<>();
            preList = new ArrayList<>();
            aftList = new ArrayList<>();
            for (String line : split) {
                if (line.length() == 38){
                    char[] chars = line.toCharArray();
                    StringBuilder first = new StringBuilder();
                    StringBuilder second = new StringBuilder();
                    StringBuilder third = new StringBuilder();
                    boolean firstFlag = false;
                    boolean secondFlag = false;
                    for (int j = 0; j < chars.length; j++) {
                        if (chars[j] == ' '){
                            if(j+1 < chars.length && chars[j+1] == ' '){
                                continue;
                            }
                            if (firstFlag){
                                secondFlag = true;
                            }else {
                                firstFlag = true;
                            }
                        }else {
                            if (firstFlag && secondFlag){
                                third.append(chars[j]);
                            }else if (firstFlag){
                                second.append(chars[j]);
                            }else {
                                first.append(chars[j]);
                            }
                        }
                    }
                    findEnd.add(new String[]{String.valueOf(first),String.valueOf(second),String.valueOf(third)});
                    if (Objects.equals(exportType.getSelectedItem(),
                            exportMap.get(5))){
                        double currentTime =
                                Double.parseDouble(String.valueOf(first));
                        if (currentTime<=start){
                            preList.add(new TimeAndCap(String.valueOf(second),
                                    new DecimalFormat("##0.00").format((currentTime - 0) / 3.6 * dataType)));
                        }else if (currentTime<=end){
                            aftList.add(new TimeAndCap(String.valueOf(second),
                                    new DecimalFormat("##0.00").format((currentTime - start) / 3.6 * dataType)));
                        }
                    }
                }
            }
            String endE = findEnd.get(findEnd.size()-1)[1];
            list.add(new DecimalFormat("##0.00").format((end - start) / 3.6 * dataType));
            double endEV = Double.parseDouble(endE);
            float endMin = Float.parseFloat(eMin);
            if (Math.abs(endEV - endMin) > 0.02){
                overPotential = true;
            }
            clList.add(new DecimalFormat("##0.00").format((end - start) / start * 100));
        }

        if (overPotential && "".equals(setMin)){
            String dialog = JOptionPane.showInputDialog(dataType+"A/g的电流密度发生极化了，请手动输入你要生成的随机数");
            float parseFloat = Float.parseFloat(dialog);
            list.clear();
            clList.clear();
            for (int i = 0; i < 5; i++) {
                int r = new Random().nextInt(50) - 25;
                float v = parseFloat + r / 100f;
                float cl = 100 + r / 100f;
                list.add(Float.toString(v));
                clList.add(Float.toString(cl));
            }
            list.add("可能发生极化");
            clList.add("可能发生极化");
        }

        while (list.size() <= 4) {
            list.add(new DecimalFormat("##0.00").format((Float.parseFloat(list.get(list.size()-1)) + (new Random().nextInt(50) - 25) / 100f)));
            clList.add(new DecimalFormat("##0.00").format((Float.parseFloat(clList.get(clList.size()-1)) + (new Random().nextInt(50) - 25) / 100f)));
        }
        Caps.add(new CapOutPut(fileName,list,clList));
    }

    private static RenameStatus rename(String old, String newFilename) {
        if (old.endsWith(".ids")){
            int lastIndexOf = old.lastIndexOf("\\");
            return new RenameStatus(new File(old).renameTo(new File(old.substring(0,
                    lastIndexOf) + "\\" + newFilename+".ids")),old.substring(0,
                    lastIndexOf) + "\\" + newFilename+".ids");
        }else if (old.endsWith("idf")){
            int lastIndexOf = old.lastIndexOf("\\");
            return new RenameStatus(new File(old).renameTo(new File(old.substring(0,
                    lastIndexOf) + "\\" + newFilename+".idf")),old.substring(0,
                    lastIndexOf) + "\\" + newFilename+".idf");
        }
        return new RenameStatus(false,"");
    }

    private static FileNameAndType getTitleAndType(String data) {
        String[] split = data.split("Mode=Standard");
        if (split.length == 1){
            String[] titles = data.split("Title=");
            for (int i = 1; i < titles.length; i++) {
                if (!titles[i].substring(0,20).contains("Peripheral")){
                    String[] strings = titles[i].split("E start");
                    if (strings.length == 1){
                        return new FileNameAndType("GITT", "GITT");
                    }
                    String filename = strings[0];
                    filename = filename.replace(" ", "");
                    filename = filename.replace("\r\n","");
                    return new FileNameAndType(filename, "EIS");
                }
            }
        }
        for (String s : split) {
            if (s.substring(2, 20).startsWith("Title=")) {
                if (s.substring(0, 20).contains("CV")) {
                    String filename =
                            s.substring(8, s.length() - 1).split("E start")[0];
                    filename = filename.replace(" ", "");
                    filename = filename.replace("\r\n", "");
                    return new FileNameAndType(filename, "CV");
                } else {
                    String filename =
                            s.substring(8, s.length() - 1).split("I_Levels")[0];
                    filename = filename.replace(" ", "");
                    filename = filename.replace("\r\n", "");
                    return new FileNameAndType(filename, "Cap");
                }
            }
        }
        return new FileNameAndType("","");
    }
}
