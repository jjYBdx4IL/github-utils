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
package com.github.jjYBdx4IL.utils.parser.csv;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FidorTransactionParserImplTest {

    private final static Logger log = Logger.getLogger(FidorTransactionParserImplTest.class.getName());

    @Test
    public void testParse() throws Exception {
        List<FidorTransactionDTO> transactions = new FidorTransactionParserImpl()
                .parse(new InputStreamReader(
                        FidorTransactionParserImplTest.class.getResourceAsStream("Fidorpay-Transaktionen.csv"),
                        Charset.forName("ISO-8859-15")));

        assertEquals(11, transactions.size());

        // first data row
        log.info(transactions.get(0));
        assertEquals("Habenzinsen (Nr. T2099662)", transactions.get(0).getDescription1());
        assertEquals("", transactions.get(0).getDescription2());
        assertEquals("2.41", transactions.get(0).getValue().toString());

        // third data row
        log.info(transactions.get(2));
        assertEquals("Solidaritätszuschlag (Nr. T2098898)", transactions.get(2).getDescription1());
        assertEquals("-0.03", transactions.get(2).getValue().toString());

        // last data row
        log.info(transactions.get(10));
        assertEquals("Desc2 Test", transactions.get(10).getDescription2());
        assertEquals("2.6", transactions.get(10).getValue().toString());
    }

}
