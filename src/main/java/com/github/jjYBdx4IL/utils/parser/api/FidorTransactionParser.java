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
package com.github.jjYBdx4IL.utils.parser.api;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.parser.csv.FidorTransactionDTO;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public interface FidorTransactionParser {
    public List<FidorTransactionDTO> parse(Reader inputReader) throws IOException;
    public void parse(Reader inputReader, FidorTransactionHandler handler) throws IOException;
}
