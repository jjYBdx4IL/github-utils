/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.awt;

//CHECKSTYLE:OFF
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AWTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AWTUtils.class);

    public static final int POS_LEFT = 1, POS_CENTER_X = 2, POS_RIGHT = 4;
    public static final int POS_TOP = 8, POS_CENTER_Y = 16, POS_BOTTOM = 32;

    private static final int SCREEN_EDGE_DISTANCE = 20;

    /**
     *
     * @param screen
     *            -1 for all screens
     * @param message
     *            the notification's message content
     * @param position
     *            the notification's position
     * @param timeoutMS
     *            the notification's duration
     * @param closeableByUser
     *            whether the notification should be closeable by the user
     */
    public static void showPopupNotification(final int screen, final String message, final int position,
            final int timeoutMS, final boolean closeableByUser) {
        if (screen == -1) {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice[] gd = ge.getScreenDevices();
            for (int i = 0; i < gd.length; i++) {
                showPopupNotification(i, message, position, timeoutMS, closeableByUser);
            }
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);

                final JDialog dialog = new JDialog();
                dialog.setModal(false);
                dialog.setModalityType(Dialog.ModalityType.MODELESS);
                dialog.setContentPane(optionPane);
                if (!closeableByUser) {
                    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                }
                dialog.setAlwaysOnTop(true);
                dialog.setFocusableWindowState(false);
                dialog.setAutoRequestFocus(false);
                dialog.setFocusable(false);
                dialog.setUndecorated(true);
                dialog.setEnabled(false);
                dialog.setType(Window.Type.POPUP);
                dialog.pack();

                AWTUtils.setPosition(dialog, screen, position);

                if (timeoutMS > 0) {
                    @SuppressWarnings("serial")
                    Timer timer = new Timer(timeoutMS, new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            dialog.dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }

                dialog.setVisible(true);
            }
        }, "Wait4Dialog");
        t.start();
    }

    public static void setPosition(Window window, int screen, int pos) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen < 0 || screen >= gd.length) {
            throw new IllegalArgumentException("invalid screen");
        }
        final Rectangle bounds = gd[screen].getDefaultConfiguration().getBounds();
        double x;
        if ((pos & POS_LEFT) == POS_LEFT) {
            x = bounds.getX() + SCREEN_EDGE_DISTANCE;
        } else if ((pos & POS_CENTER_X) == POS_CENTER_X) {
            x = bounds.getX() + (bounds.getWidth() - window.getWidth()) / 2f;
        } else {
            x = bounds.getMaxX() - window.getWidth() - SCREEN_EDGE_DISTANCE;
        }
        double y;
        if ((pos & POS_TOP) == POS_TOP) {
            y = bounds.getY() + SCREEN_EDGE_DISTANCE;
        } else if ((pos & POS_CENTER_Y) == POS_CENTER_Y) {
            y = bounds.getY() + (bounds.getHeight() - window.getHeight()) / 2f;
        } else {
            y = bounds.getMaxY() - window.getHeight() - SCREEN_EDGE_DISTANCE;
        }
        window.setLocation((int) x, (int) y);

    }

    /**
     * Set JFrame location relative to a specific screen in a multi-screen
     * setup.
     *
     * @param screen
     *            the target screen
     * @param frame
     *            the window to position
     * @param x
     *            coordinate relative to the given screen
     * @param y
     *            coordinate relative to the given screen
     */
    public static void showOnScreen(int screen, JFrame frame, int x, int y) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen < 0 || screen >= gd.length) {
            throw new IllegalArgumentException("invalid screen");
        }
        final Rectangle bounds = gd[screen].getDefaultConfiguration().getBounds();
        frame.setLocation(bounds.x + x, bounds.y + y);
    }

    /**
     * Center JFrame position on a specific screen in a multi-screen setup.
     *
     * @param screen
     *            the screen to center the window on
     * @param frame
     *            the window to center
     */
    public static void centerOnScreen(int screen, JFrame frame) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen < 0 || screen >= gd.length) {
            throw new IllegalArgumentException("invalid screen");
        }
        final Rectangle bounds = gd[screen].getDefaultConfiguration().getBounds();
        frame.setLocation(bounds.x + (bounds.width - frame.getWidth()) / 2,
                bounds.y + (bounds.height - frame.getHeight()) / 2);
    }

    /**
     * Center JFrame position on the screen where the mouse is located.
     *
     * @param window
     *            the window to center
     */
    public static void centerOnMouseScreen(Window window) {
        GraphicsDevice gd = MouseInfo.getPointerInfo().getDevice();
        final Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        window.setLocation(bounds.x + (bounds.width - window.getWidth()) / 2,
                bounds.y + (bounds.height - window.getHeight()) / 2);
    }

    /**
     *
     * @param title
     *            the dialog title
     * @param question
     *            the confirmation dialog's question
     * @return true iff user pressed the yes button
     */
    public static boolean askForConfirmationOnMouseScreen(String title, String question) {
        JOptionPane jOptionPane = new JOptionPane(question, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog jDialog = jOptionPane.createDialog(title);
        centerOnMouseScreen(jDialog);
        jDialog.setVisible(true);

        Object selectedValue = jOptionPane.getValue();
        int dialogResult = JOptionPane.CLOSED_OPTION;
        if (selectedValue != null) {
            dialogResult = Integer.parseInt(selectedValue.toString());
        }

        switch (dialogResult) {
        case JOptionPane.YES_OPTION:
            return true;
        default:
            return false;
        }
    }

    /**
     * Display some informational message.
     *
     * @param title
     *            the dialog title
     * @param text
     *            the dialog text
     */
    public static void showInfoDialogOnMouseScreen(String title, String text) {
        JOptionPane jOptionPane = new JOptionPane(text, JOptionPane.PLAIN_MESSAGE);
        JDialog jDialog = jOptionPane.createDialog(title);
        centerOnMouseScreen(jDialog);
        jDialog.setVisible(true);
    }

    /**
     * Determine screen index for the screen the mouse pointer is currently
     * located on.
     *
     * @return the index for the array returned by
     *         GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()
     * @throws RuntimeException
     *             if no screen could be found
     */
    public static int getMousePointerScreenDeviceIndex() {
        GraphicsDevice myScreen = MouseInfo.getPointerInfo().getDevice();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allScreens = env.getScreenDevices();
        int myScreenIndex = -1;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(myScreen)) {
                myScreenIndex = i;
                break;
            }
        }
        if (myScreenIndex < 0) {
            throw new RuntimeException("no screen for mouse position found");
        }
        return myScreenIndex;
    }

    /**
     * Used for testing stuff interactively.
     */
    public static void showFrameAndWaitForCloseByUser() {
        final JFrame frame = new JFrame();
        Container container = frame.getContentPane();
        JLabel label = new JLabel("Close me to continue...");
        container.add(label);

        showFrameAndWaitForCloseByUser(frame);
    }

    /**
     * Used for testing stuff interactively.
     * 
     * @param frame
     *            the JFrame
     */
    public static void showFrameAndWaitForCloseByUser(final JFrame frame) {
        frame.pack();
        AWTUtils.centerOnMouseScreen(frame);
        final CountDownLatch latch = new CountDownLatch(1);
        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOG.trace("closing " + frame.isVisible() + " " + e.paramString());
                latch.countDown();
            }
        };
        frame.addWindowListener(listener);
        frame.setVisible(true);

        try {
            latch.await();
        } catch (InterruptedException ex) {
            LOG.warn("", ex);
        } finally {
            frame.removeWindowListener(listener);
        }
    }

    private AWTUtils() {
    }
}
