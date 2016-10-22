/*
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.awt;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AWTUtils {

    /**
     * Set JFrame location relative to a specific screen in a multi-screen setup.
     *
     * @param screen
     * @param frame
     * @param x coordinate relative to the given screen
     * @param y coordinate relative to the given screen
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
     * @param frame
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
     * @param question
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
     * @param text
     */
    public static void showInfoDialogOnMouseScreen(String title, String text) {
        JOptionPane jOptionPane = new JOptionPane(text, JOptionPane.PLAIN_MESSAGE);
        JDialog jDialog = jOptionPane.createDialog(title);
        centerOnMouseScreen(jDialog);
        jDialog.setVisible(true);
    }

    /**
     * Determine screen index for the screen the mouse pointer is currently located on.
     *
     * @return the index for the array returned by
     * GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()
     * @throws RuntimeException if no screen could be found
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

    private AWTUtils() {
    }
}
