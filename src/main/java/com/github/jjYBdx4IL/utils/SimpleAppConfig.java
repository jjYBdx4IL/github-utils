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
package com.github.jjYBdx4IL.utils;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.env.Env;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple key-value String store using mapdb for more reliable persistency.
 * 
 * @author jjYBdx4IL
 * @deprecated don't use mapdb, use a mature embedded database like h2 instead
 */
@Deprecated
public class SimpleAppConfig implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleAppConfig.class);

    private static File forceDir = null; // for testing
    
    private final File dbFile;
    private final DB db;
    private final ConcurrentMap<String, String> map;

    @SuppressWarnings("unchecked")
    public SimpleAppConfig(Class<?> appClassRef) {
        dbFile = getDbFile(appClassRef);
        LOG.debug("opening config db at " + dbFile.getAbsolutePath());
        db = DBMaker
                .fileDB(dbFile)
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        map = (ConcurrentMap<String, String>) db.hashMap("map").createOrOpen();
    }

    public String get(String key) {
        return map.get(key);
    }

    public String get(String key, String defaultValue) {
        String value = map.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 
     * @param key the key
     * @param value a null value will remove the config property.
     */
    public void put(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            map.remove(key);
        } else {
            map.put(key, value);
        }
        db.commit();
    }

    /**
     * Because we commit the configuration after every update/put, this method allows for more efficient batch updates
     * because it only commits changes to disk once.
     * 
     * @param map the config map to add/store
     */
    public void putAll(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey() == null) {
                throw new NullPointerException();
            }
            if (e.getValue() == null) {
                this.map.remove(e.getKey());
            } else {
                this.map.put(e.getKey(), e.getValue());
            }
        }
        db.commit();
    }

    @Override
    public void close() {
        LOG.debug("closing config db at " + dbFile.getAbsolutePath());
        db.close();
    }
    
    private File getDbFile(Class<?> appClassRef) {
        if (forceDir != null) {
            return new File(forceDir, "mapdb3");
        }
        return new File(Env.getConfigDir(appClassRef.getName()), "mapdb3");
    }
    
    /**
     * for testing only.
     * 
     * @param forceDir the config directory to use
     */
    protected static void setForceDir(File forceDir) {
        LOG.debug("forcing config dir location to " + forceDir.getAbsolutePath());
        SimpleAppConfig.forceDir = forceDir;
    }
}
