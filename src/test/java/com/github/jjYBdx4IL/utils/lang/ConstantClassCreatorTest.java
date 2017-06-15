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
import static com.github.jjYBdx4IL.utils.lang.ConstantClassCreator.*;

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ConstantClassCreatorTest {

    @SuppressWarnings("unused")
    private final static Logger log = Logger.getLogger(ConstantClassCreatorTest.class.getName());
    private final static File tempDir = FileUtil.createMavenTestDir(ConstantClassCreatorTest.class);

    @Before
    public void before() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Test
    public void testMain() throws FileNotFoundException, IOException {
        String classname = "test.Out";
        String sysPropName = ConstantClassCreatorTest.class.getName() + ".key";
        String sysPropValue = ConstantClassCreatorTest.class.getName() + ".value";
        System.setProperty(sysPropName, sysPropValue);
        main(new String[] { "--" + OPTNAME_OUTPUT_DIRECTORY, tempDir.getAbsolutePath(), "--" + OPTNAME_OUTPUT_CLASSNAME,
                classname, "--" + OPTNAME_SYSPROP, sysPropName, "--" + OPTNAME_SYSPROP, sysPropName + ".2" });

        File outputFileName = new File(tempDir, "test" + File.separator + "Out.java");
        @SuppressWarnings("deprecation")
        String content = IOUtils.toString(new FileInputStream(outputFileName));
        assertTrue(content, content.contains(String.format("\"%s\"", sysPropValue)));
        assertTrue(content, content.contains("null"));
        assertTrue(content, !content.contains("\"null\""));
    }

}
