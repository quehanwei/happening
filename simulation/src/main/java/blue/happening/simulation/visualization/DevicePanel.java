package blue.happening.simulation.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.MeshHandler;
import blue.happening.mesh.RemoteDevice;
import blue.happening.mesh.statistics.Stat;
import blue.happening.mesh.statistics.StatsResult;
import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.entities.LogItem;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 1000;
    private static final int TIME_WINDOW_SIZE = 25;
    private TitledBorder deviceTitle;
    private TitledBorder demoTitle;
    private JPanel devicePanel;
    private JLabel statsOgmIn, statsOgmOut, statsUcmIn, statsUcmOut;
    private JLabel deviceLabel;
    private JTable table;
    private JPanel tablePanel;
    private JTable ogmLogTable;
    private JTable ucmLogTable;
    private JButton sendButton;
    private JButton pauseButton;
    private JButton loopButton;
    private JButton nextButton;
    private JButton demoButton;
    private JPanel logTablePanel;
    private JButton disableButton;
    private JSlider packageDropSlider;
    private JSlider packageDelaySlider;
    private JSlider ogmIntervalSlider;
    private JSlider purgeIntervalSlider;
    private JSlider deviceExpirationSlider;
    private JSlider initialTtlSlider;
    private JSlider hopPenaltySlider;
    private List<RemoteDevice> selectedDevices;
    private boolean messageCount;

    private Device device;
    private NetworkStatsPanel ogmNetworkStats;
    private NetworkStatsPanel ucmNetworkStats;

    DevicePanel() {
        messageCount = true;
        selectedDevices = new ArrayList<>();

        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JTabbedPane tabbedPane = new JTabbedPane();

        // Button Panel

        deviceLabel = new JLabel("Current device", JLabel.LEFT);
        sendButton = new JButton("Send message");
        sendButton.setEnabled(false);

        final JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        btnPanel.add(deviceLabel);
        btnPanel.add(sendButton);

        // Slider Panel

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setOpaque(false);

        final JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setMaximumSize(new Dimension((int) getSize().getWidth(), 64));
        controlPanel.setOpaque(false);
        loopButton = new JButton("Loop");
        pauseButton = new JButton("Pause");
        nextButton = new JButton("Next");
        demoButton = new JButton("Select Demo");
        controlPanel.add(loopButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(demoButton);

        demoTitle = BorderFactory.createTitledBorder(
                HappeningDemo.getPattern() == null ? "Demo" : HappeningDemo.getPattern());
        controlPanel.setBorder(demoTitle);
        sliderPanel.add(controlPanel);

        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        devicePanel.setOpaque(false);

        deviceTitle = BorderFactory.createTitledBorder("Device");
        devicePanel.setBorder(deviceTitle);
        devicePanel.setVisible(false);

        JPanel deviceButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deviceButtonWrapper.setBorder(BorderFactory.createEmptyBorder());
        deviceButtonWrapper.setBackground(new Color(0, 0, 0, 0));
        disableButton = new JButton("Disable");
        deviceButtonWrapper.add(disableButton);
        deviceButtonWrapper.setMaximumSize(new Dimension((int) getSize().getWidth(), 32));
        devicePanel.add(deviceButtonWrapper);

        packageDropSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        packageDropSlider.setMajorTickSpacing(10);
        packageDropSlider.setMinorTickSpacing(10);
        packageDropSlider.setPaintTicks(true);
        packageDropSlider.setPaintLabels(true);
        packageDropSlider.setOpaque(false);
        packageDropSlider.setEnabled(false);
        devicePanel.add(new JLabel("Message Loss"));
        devicePanel.add(packageDropSlider);

        packageDelaySlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
        packageDelaySlider.setMajorTickSpacing(100);
        packageDelaySlider.setMinorTickSpacing(100);
        packageDelaySlider.setPaintTicks(true);
        packageDelaySlider.setPaintLabels(true);
        packageDelaySlider.setOpaque(false);
        packageDelaySlider.setEnabled(false);
        devicePanel.add(new JLabel("Message Delay"));
        devicePanel.add(packageDelaySlider);

        sliderPanel.add(devicePanel);

        final JPanel globalPanel = new JPanel();
        globalPanel.setLayout(new BoxLayout(globalPanel, BoxLayout.Y_AXIS));
        globalPanel.setBorder(BorderFactory.createTitledBorder("Global Settings"));
        globalPanel.setOpaque(false);

        ogmIntervalSlider = new JSlider(JSlider.HORIZONTAL, 0, 16, MeshHandler.OGM_INTERVAL);
        ogmIntervalSlider.setMajorTickSpacing(4);
        ogmIntervalSlider.setMinorTickSpacing(1);
        ogmIntervalSlider.setPaintTicks(true);
        ogmIntervalSlider.setPaintLabels(true);
        globalPanel.add(new JLabel("OGM Interval", JLabel.CENTER));
        globalPanel.add(ogmIntervalSlider);

        purgeIntervalSlider = new JSlider(JSlider.HORIZONTAL, 0, 64, MeshHandler.PURGE_INTERVAL);
        purgeIntervalSlider.setMajorTickSpacing(16);
        purgeIntervalSlider.setMinorTickSpacing(4);
        purgeIntervalSlider.setPaintTicks(true);
        purgeIntervalSlider.setPaintLabels(true);
        globalPanel.add(new JLabel("Purge Interval", JLabel.CENTER));
        globalPanel.add(purgeIntervalSlider);

        deviceExpirationSlider = new JSlider(JSlider.HORIZONTAL, 0, 64, MeshHandler.DEVICE_EXPIRATION);
        deviceExpirationSlider.setMajorTickSpacing(16);
        deviceExpirationSlider.setMinorTickSpacing(4);
        deviceExpirationSlider.setPaintTicks(true);
        deviceExpirationSlider.setPaintLabels(true);
        globalPanel.add(new JLabel("Device Expiration", JLabel.CENTER));
        globalPanel.add(deviceExpirationSlider);

        initialTtlSlider = new JSlider(JSlider.HORIZONTAL, 0, 16, MeshHandler.INITIAL_MESSAGE_TTL);
        initialTtlSlider.setMajorTickSpacing(4);
        initialTtlSlider.setMinorTickSpacing(1);
        initialTtlSlider.setPaintTicks(true);
        initialTtlSlider.setPaintLabels(true);
        globalPanel.add(new JLabel("Initial TTL", JLabel.CENTER));
        globalPanel.add(initialTtlSlider);

        hopPenaltySlider = new JSlider(JSlider.HORIZONTAL, 0, 256, MeshHandler.HOP_PENALTY);
        hopPenaltySlider.setMajorTickSpacing(64);
        hopPenaltySlider.setMinorTickSpacing(16);
        hopPenaltySlider.setPaintTicks(true);
        hopPenaltySlider.setPaintLabels(true);
        globalPanel.add(new JLabel("Hop Penalty", JLabel.CENTER));
        globalPanel.add(hopPenaltySlider);

        sliderPanel.add(globalPanel);

        // Devices Panel

        table = new JTable();
        table.setAutoCreateRowSorter(true);

        tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(new JScrollPane(table));
        tablePanel.setVisible(false);
        tablePanel.setOpaque(false);

        final JPanel devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        devicesPanel.setOpaque(false);
        devicesPanel.add(btnPanel);
        devicesPanel.add(tablePanel);

        // Network Stats

        String[] settings = {"Number of Messages", "Size of Messages (bytes)"};

        JComboBox<String> selectStats = new JComboBox<>(settings);
        selectStats.setSelectedIndex(0);
        selectStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                messageCount = !messageCount;
                clearNetworkStats();
            }
        });

        ogmNetworkStats = new NetworkStatsPanel(TIME_WINDOW_SIZE);
        ucmNetworkStats = new NetworkStatsPanel(TIME_WINDOW_SIZE);

        statsOgmIn = new JLabel();
        statsOgmOut = new JLabel();
        statsUcmIn = new JLabel();
        statsUcmOut = new JLabel();

        final JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.add(new JLabel("Y-Axis:"));
        statsPanel.add(selectStats);

        statsPanel.add(new JLabel("OGM Stats"));
        statsPanel.add(statsOgmIn);
        statsPanel.add(statsOgmOut);
        statsPanel.add(ogmNetworkStats.getChartPanel());

        statsPanel.add(new JLabel("UCM Stats"));
        statsPanel.add(statsUcmIn);
        statsPanel.add(statsUcmOut);
        statsPanel.add(ucmNetworkStats.getChartPanel());

        statsPanel.setVisible(false);

        // Message Logging

        ogmLogTable = new JTable();
        ogmLogTable.setAutoCreateRowSorter(true);

        ucmLogTable = new JTable();
        ucmLogTable.setAutoCreateRowSorter(true);

        logTablePanel = new JPanel();
        logTablePanel.setLayout(new BoxLayout(logTablePanel, BoxLayout.Y_AXIS));
        logTablePanel.add(new JLabel("OGM Logs"));
        logTablePanel.add(new JScrollPane(ogmLogTable));
        logTablePanel.add(new JLabel("UCM Logs"));
        logTablePanel.add(new JScrollPane(ucmLogTable));
        logTablePanel.setVisible(false);
        logTablePanel.setOpaque(false);

        final JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setOpaque(false);
        logPanel.add(logTablePanel);

        // Tabs

        tabbedPane.addTab("Control", sliderPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Devices", devicesPanel);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.addTab("Traffic", statsPanel);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        tabbedPane.addTab("Logs", logPanel);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        add(tabbedPane);

        setVisible(true);

        packageDropSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (device != null) {
                    device.setMessageLoss((float) source.getValue() / 100);
                }
            }
        });

        packageDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (device != null) {
                    device.setMessageDelay(source.getValue());
                }
            }
        });

        ogmIntervalSlider.addChangeListener(new ChangeListener() {
            private boolean iPaused = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    HappeningDemo.setPause(true);
                    iPaused = true;
                } else {
                    MeshHandler.OGM_INTERVAL = Math.max(1, source.getValue());
                    if (iPaused) {
                        HappeningDemo.setPause(false);
                        iPaused = false;
                    }
                }
            }
        });

        purgeIntervalSlider.addChangeListener(new ChangeListener() {
            private boolean iPaused = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    HappeningDemo.setPause(true);
                    iPaused = true;
                } else {
                    MeshHandler.PURGE_INTERVAL = Math.max(1, source.getValue());
                    if (iPaused) {
                        HappeningDemo.setPause(false);
                        iPaused = false;
                    }
                }
            }
        });

        deviceExpirationSlider.addChangeListener(new ChangeListener() {
            private boolean iPaused = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    HappeningDemo.setPause(true);
                    iPaused = true;
                } else {
                    MeshHandler.DEVICE_EXPIRATION = source.getValue();
                    if (iPaused) {
                        HappeningDemo.setPause(false);
                        iPaused = false;
                    }
                }
            }
        });

        initialTtlSlider.addChangeListener(new ChangeListener() {
            private boolean iPaused = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    HappeningDemo.setPause(true);
                    iPaused = true;
                } else {
                    MeshHandler.INITIAL_MESSAGE_TTL = source.getValue();
                    if (iPaused) {
                        HappeningDemo.setPause(false);
                        iPaused = false;
                    }
                }
            }
        });

        hopPenaltySlider.addChangeListener(new ChangeListener() {
            private boolean iPaused = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    HappeningDemo.setPause(true);
                    iPaused = true;
                } else {
                    MeshHandler.HOP_PENALTY = Math.min(source.getValue(), 255);
                    if (iPaused) {
                        HappeningDemo.setPause(false);
                        iPaused = false;
                    }
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String s = (String) JOptionPane.showInputDialog(tabbedPane, "Message",
                        "Send Message", JOptionPane.PLAIN_MESSAGE, null, null, "Hallo");
                for (RemoteDevice remotedevice : selectedDevices) {
                    if (device != null) {
                        device.getMeshHandler().sendMessage(s.getBytes(), remotedevice.getUuid());
                    }
                }
            }
        });

        disableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (device != null) {
                    device.toggleEnabled();
                    disableButton.setText(device.isEnabled() ? "Disable" : "Enable");
                }
            }
        });

        loopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (HappeningDemo.isLoop()) {
                    loopButton.setText("Loop");
                    HappeningDemo.setLoop(false);
                } else {
                    loopButton.setText("Shuffle");
                    HappeningDemo.setLoop(true);
                }
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (HappeningDemo.isPaused()) {
                    pauseButton.setText("Pause");
                    HappeningDemo.setPause(false);
                } else {
                    pauseButton.setText("Play");
                    HappeningDemo.setPause(true);
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (HappeningDemo.isPaused()) {
                    pauseButton.setText("Pause");
                    HappeningDemo.setPause(false);
                }
                HappeningDemo.setInterrupt(true);
            }
        });

        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HappeningDemo.setPause(true);
                String selection = (String) JOptionPane.showInputDialog(tabbedPane, "Demo",
                        "Select Demo", JOptionPane.QUESTION_MESSAGE, null, HappeningDemo.getPatternKeys(),
                        HappeningDemo.getPatternKeys()[0]);
                HappeningDemo.setPattern(selection);
                HappeningDemo.setPause(false);
                HappeningDemo.setInterrupt(true);
            }
        });
    }

    private void updateNetworkStats(StatsResult stats) {
        Stat ogmIn = stats.getOgmIncoming();
        Stat ogmOut = stats.getOgmOutgoing();

        Stat ucmIn = stats.getUcmIncoming();
        Stat ucmOut = stats.getUcmOutgoing();

        if (messageCount) {
            ogmNetworkStats.addValues(ogmIn.getMessageCountForTs(), ogmOut.getMessageCountForTs());
            ucmNetworkStats.addValues(ucmIn.getMessageCountForTs(), ucmOut.getMessageCountForTs());
        } else {
            ogmNetworkStats.addValues(ogmIn.getMessageSizeForTs(), ogmOut.getMessageSizeForTs());
            ucmNetworkStats.addValues(ucmIn.getMessageSizeForTs(), ucmOut.getMessageSizeForTs());
        }

        statsOgmIn.setText("Incoming Traffic: " +
                ogmIn.getMessageCountForTs() + "/" + (int) ogmIn.getTotalMessageCount() +
                " (" + Math.round(ogmIn.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ogmIn.getTotalMessageSize() / 1024) + "kb)");

        statsOgmOut.setText("Outgoing Traffic: " +
                ogmOut.getMessageCountForTs() + "/" + (int) ogmOut.getTotalMessageCount() +
                " (" + Math.round(ogmOut.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ogmOut.getTotalMessageSize() / 1024) + "kb)");

        statsUcmIn.setText("Incoming Traffic: " +
                ucmIn.getMessageCountForTs() + "/" + (int) ucmIn.getTotalMessageCount() +
                " (" + Math.round(ucmIn.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ucmIn.getTotalMessageSize() / 1024) + "kb)");

        statsUcmOut.setText("Outgoing Traffic: " +
                ucmOut.getMessageCountForTs() + "/" + (int) ucmOut.getTotalMessageCount() +
                " (" + Math.round(ucmOut.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ucmOut.getTotalMessageSize() / 1024) + "kb)");
    }

    private void updateMessageLossSlider(Device device) {
        int newVal = (int) (device.getMockLayer().getMessageLoss() * 100);
        if (newVal != packageDropSlider.getValue()) {
            packageDropSlider.setValue(newVal);
            packageDropSlider.updateUI();
        }
    }

    private void updatePackageDelay(Device device) {
        int newVal = device.getMessageDelay();
        if (newVal != packageDelaySlider.getValue()) {
            packageDelaySlider.setValue(newVal);
            packageDelaySlider.updateUI();
        }
    }

    private void setNeighbourList(Device device) {
        tablePanel.setVisible(true);
        List<MeshDevice> neighbours = device.getDevices();
        DeviceNeighbourTableModel neighbourTableModel = new DeviceNeighbourTableModel(neighbours);
        table.setModel(neighbourTableModel);
        table.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
    }

    private void addNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().add(neighbour);
        neighbourTableModel.fireTableDataChanged();
    }

    private void updateNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        List<MeshDevice> neighbours = neighbourTableModel.getNeighbours();
        int indexOfExisting = neighbours.indexOf(neighbour);
        try {
            neighbours.set(indexOfExisting, neighbour);
            neighbourTableModel.fireTableRowsUpdated(indexOfExisting, indexOfExisting);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void removeNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().remove(neighbour);
        neighbourTableModel.fireTableDataChanged();
    }

    private void setOgmLog(Device device) {
        DeviceLogTableModel ogmLogTableModel = new DeviceLogTableModel(device.getOgmLog().getLogs(), device);
        ogmLogTable.setModel(ogmLogTableModel);
        ogmLogTable.setVisible(true);
        ogmLogTable.updateUI();
    }

    private void updateOgmLog(LogItem log) {
        DeviceLogTableModel ogmLogTableModel = (DeviceLogTableModel) ogmLogTable.getModel();
        if (ogmLogTableModel.getLogs().contains(log)) {
            ogmLogTableModel.getLogs().set(ogmLogTableModel.getLogs().indexOf(log), log);
        } else {
            ogmLogTableModel.getLogs().add(log);
        }
        ogmLogTableModel.fireTableDataChanged();
    }

    private void setUcmLog(Device device) {
        DeviceLogTableModel ucmLogTableModel = new DeviceLogTableModel(device.getUcmLog().getLogs(), device);
        ucmLogTable.setModel(ucmLogTableModel);
        ucmLogTable.setVisible(true);
        ucmLogTable.updateUI();
    }

    private void updateUcmLog(LogItem log) {
        DeviceLogTableModel ucmLogTableModel = (DeviceLogTableModel) ucmLogTable.getModel();
        if (ucmLogTableModel.getLogs().contains(log)) {
            ucmLogTableModel.getLogs().set(ucmLogTableModel.getLogs().indexOf(log), log);
        } else {
            ucmLogTableModel.getLogs().add(log);
        }
        ucmLogTableModel.fireTableDataChanged();

    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        setOgmLog(device);
        setUcmLog(device);
        clearNetworkStats();
        deviceLabel.setText(device.getName());
        deviceTitle.setTitle(device.getName());
        devicePanel.updateUI();
        devicePanel.setVisible(true);
        logTablePanel.setVisible(true);
        packageDropSlider.setEnabled(true);
        packageDelaySlider.setEnabled(true);
        disableButton.setText(device.isEnabled() ? "Disable" : "Enable");
    }

    private void clearNetworkStats() {
        ogmNetworkStats.clear();
        ucmNetworkStats.clear();
    }

    public void updateDevice(Device device, Device.DeviceChangedEvent event) {
        if (event != null) {
            try {
                switch (event.getType()) {
                    case NEIGHBOUR_ADDED:
                        addNeighbour((MeshDevice) event.getOptions());
                        break;
                    case NEIGHBOUR_UPDATED:
                        updateNeighbour((MeshDevice) event.getOptions());
                        break;
                    case NEIGHBOUR_REMOVED:
                        removeNeighbour((MeshDevice) event.getOptions());
                        break;
                    case NETWORK_STATS_UPDATED:
                        updateNetworkStats((StatsResult) event.getOptions());
                        break;
                    case OGM_LOG_ITEM_ADDED:
                        updateOgmLog((LogItem) event.getOptions());
                        break;
                    case UCM_LOG_ITEM_ADDED:
                        updateUcmLog((LogItem) event.getOptions());
                        break;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        } else {
            setDevice(device);
        }
    }

    private void setSelectedDevicesFromSelectedDeviceNames(List<String> selectedDeviceNames) {
        selectedDevices.clear();
        for (String name : selectedDeviceNames) {
            RemoteDevice selectedDevice = device.getMeshHandler().getRoutingTable().get(name);
            selectedDevices.add(selectedDevice);
        }
        if (selectedDevices.size() > 0) {
            sendButton.setEnabled(true);
        } else {
            sendButton.setEnabled(false);
        }
    }

    private class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            List<String> selectedDevicesNames = new ArrayList<>();
            if (!e.getValueIsAdjusting()) {
                for (int selectedRow : table.getSelectedRows()) {
                    selectedDevicesNames.add((String) table.getValueAt(selectedRow, 0));
                }
                setSelectedDevicesFromSelectedDeviceNames(selectedDevicesNames);
            }
        }
    }
}
