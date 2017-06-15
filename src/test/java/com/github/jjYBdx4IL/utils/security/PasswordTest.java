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
package com.github.jjYBdx4IL.utils.security;

//CHECKSTYLE:OFF
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class PasswordTest {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordTest.class);

    /**
     * Test of getSaltedHash method, of class Password.
     *
     * @throws java.lang.Exception on error
     */
    @Test
    public void testIt() throws Exception {
//        for (Provider p : Security.getProviders()) {
//            log.info(p.getName() + ": " + p.getInfo());
//            log.info("  \\--> " + p.toString());
//            for (Service s : p.getServices()) {
//                log.info("  \\--> " + s.getType() + " : " + s.getClassName() + " : " + s.getAlgorithm());
//                log.info("  \\--> " + s.toString());
//            }
//        }

        long storeStartTime = System.currentTimeMillis();

        String password = "password";
        String password2 = "password2";
        String result1a = Password.getSaltedHash(password);
        LOG.info(result1a);
        String result1b = Password.getSaltedHash(password);
        LOG.info(result1b);
        String result2 = Password.getSaltedHash(password2);
        LOG.info(result2);

        long storeEndTime = System.currentTimeMillis();

        long checkStartTime = System.currentTimeMillis();

        assertTrue(Password.check(password, result1a));
        assertTrue(Password.check(password, result1b));
        assertTrue(Password.check(password2, result2));

        assertFalse(Password.check(password2, result1a));
        assertFalse(Password.check(password2, result1b));
        assertFalse(Password.check(password, result2));

        long checkEndTime = System.currentTimeMillis();

        LOG.info(String.format("store speed: %.2f passwords/second, check speed: %.2f passwords/second",
                3000.0 / (storeEndTime - storeStartTime), 3000.0 / (checkEndTime - checkStartTime)));
    }
}
