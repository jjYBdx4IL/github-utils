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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usage: <pre>new ClipBoardListener() {...}.start();</pre>
 * 
 * @author jjYBdx4IL
 */
public abstract class ClipBoardListener implements ClipboardOwner {

    private static final Logger LOG = LoggerFactory.getLogger(ClipBoardListener.class);

    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

    public ClipBoardListener() {
    }
    
    @Override
    public void lostOwnership(Clipboard c, Transferable t) {

        boolean doneWaiting = false;
        Transferable contents = null;
        while (!doneWaiting) {
            try {
                contents = sysClip.getContents(this);
                doneWaiting = true;
            } catch (IllegalStateException ex) {
                try {
                    Thread.sleep(250L);
                } catch (InterruptedException ex1) {
                    LOG.error("", ex1);
                }
            }
        }
        try {
            processClipBoard(contents, c);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }

    public void start() {
        takeOwnership("");
    }
    
    void takeOwnership(final String text) {
        Transferable t = new Transferable() {

            final String data = text;

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.stringFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.stringFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return data;
            }
        };
        sysClip.setContents(t, this);
    }

    public void processClipBoard(Transferable t, Clipboard c) { //your implementation
        String tempText;
        Transferable trans = t;

        try {
            if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                tempText = (String) trans.getTransferData(DataFlavor.stringFlavor);
                LOG.debug(tempText);
                final String newText = onContentChange(tempText);
                takeOwnership(newText);
            }
            // else { TODO } FIXME: we don't call takeOwnership here...
        } catch (UnsupportedFlavorException | IOException e) {
            LOG.error("", e);
        }
    }

    public abstract String onContentChange(String newTextContent);

}
