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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjYBdx4IL
 */
public class Desktop {

    private static final Logger log = LoggerFactory.getLogger(Desktop.class);

    public static boolean browse(URI uri) {

        if (browseDESKTOP(uri)) return true;

        if (openSystemSpecific(uri.toString())) return true;

        log.warn(String.format("failed to browse %s", uri));

        return false;
    }


    public static boolean open(File file) {

        if (openDESKTOP(file)) return true;

        if (openSystemSpecific(file.getPath())) return true;

        log.warn(String.format("failed to open %s", file.getAbsolutePath()));

        return false;
    }


    public static boolean edit(File file) {

        if (editDESKTOP(file)) return true;

        if (openSystemSpecific(file.getPath())) return true;

        log.warn(String.format("failed to edit %s", file.getAbsolutePath()));

        return false;
    }


    private static boolean openSystemSpecific(String what) {

        if (SystemUtils.IS_OS_LINUX) {
            if (isXDG()) {
                if (runCommand("xdg-open", "%s", what)) return true;
            }
            if (isKDE()) {
                if (runCommand("kde-open", "%s", what)) return true;
            }
            if (isGNOME()) {
                if (runCommand("gnome-open", "%s", what)) return true;
            }
            if (runCommand("kde-open", "%s", what)) return true;
            if (runCommand("gnome-open", "%s", what)) return true;
        }

        if (SystemUtils.IS_OS_MAC) {
            if (runCommand("open", "%s", what)) return true;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            if (runCommand("explorer", "%s", what)) return true;
        }

        return false;
    }


    private static boolean browseDESKTOP(URI uri) {

        try {
            if (!java.awt.Desktop.isDesktopSupported()) {
                log.debug("Platform is not supported.");
                return false;
            }

            if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                log.debug("BROWSE is not supported.");
                return false;
            }

            log.info("Trying to use Desktop.getDesktop().browse() with " + uri.toString());
            java.awt.Desktop.getDesktop().browse(uri);

            return true;
        } catch (Throwable t) {
            log.error("Error using desktop browse.", t);
            return false;
        }
    }


    private static boolean openDESKTOP(File file) {
        try {
            if (!java.awt.Desktop.isDesktopSupported()) {
                log.debug("Platform is not supported.");
                return false;
            }

            if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                log.debug("OPEN is not supported.");
                return false;
            }

            log.info("Trying to use Desktop.getDesktop().open() with " + file.toString());
            java.awt.Desktop.getDesktop().open(file);

            return true;
        } catch (Throwable t) {
            log.error("Error using desktop open.", t);
            return false;
        }
    }


    private static boolean editDESKTOP(File file) {
        try {
            if (!java.awt.Desktop.isDesktopSupported()) {
                log.debug("Platform is not supported.");
                return false;
            }

            if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.EDIT)) {
                log.debug("EDIT is not supported.");
                return false;
            }

            log.info("Trying to use Desktop.getDesktop().edit() with " + file);
            java.awt.Desktop.getDesktop().edit(file);

            return true;
        } catch (Throwable t) {
            log.error("Error using desktop edit.", t);
            return false;
        }
    }


    private static boolean runCommand(String command, String args, String file) {

        log.info("Trying to exec:\n   cmd = " + command + "\n   args = " + args + "\n   %s = " + file);

        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) return false;

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    log.error("Process ended immediately.");
                    return false;
                } else {
                    log.error("Process crashed.");
                    return false;
                }
            } catch (IllegalThreadStateException itse) {
                log.error("Process is running.");
                return true;
            }
        } catch (IOException e) {
            log.error("Error running command.", e);
            return false;
        }
    }


    private static String[] prepareCommand(String command, String args, String file) {

        List<String> parts = new ArrayList<String>();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing

                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    private static boolean isXDG() {
        String xdgSessionId = System.getenv("XDG_SESSION_ID");
        return xdgSessionId != null && !xdgSessionId.isEmpty();
    }

    private static boolean isGNOME() {
        String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("gnome");
    }

    private static boolean isKDE() {
        String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("kde");
    }
}