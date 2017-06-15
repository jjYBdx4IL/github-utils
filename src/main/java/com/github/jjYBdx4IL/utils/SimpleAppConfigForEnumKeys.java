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
import java.util.HashMap;
import java.util.Map;

/**
 * A simple key-value String store using mapdb for more reliable persistency.
 *
 * @author jjYBdx4IL
 * @param <T> an enumeration type used as keys
 */
public class SimpleAppConfigForEnumKeys<T extends Enum<T>> extends SimpleAppConfig {

    public SimpleAppConfigForEnumKeys(Class<?> appClassRef) {
        super(appClassRef);
    }

    public String get(T key) {
        return get(key.name());
    }

    public String get(T key, String defaultValue) {
        return get(key.name(), defaultValue);
    }

    /**
     *
     * @param key the config key
     * @param value a null value will remove the config property.
     */
    public void put(T key, String value) {
        put(key.name(), value);
    }

    /**
     * {@inheritDoc }
     */
    public void putAllByEnum(Map<T, String> map) {
        Map<String, String> map2 = new HashMap<>();
        for (Map.Entry<T, String> e : map.entrySet()) {
            map2.put(e.getKey().name(), e.getValue());
        }
        putAll(map2);
    }
}
