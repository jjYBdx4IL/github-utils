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
package com.github.jjYBdx4IL.utils.env;

import com.github.jjYBdx4IL.utils.WindowsUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class Env {

    private static final Logger LOG = LoggerFactory.getLogger(Env.class);

    private static List<String> getEnvPropDump() {
        List<String> lines = new ArrayList<>();
        for (Entry<Object, Object> e : System.getProperties().entrySet()) {
            lines.add(String.format("SYSPROP: %s=%s", e.getKey(), e.getValue()));
        }
        for (Entry<String, String> e : System.getenv().entrySet()) {
            lines.add(String.format("ENV: %s=%s", e.getKey(), e.getValue()));
        }
        Collections.sort(lines);
        return lines;
    }

    public static void dumpEnvToStdErr() {
        dumpEnv(System.err);
    }

    public static void dumpEnvToStdOut() {
        dumpEnv(System.out);
    }

    public static void dumpEnv(PrintStream s) {
        for (String line : getEnvPropDump()) {
            s.println(line);
        }
    }

    public static void dumpEnvInfo() {
        if (!LOG.isInfoEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.info(line);
        }
    }

    public static void dumpEnvDebug() {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.debug(line);
        }
    }

    public static void dumpEnvTrace() {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.trace(line);
        }
    }

    protected static String get(String envVar) {
        return get(envVar, null);
    }

    protected static String get(String envVar, String fallback) {
        String value = System.getenv(envVar);
        if (value == null) {
            value = fallback;
        }
        if (value == null) {
            throw new IllegalAccessError(
                    String.format("tried to access env var %s, but it is not set", envVar));
        }
        return value;
    }
    
    public static boolean isLinux() {
    	return System.getProperty("os.name").toLowerCase().startsWith("linux");
    }
    
    public static boolean isWindows() {
    	return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    public static void assertWindows() {
		if (!isWindows()) {
			throw new RuntimeException("function not implemented for this platform");
		}
    }
    
	public static File getDesktopDir() {
		assertWindows();

		return new File(WindowsUtils.getCurrentUserDesktopPath());
	}

}
