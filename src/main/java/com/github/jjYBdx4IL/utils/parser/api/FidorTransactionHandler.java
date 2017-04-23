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
package com.github.jjYBdx4IL.utils.parser.api;

import com.github.jjYBdx4IL.utils.parser.csv.FidorTransactionDTO;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public interface FidorTransactionHandler {

    /**
     *
     * @param fidorTransactionDTO
     * @param lineNumber corresponding to data in fidorTransactionDTO, starts at 1 (header line), first data line is 2
     */
    void handleTransaction(FidorTransactionDTO fidorTransactionDTO, long lineNumber);
}
