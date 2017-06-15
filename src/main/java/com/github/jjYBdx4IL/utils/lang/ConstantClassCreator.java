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
package com.github.jjYBdx4IL.utils.lang;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.text.WordUtils;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.ProgressCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("deprecation")
public class ConstantClassCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ConstantClassCreator.class);

    final static String OPTNAME_H = "h";
    final static String OPTNAME_HELP = "help";
    final static String OPTNAME_SYSPROP = "sysprop";
    final static String OPTNAME_OUTPUT_CLASSNAME = "output-classname";
    final static String OPTNAME_OUTPUT_DIRECTORY = "output-dir";

    public static void main(String[] args) {
        try {
            CommandLineParser parser = new GnuParser();
            Options options = new Options();
            options.addOption(OPTNAME_H, OPTNAME_HELP, false, "show help (this page)");
            options.addOption(null, OPTNAME_SYSPROP, true, "system property name(s)");
            options.addOption(null, OPTNAME_OUTPUT_CLASSNAME, true, "output classname");
            options.addOption(null, OPTNAME_OUTPUT_DIRECTORY, true, "output directory");
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(OPTNAME_H)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(ConstantClassCreator.class.getName(), options);
            } else if (line.hasOption(OPTNAME_SYSPROP)) {
                new ConstantClassCreator().run(line);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void run(CommandLine line) throws IOException, JClassAlreadyExistsException, ParseException {
        String classname = line.getOptionValue(OPTNAME_OUTPUT_CLASSNAME);
        if (classname == null || classname.isEmpty()) {
            throw new ParseException("--" + OPTNAME_OUTPUT_CLASSNAME + " required");
        }
        String[] sysPropNames = line.getOptionValues(OPTNAME_SYSPROP);
        if (sysPropNames.length == 0) {
            throw new ParseException("--" + OPTNAME_SYSPROP + " required");
        }
        String outputDirName = line.getOptionValue(OPTNAME_OUTPUT_DIRECTORY);
        if (outputDirName == null || outputDirName.isEmpty()) {
            throw new ParseException("--" + OPTNAME_OUTPUT_DIRECTORY + " required");
        }
        File outputDir = new File(outputDirName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(classname);
        for (String sysPropName : sysPropNames) {
            String value = System.getProperty(sysPropName);
            cls.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC,
                    String.class,
                    WordUtils.camelCase(sysPropName),
                    value == null ? JExpr._null() : JExpr.lit(value));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cm.build(new ProgressCodeWriter(new FileCodeWriter(outputDir), new PrintStream(baos) {
            @Override
            public void print(final String msg) {
                LOG.info("creating: " + msg);
            }
        }));
    }
}
