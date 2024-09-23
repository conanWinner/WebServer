package org.example.gui;

import org.example.core.ServerListenerThread;
import org.example.util.FormatTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class ServerGUI {
    private Set<String> setBlackList = ServerListenerThread.setBlackList;

    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JLabel connectionCountLabel;
    private JList<String> activeUsersList;  // Danh sách các địa chỉ IP của các kết nối
    private DefaultListModel<String> listModel;  // Model để quản lý danh sách các kết nối

    private JTextField inputField;  // Ô nhập
    private JButton addButton;  // Nút thêm
    private JButton removeButton;  // Nút xóa
    private DefaultListModel<String> rightListModel;  // Model cho danh sách bên phải
    private JList<String> blackList;  // Danh sách bên phải

    public ServerGUI(ServerListenerThread serverListener) {

        frame = new JFrame("HTTP Server Control Panel");
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        connectionCountLabel = new JLabel("Connected: 0");  // Bắt đầu với 0 kết nối
        inputField = new JTextField(15); // Ô nhập và nút thêm
        addButton = new JButton("Add on blacklist");
        removeButton = new JButton("Remove");

        listModel = new DefaultListModel<>();
        activeUsersList = new JList<>(listModel);  // Tạo JList để hiển thị các địa chỉ IP

        rightListModel = new DefaultListModel<>();
        blackList = new JList<>(rightListModel);  // Tạo JList cho danh sách bên phải


        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(connectionCountLabel);
        panel.add(Box.createRigidArea(new Dimension(15, 0)));
        panel.add(inputField);
        panel.add(addButton);
        panel.add(removeButton);


        frame.add(panel, "North");
        frame.add(new JScrollPane(logArea), "West");
        frame.add(new JScrollPane(activeUsersList), "Center");
        frame.add(new JScrollPane(blackList), "East");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    logArea.append("Starting server...\n");
                    serverListener.start();
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }).start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.append("Stopping server...\n");
                serverListener.interrupt();  // Dừng luồng
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = inputField.getText();
                if (!inputText.isEmpty()) {
                    rightListModel.addElement(inputText + "   -   " + new FormatTime().getFormattedTime());
                    inputField.setText("");
                    setBlackList.add(inputText);
                }
            }
        });

        // Thêm ActionListener cho nút delete
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = blackList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedValue = rightListModel.getElementAt(selectedIndex);
                    rightListModel.remove(selectedIndex);

                    System.out.println("selectedVAalue: " + selectedValue);
                    String value = cutStringBySpace(selectedValue);

                    setBlackList.remove(value);  // Xóa khỏi danh sách blacklist
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an item to delete.");
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  // Đặt giao diện chính giữa màn hình
        frame.setVisible(true);
    }

    // Cập nhật số lượng kết nối
    public void GUIupdateConnectionCount(int count) {
        SwingUtilities.invokeLater(() -> connectionCountLabel.setText("Connected: " + count));
    }

    // Cập nhật danh sách các địa chỉ IP và thời gian
    public void GUIaddActiveUser(String ipandtime) {
        SwingUtilities.invokeLater(() -> listModel.addElement(ipandtime));
    }

    // Cắt chuỗi
    private String cutStringBySpace(String str){
        String result = str.split(" ")[0].trim();
        return result;
    }


}
