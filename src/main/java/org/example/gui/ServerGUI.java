package org.example.gui;

import org.example.core.ServerListenerThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerGUI {

    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JLabel connectionCountLabel;
    private JList<String> activeUsersList;  // Danh sách các địa chỉ IP của các kết nối
    private DefaultListModel<String> listModel;  // Model để quản lý danh sách các kết nối

    public ServerGUI(ServerListenerThread serverListener) {

        frame = new JFrame("HTTP Server Control Panel");
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        connectionCountLabel = new JLabel("Connections: 0");  // Bắt đầu với 0 kết nối

        listModel = new DefaultListModel<>();
        activeUsersList = new JList<>(listModel);  // Tạo JList để hiển thị các địa chỉ IP

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(connectionCountLabel);
        frame.add(panel, "North");
        frame.add(new JScrollPane(logArea), "Center");
        frame.add(new JScrollPane(activeUsersList), "East");  // Đặt danh sách kết nối bên phải giao diện

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

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  // Đặt giao diện chính giữa màn hình
        frame.setVisible(true);
    }

    // Cập nhật số lượng kết nối
    public void updateConnectionCount(int count) {
        SwingUtilities.invokeLater(() -> connectionCountLabel.setText("Connections: " + count));
    }

    // Cập nhật danh sách các địa chỉ IP
    public void addActiveUser(String ipandtime) {
        SwingUtilities.invokeLater(() -> listModel.addElement(ipandtime));
    }

    // Xóa địa chỉ IP khỏi danh sách khi ngắt kết nối
    public void removeActiveUser(String ipAddress) {
        SwingUtilities.invokeLater(() -> listModel.removeElement(ipAddress));
    }
}
