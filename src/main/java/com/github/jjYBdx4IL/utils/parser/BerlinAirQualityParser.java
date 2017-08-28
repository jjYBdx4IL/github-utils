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
package com.github.jjYBdx4IL.utils.parser;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.URLDocParser;

//CHECKSTYLE:OFF
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BerlinAirQualityParser extends URLDocParser<AirQualityParseResult> {

    public static final String ID_PM10_VERKEHR = "PM10_VERKEHR";

    public BerlinAirQualityParser() {
        super("http://www.met.fu-berlin.de/senum/messwerte/aktuell.txt",
                "http://www.stadtentwicklung.berlin.de/umwelt/luftqualitaet/de/messnetz/aktuelle_werte.shtml");
    }

    @SuppressWarnings("unused")
	@Override
    public URLDocParser<AirQualityParseResult> parse() throws ParseException {
        Pattern p;
        Matcher m;
        List<AirQualityParseResult> result = new ArrayList<>();
        String s = getSourceDoc();

        p = Pattern.compile("^\\s*Datum : (\\d+)\\.(\\d+)\\.(\\d+) - (\\d+):(\\d+) Uhr MES?Z",
                DEFAULT_PATTERN_COMPILE_OPTIONS);
        m = p.matcher(s);
        if (!m.find()) {
            throw new ParseException();
        }
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        c.clear();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(2))-1, Integer.parseInt(m.group(1)),
                Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), 0);

        p = Pattern.compile("Feinstaub PM10.*?\\b(\\d+) an der (Verkehrsmessstelle .+?) und.*?Stickstoffdioxid NO2",
                DEFAULT_PATTERN_COMPILE_OPTIONS);
        m = p.matcher(s);
        if (!m.find()) {
            throw new ParseException();
        }

        addResult(new AirQualityParseResult(ID_PM10_VERKEHR, m.group(1), AirQualityType.PM10, m.group(2), c));

        return this;
    }


}
