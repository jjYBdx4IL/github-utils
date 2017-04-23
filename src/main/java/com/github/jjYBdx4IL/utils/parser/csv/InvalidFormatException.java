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

import java.io.IOException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class InvalidFormatException extends IOException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFormatException(String message, long lineNumber, String lineContent) {
        super(String.format("%s (line %d: %s)", message, lineNumber, lineContent));
    }

    public InvalidFormatException(String message, long lineNumber) {
        super(String.format("%s (line %d)", message, lineNumber));
    }

    public InvalidFormatException(String message, long lineNumber, Throwable cause) {
        super(String.format("%s (line %d)", message, lineNumber), cause);
    }

}
