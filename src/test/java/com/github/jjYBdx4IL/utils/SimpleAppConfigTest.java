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

import com.github.jjYBdx4IL.test.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleAppConfigTest {

    private static File TEMP_DIR = FileUtil.createMavenTestDir(SimpleAppConfigTest.class);
    
    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
        SimpleAppConfig.setForceDir(TEMP_DIR);
    }
    
    @Test
    public void testSameSession() {
        try (SimpleAppConfig cfg = new SimpleAppConfig(SimpleAppConfigTest.class)) {
            assertNull(cfg.get("not existing key"));
            assertNull(cfg.get("not existing key", null));
            assertEquals("def123", cfg.get("not existing key", "def123"));

            cfg.put("1", "ONE");
            assertEquals("ONE", cfg.get("1"));
            cfg.put("1", null);
            assertNull(cfg.get("1"));

            Map<String, String> map = new HashMap<>();
            map.put("1", null);
            map.put("2", null);
            map.put("3", null);
            cfg.putAll(map);
            assertNull(cfg.get("1"));
            assertNull(cfg.get("2"));
            assertNull(cfg.get("3"));

            map.put("1", "ONE");
            map.put("2", "TWO");
            map.put("3", "THREE");
            cfg.putAll(map);
            assertEquals("ONE", cfg.get("1"));
            assertEquals("TWO", cfg.get("2"));
            assertEquals("THREE", cfg.get("3"));
        }
    }

    @Test
    public void testSeparateSessions() {
        try (SimpleAppConfig cfg = new SimpleAppConfig(SimpleAppConfigTest.class)) {
            cfg.put("1", "ONE");
        }
        try (SimpleAppConfig cfg = new SimpleAppConfig(SimpleAppConfigTest.class)) {
            assertEquals("ONE", cfg.get("1"));
        }
        try (SimpleAppConfig cfg = new SimpleAppConfig(SimpleAppConfigTest.class)) {
            cfg.put("1", "TWO");
        }
        try (SimpleAppConfig cfg = new SimpleAppConfig(SimpleAppConfigTest.class)) {
            assertEquals("TWO", cfg.get("1"));
        }
    }
}
