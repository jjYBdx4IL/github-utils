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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jjYBdx4IL
 */
public class Surefire extends Maven {

    public final static String PROPNAME_SUREFIRE_TEST_CLASS_PATH = "surefire.test.class.path";

    public static String getSurefireTestClassPath() {
        String s = System.getProperty(PROPNAME_SUREFIRE_TEST_CLASS_PATH);
        if (s == null) {
            throw new IllegalStateException();
        }
        return s;
    }
    
    /**
     * JUnit test being run inside Eclipse without calling maven?
     * 
     * @return
     */
    public static boolean isEclipseDirectJUnit() {
        if (Maven.getMavenBasedir() == null && System.getProperty("sun.java.command").startsWith("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner ")) {
        	return true;
        }
    	return false;
    }
    
    /**
     * <b>Single</b> JUnit test being run inside Eclipse without calling maven?
     * 
     * @return
     */
    public static boolean isEclipseDirectSingleJUnit() {
        if (System.getProperty("basedir") == null
      			&& System.getProperty("sun.java.command").startsWith("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner ")
      			&& System.getProperty("sun.java.command").contains(" -test ")) {
        	return true;
        }
    	return false;
    }
    
    public static String getMavenBasedir() {
    	if (isEclipseDirectJUnit()) {
    		return System.getProperty("user.dir");
    	}
        return Maven.getMavenBasedir();
    }

    /**
     * Some junit tests might require user interaction. You may use
     * this function together with a junit assumption to prevent running those tests in junit batch runs.
     * 
     * @return
     */
    public static boolean isSingleTextExecution() {
        return isEclipseDirectSingleJUnit()
                || Maven.getMavenBasedir() != null && System.getProperty("test", "").contains("#");
    }

    public static File getMavenTargetDir() {
        return new File(getMavenBasedir(), REL_TGT_DIR);
    }

    public static File getTempDirForClass(Class<?> clazz) throws IOException {
        File dir = new File(getMavenTargetDir(), clazz.getCanonicalName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            throw new IOException("failed to create directory: " + dir.getAbsolutePath());
        }
        return dir;
    }

    /**
     * Same as {@link #getTempDirForClass(Class)} but wraps the IOException inside a RuntimeException.
     * 
     * @param clazz
     * @return
     */
    public static File getTempDirForClassRT(Class<?> clazz) {
        try {
            return getTempDirForClass(clazz);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
