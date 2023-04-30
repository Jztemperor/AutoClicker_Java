import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AutoClicker extends JFrame implements ActionListener {
    private boolean clicking;
    private JButton startButton, stopButton;
    private JTextField delayField;
    private JLabel statusLabel;
    private JLabel instructionLabel;
    private int delay;
    private Robot robot;

    public AutoClicker(){
        super("AutoClicker");
        initGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startButton){
            startClicking();
        }else if(e.getSource() == stopButton){
            stopClicking();
        }
    }

    public void initGUI(){
        setLayout(new FlowLayout());
        setResizable(false);

        delayField = new JTextField(5);
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        statusLabel = new JLabel("Auto Clicker is not running.");
        instructionLabel = new JLabel("You can stop the clicker with the ESC key.");


        add(new JLabel("Delay (ms): "));
        add(delayField);
        add(startButton);
        add(stopButton);
        add(statusLabel);
        add(instructionLabel);

        startButton.addActionListener(this);
        stopButton.addActionListener(this);

        setupHotKey();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,120);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupHotKey(){
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                if(nativeEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE){
                    stopClicking();
                }
            }
        });
    }

    private void startClicking(){
        if(!clicking) {
            clicking = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            statusLabel.setText("AutoClicker is running.");
            try {
                delay = Integer.parseInt(delayField.getText());
            } catch (NumberFormatException nEx) {
                delay = 100;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clicking){
                        try{
                            initRobot();
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            System.out.println("Clicking action was interrupted!");

                        }
                    }
                }
            }).start();
        }
    }

    private void stopClicking(){
        clicking = false;
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        statusLabel.setText("AutoClicker is not running.");
    }

    private void initRobot(){
        try{
            robot = new Robot();
        }catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
