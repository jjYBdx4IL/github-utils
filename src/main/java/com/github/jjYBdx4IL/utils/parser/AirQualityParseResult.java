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
package com.github.jjYBdx4IL.utils.parser;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.Calendar;

public class AirQualityParseResult extends ParseResult {

    private final String value;
    private final AirQualityType type;
    private final String desc;
    private final Calendar time;

    AirQualityParseResult(String id, String value, AirQualityType type, String desc, Calendar time) {
        super(id);
        this.value = value;
        this.type = type;
        this.desc = desc;
        this.time = (Calendar) time.clone();
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the type
     */
    public AirQualityType getType() {
        return type;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @return the time
     */
    public Calendar getTime() {
        return (Calendar) time.clone();
    }
}
