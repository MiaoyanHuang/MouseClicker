package hmy.self.clicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MouseClicker implements NativeKeyListener {

    private static Boolean clicking = Boolean.FALSE;
    private static Robot robot;
    private static JComboBox<String> speedComboBox;
    private static JButton startButton;
    private static JLabel statusLabel;
    private static Timer timer;
    private static final List<Speed> speedList = Arrays.asList(Speed.values());

    public static void main(String[] args) {
        init();
    }

    /**
     * Handle the key press event
     * @param event KeyEvent
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        if (event.getKeyCode() == NativeKeyEvent.VC_F8) {
            if (clicking) {
                stopClicking();
            } else {
                startClicking();
            }
        }
    }

    /**
     * Initialize the application
     */
    private static void init() {
        // Create frame
        JFrame frame = new JFrame("Mouse Clicker");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a panel for the title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Mouse Clicker");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Create a panel for the controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create a label and combo box for click speed selection
        JLabel speedLabel = new JLabel("Click Speed:");
        speedLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(speedLabel, gbc);

        // SpeedComboBox
        speedComboBox = new JComboBox<>(speedList.stream().map(Speed::getName).toArray(String[]::new));
        speedComboBox.setSelectedItem(Speed.DEFAULT_SPEED); // set default speed to 0.5 seconds
        speedComboBox.setFont(new Font("Serif", Font.PLAIN, 24));
        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(speedComboBox, gbc);

        // Create a button to start/stop clicking
        startButton = new JButton("Start Clicking");
        startButton.setFont(new Font("Serif", Font.PLAIN, 24));
        startButton.addActionListener(e -> {
            if (clicking) {
                stopClicking();
            } else {
                startClicking();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlPanel.add(startButton, gbc);

        // Add the control panel to the frame
        frame.add(controlPanel, BorderLayout.CENTER);

        // Create a panel for the status label
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Press F8 to start clicking");
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Initialize the robot
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(frame, "Error initializing Robot", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Make sure the frame is focused to capture key events
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        // Center the frame
        frame.setLocationRelativeTo(null);

        // Set icon
        URL imageURL = MouseClicker.class.getResource("/icon.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            frame.setIconImage(icon.getImage());
        }

        // Register a global keyboard listener
        registerGlobalKeyBoardListener();

        // Show the frame
        frame.setVisible(true);
    }

    /**
     * Register a global keyboard listener to listen for F8 key press
     */
    private static void registerGlobalKeyBoardListener() {
        // Register the native hook
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.out.println(e.getMessage());
        }
        GlobalScreen.addNativeKeyListener(new MouseClicker());
    }

    /**
     * Start clicking
     */
    private static void startClicking() {
        clicking = true;
        startButton.setText("Stop Clicking");
        statusLabel.setText("Press F8 to stop clicking");

        int speed = speedList.get(speedComboBox.getSelectedIndex()).getSpeed();
        timer = new Timer(speed, e -> {
            if (clicking) {
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
            }
        });
        timer.start();
    }

    /**
     * Stop clicking
     */
    private static void stopClicking() {
        clicking = false;
        startButton.setText("Start Clicking");
        statusLabel.setText("Press F8 to start clicking");
        if (timer != null) {
            timer.stop();
        }
    }
}
