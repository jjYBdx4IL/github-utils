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

//CHECKSTYLE:OFF
import static com.github.jjYBdx4IL.utils.parser.BerlinAirQualityParser.ID_PM10_VERKEHR;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BerlinAirQualityParserTest {

    private BerlinAirQualityParser parser;

    @Before
    public void before() {
        parser = new BerlinAirQualityParser();
    }

    @Test
    public void testParseValueLive() throws IOException, ParseException {
        AirQualityParseResult result = parser.fetch().parse().getResult(ID_PM10_VERKEHR);
        Integer iValue = Integer.parseInt(result.getValue());
        assertTrue(iValue > 0);
        assertTrue(result.getDesc().contains("Verkehrsmessstelle"));
    }

    @Test
    public void testParseValueBerlin1Txt() throws IOException, ParseException {
        @SuppressWarnings("deprecation")
        String source = IOUtils.toString(getClass().getResourceAsStream("berlin1.txt"));
        AirQualityParseResult result = parser.setSourceDoc(source).parse().getResult(ID_PM10_VERKEHR);
        assertEquals(24, Integer.parseInt(result.getValue()));

        // 01.07.2014 - 11:00 Uhr MESZ
        assertEquals(2014, result.getTime().get(Calendar.YEAR));
        assertEquals(6, result.getTime().get(Calendar.MONTH));
        assertEquals(1, result.getTime().get(Calendar.DAY_OF_MONTH));
        assertEquals(11, result.getTime().get(Calendar.HOUR));
        assertEquals(0, result.getTime().get(Calendar.MINUTE));

        assertTrue(result.getDesc().contains("Verkehrsmessstelle"));

        assertEquals("PM10", result.getType().toString());
    }

    @Test
    public void testParseValueBerlin2Txt() throws IOException, ParseException {
        @SuppressWarnings("deprecation")
        String source = IOUtils.toString(getClass().getResourceAsStream("berlin2.txt"));
        AirQualityParseResult result = parser.setSourceDoc(source).parse().getResult(ID_PM10_VERKEHR);
        assertEquals(21, Integer.parseInt(result.getValue()));
        assertTrue(result.getDesc().contains("Verkehrsmessstelle"));
    }
}
