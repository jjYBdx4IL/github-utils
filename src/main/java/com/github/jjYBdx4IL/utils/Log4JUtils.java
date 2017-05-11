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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class uses reflection to access log4j and thereby avoids any explicit log4j dependencies. The contained methods
 * silently fall back to noop if log4j cannot be found on the class path.
 *
 * @author jjYBdx4IL
 */
public class Log4JUtils {

    public static final String DEFAULT_LOG_PATTERN = "%d{ISO8601} %-5p [%-11t] [%-50c{4}] %L %m%n";
    public static final String PATTERN_LAYOUT_CLASS_NAME = "org.apache.log4j.PatternLayout";
    public static final String LAYOUT_CLASS_NAME = "org.apache.log4j.Layout";
    public static final String LOG_MGR_CLASS_NAME = "org.apache.log4j.LogManager";
    public static final String LOGGER_CLASS_NAME = "org.apache.log4j.Logger";
    public static final String FILE_APPENDER_CLASS_NAME = "org.apache.log4j.FileAppender";
    public static final String APPENDER_IFACE_NAME = "org.apache.log4j.Appender";

    /**
     * Convenience method for {@link #addFileAppender(String,String)}.
     * Uses {@link #DEFAULT_LOG_PATTERN}.
     * 
     * @param filename 
     */
    public static void addFileAppender(String filename) {
        addFileAppender(filename, DEFAULT_LOG_PATTERN);
    }

    /**
     * Beware! This method adds the appender to the root looger. If there are other logger elements specified in your
     * log4j configuration (file), you need to set their additivity attribute to true.
     *
     * @param filename
     * @param logPattern
     */
    public static void addFileAppender(String filename, String logPattern) {
        // check if log4j is on the class path:
        Class loggerClass;
        try {
            loggerClass = Class.forName(LOGGER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            return;
        }
        try {
            Class patternLayoutClass = Class.forName(PATTERN_LAYOUT_CLASS_NAME);
            //PatternLayout patternLayout = new PatternLayout(logPattern);
            Object patternLayout = patternLayoutClass
                    .getConstructor(String.class)
                    .newInstance(logPattern);
            //FileAppender appender = new FileAppender(patternLayout, logFile.getAbsolutePath(), true);
            Class fileAppenderClass = Class.forName(FILE_APPENDER_CLASS_NAME);
            Class layoutClass = Class.forName(LAYOUT_CLASS_NAME);
            Object appender = fileAppenderClass
                    .getConstructor(layoutClass, String.class, boolean.class)
                    .newInstance(patternLayout, filename, true);
            // configure the appender here, with file location, etc
            //appender.activateOptions();
            Method activateOptionsMethod = fileAppenderClass.getMethod("activateOptions");
            activateOptionsMethod.invoke(appender);

            //org.apache.log4j.LogManager.getRootLogger().addAppender(appender);
            Class logManagerClass = Class.forName(LOG_MGR_CLASS_NAME);
            Method getRootLoggerMethod = logManagerClass.getMethod("getRootLogger");
            Object rootLogger = getRootLoggerMethod.invoke(null);
            Class appenderIface = Class.forName(APPENDER_IFACE_NAME);
            Method addAppenderMethod = loggerClass.getMethod("addAppender", appenderIface);
            addAppenderMethod.invoke(rootLogger, appender);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InstantiationException | NoSuchMethodException | SecurityException
                | InvocationTargetException ex) {
            throw new RuntimeException("found log4j, but it seems to be incomplete", ex);
        }
    }

    private Log4JUtils() {
    }

}
