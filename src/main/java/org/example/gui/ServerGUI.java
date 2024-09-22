package org.example.gui;

import org.example.core.ServerListenerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServerGUI {

    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JLabel connectionCountLabel;
    private JList<String> activeUsersList;  // Danh sách các địa chỉ IP của các kết nối
    private DefaultListModel<String> listModel;  // Model để quản lý danh sách các kết nối

    private JTextField inputField;  // Ô nhập
    private JButton addButton;  // Nút thêm
    private DefaultListModel<String> rightListModel;  // Model cho danh sách bên phải
    private JList<String> blackList;  // Danh sách bên phải

    public ServerGUI(ServerListenerThread serverListener) {

        frame = new JFrame("HTTP Server Control Panel");
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        connectionCountLabel = new JLabel("Connections: 0");  // Bắt đầu với 0 kết nối
        inputField = new JTextField(15); // Ô nhập và nút thêm
        addButton = new JButton("Add on blacklist");

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

        // Thêm ActionListener cho nút thêm
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = inputField.getText();
                if (!inputText.isEmpty()) {
                    rightListModel.addElement(inputText);  // Thêm vào danh sách bên phải
                    inputField.setText("");  // Xóa ô nhập
                    Set<String> blackList = ServerListenerThread.setBlackList;
                    blackList.add(inputText);
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
        SwingUtilities.invokeLater(() -> connectionCountLabel.setText("Connections: " + count));
    }

    // Cập nhật danh sách các địa chỉ IP và thời gian
    public void GUIaddActiveUser(String ipandtime) {
        SwingUtilities.invokeLater(() -> listModel.addElement(ipandtime));
    }


}
