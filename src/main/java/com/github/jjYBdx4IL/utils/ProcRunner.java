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
package com.github.jjYBdx4IL.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class to run external process.
 *
 * Always redirects stderr into stdout, has timeout control
 *
 * @author jjYBdx4IL
 */
public class ProcRunner {

    private static final long DEFAULT_TIMEOUT = 0L; // no timeout by default

    ProcessBuilder mProcessBuilder;

    List<String> mOutput = new ArrayList<String>();

    public ProcRunner(boolean includeErrorStream, List<String> command) {
        mProcessBuilder = new ProcessBuilder(command).redirectErrorStream(includeErrorStream);
    }

    public ProcRunner(boolean includeErrorStream, String... command) {
        mProcessBuilder = new ProcessBuilder(command).redirectErrorStream(includeErrorStream);
    }
    
    public ProcRunner(List<String> command) {
        this(true, command);
    }

    public ProcRunner(String... command) {
    	this(true, command);
    }

    public void setWorkDir(File directory) {
        mProcessBuilder.directory(directory);
    }

    /**
     * No timeout.
     * @return
     * @throws IOException 
     */
    public int run() throws IOException {
        return run(DEFAULT_TIMEOUT);
    }

    /**
     * 
     * @param timeout in millis
     * @return
     * @throws IOException
     */
    public int run(long timeout) throws IOException {
        final Process p = mProcessBuilder.start();
        Thread t = new Thread() {
            @Override
            public void run() {
                String line;
                mOutput.clear();
                try {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            p.getInputStream()))) {
                        while ((line = br.readLine()) != null) {
                            mOutput.add(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (t.isAlive()) {
            throw new IOException("external process not terminating.");
        }
        try {
            return p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public List<String> getOutputLines() {
        return mOutput;
    }

    public String getOutputBlob() {
        StringBuilder sb = new StringBuilder();
        for (String line : mOutput) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
