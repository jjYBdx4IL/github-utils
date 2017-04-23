package com.github.jjYBdx4IL.utils;

/*
 * #%L
 * github-utils
 * %%
 * Copyright (C) 2017 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.io.File;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AbstractConfigTest {

    private void assertDelete(File file) {
        if (file.exists()) {
            assertTrue(file.delete());
            assertFalse(file.exists());
        }
    }

    @Test
    public void testReadNonExistingFile() throws Exception {
        ExampleConfig config = new ExampleConfig("GithubUtilsTest");
        File file = config.getConfigFile();

        assertDelete(file);
        assertFalse(config.read());
    }

    @Test
    public void testFullCycle() throws Exception {
        ExampleConfig config = new ExampleConfig("GithubUtilsTest");
        config.configOption1 = "config value 1";
        config.configOption2 = "config value 2";
        config.write();

        ExampleConfig config2 = new ExampleConfig("GithubUtilsTest");
        assertTrue(config2.read());
        assertEquals("config value 1", config2.configOption1);
        assertEquals("CONFIG VALUE 2", config2.configOption2);
    }

}
