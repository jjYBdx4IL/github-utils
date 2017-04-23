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

import com.github.jjYBdx4IL.utils.parser.api.FidorTransactionHandler;
import com.github.jjYBdx4IL.utils.parser.api.FidorTransactionParser;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Not thread-safe.
 *
 * @author Github jjYBdx4IL Projects
 */
public class FidorTransactionParserImpl implements FidorTransactionParser {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public List<FidorTransactionDTO> parse(Reader inputReader) throws IOException {
        final List<FidorTransactionDTO> transactions = new ArrayList<>();
        parse(inputReader, new FidorTransactionHandler() {

            @Override
            public void handleTransaction(FidorTransactionDTO fidorTransactionDTO, long lineNumber) {
                transactions.add(fidorTransactionDTO);
            }
        });
        return transactions;
    }

    @Override
    public void parse(Reader inputReader, FidorTransactionHandler handler) throws IOException {
        try (CSVReader reader = new CSVReader(inputReader, ';', '"', 0)) {
            String[] line;
            long lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                if (lineNumber == 1) {
                    if (line.length != 4) {
                        throw new InvalidFormatException("first line does not have 4 columns", lineNumber);
                    }
                    if (!line[0].toLowerCase(Locale.ROOT).equals("datum")) {
                        throw new InvalidFormatException("expected \"Datum\" in first column", lineNumber);
                    }
                    if (!line[1].toLowerCase(Locale.ROOT).equals("beschreibung")) {
                        throw new InvalidFormatException("expected \"Beschreibung\" in second column", lineNumber);
                    }
                    if (!line[2].toLowerCase(Locale.ROOT).equals("beschreibung2")) {
                        throw new InvalidFormatException("expected \"Beschreibung2\" in third column", lineNumber);
                    }
                    if (!line[3].toLowerCase(Locale.ROOT).equals("wert")) {
                        throw new InvalidFormatException("expected \"Wert\" in fourth column", lineNumber);
                    }
                } else {
                    FidorTransactionDTO transaction = new FidorTransactionDTO();
                    try {
                        transaction.setDate(sdf.parse(line[0]));
                    } catch (ParseException ex) {
                        throw new InvalidFormatException("failed to parse date", lineNumber, ex);
                    }
                    transaction.setDescription1(line[1]);
                    transaction.setDescription2(line[2]);
                    try {
                        transaction.setValue(Double.valueOf(line[3].replace(',', '.')));
                    } catch (NumberFormatException ex) {
                        throw new InvalidFormatException("failed to parse value", lineNumber, ex);
                    }
                    handler.handleTransaction(transaction, lineNumber);
                }
                lineNumber++;
            }
        }
    }
}
