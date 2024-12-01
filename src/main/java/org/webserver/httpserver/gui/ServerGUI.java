package org.webserver.httpserver.gui;

import org.webserver.httpserver.config.Configuration;
import org.webserver.httpserver.config.ConfigurationManager;
import org.webserver.httpserver.core.ServerListenerThread;
import org.webserver.httpserver.util.FormatTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class ServerGUI {
    private ServerListenerThread serverListener;
    private Configuration conf;
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

    public ServerGUI(Configuration conf) {
        this.conf = conf;

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

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

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

    private void startServer() {
        new Thread(() -> {
            try {
                logArea.append("Starting server...\n");
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

                serverListener = new ServerListenerThread(conf.getServer().getListen(), conf.getServer().getWebroot(), conf.getServer().getServerName());
                serverListener.setConnectionCountCallback(this::GUIupdateConnectionCount);
                serverListener.setConnectionListCallback(this::GUIaddActiveUser);
                serverListener.start();

            } catch (Exception ex) {
                logArea.append("Failed to start server: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    private void stopServer() {
        logArea.append("Stopping server...\n");
        if (serverListener != null) {
            serverListener.stopServer();
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
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
