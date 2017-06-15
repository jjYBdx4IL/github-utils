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
package com.github.jjYBdx4IL.utils.parser.csv;

//CHECKSTYLE:OFF
import java.util.Date;

/**
 * 
 * @author Github jjYBdx4IL Projects
 */
public class FidorTransactionDTO {

    private Date date;
    private String description1;
    private String description2;
    private Double value;

    /**
     * @return the date
     */
    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }

    /**
     * @return the description1
     */
    public String getDescription1() {
        return description1;
    }

    /**
     * @param description1 the description1 to set
     */
    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    /**
     * @return the description2
     */
    public String getDescription2() {
        return description2;
    }

    /**
     * @param description2 the description2 to set
     */
    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    /**
     * @return the value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FidorTransactionDTO [");
        builder.append("date=");
        builder.append(date);
        builder.append(", description1=");
        builder.append(description1);
        builder.append(", description2=");
        builder.append(description2);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }
}
