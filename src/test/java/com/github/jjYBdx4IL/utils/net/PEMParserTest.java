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
package com.github.jjYBdx4IL.utils.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.utils.junit4.PropertyRestorer;
import com.github.jjYBdx4IL.utils.net.SSLUtils.PrincipalParts;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

// CHECKSTYLE:OFF
/**
 * openssl s_client -showcerts -connect www.ibm.com:443
 *
 * @author Github jjYBdx4IL Projects
 */
public class PEMParserTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final PropertyRestorer propertyRestorer = PropertyRestorer.getInstance();

    static {
        Provider p = (Provider) new BouncyCastleProvider();
        // Security.insertProviderAt(p, 1);
        Security.addProvider(p);
    }


    @BeforeClass
    public static void beforeClass() {
        propertyRestorer.setDefaultTimeZone(TimeZone.getDefault());
        propertyRestorer.setDefaultLocale(Locale.getDefault());
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.US);
    }

    @AfterClass
    public static void afterClass() {
        propertyRestorer.restoreProps();
    }
    
    public PEMParserTest() {
    }

    /**
     * Test of stripComments method, of class PEMParser.
     * 
     * @throws java.lang.Exception on error
     */
    @Test
    public void testStripCommentsServerCert() throws Exception {
        try (PEMParser pemReader = new PEMParser(new InputStreamReader(
                PEMParserTest.class.getResourceAsStream("ibm.com.cert.pem")))) {
            X509CertificateHolder certHolder = (X509CertificateHolder) pemReader.readObject();

            X509Certificate cert = new JcaX509CertificateConverter().setProvider( "BC" )
                .getCertificate( certHolder );

            try {
                cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2014-02-17"));
                fail();
            } catch (ParseException | CertificateExpiredException | CertificateNotYetValidException ex) {
            }

            cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2017-07-19"));

            try {
                cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2016-02-17"));
                fail();
            } catch (ParseException | CertificateExpiredException | CertificateNotYetValidException ex) {
            }

            assertEquals("Fri Jan 26 23:59:59 UTC 2018", cert.getNotAfter().toString());

            assertEquals("C=US,O=GeoTrust Inc.,CN=GeoTrust SSL CA - G3",
                    cert.getIssuerDN().getName());

            assertEquals("C=US,ST=New York,L=Armonk,O=IBM,CN=www.ibm.com",
                    cert.getSubjectDN().getName());

            assertEquals("www.ibm.com", SSLUtils.getSubjectPart(cert, PrincipalParts.CN.toString()));
            assertEquals("GeoTrust SSL CA - G3", SSLUtils.getIssuerPart(cert, PrincipalParts.CN.toString()));
        }
    }

	@Test
    public void testStripCommentsCACert() throws Exception {
        try (PEMParser pemReader = new PEMParser(new InputStreamReader(
                PEMParserTest.class.getResourceAsStream("ca.cert.pem")))) {
            X509CertificateHolder certHolder = (X509CertificateHolder) pemReader.readObject();

            X509Certificate cert = new JcaX509CertificateConverter().setProvider( "BC" )
                .getCertificate( certHolder );

            try {
                cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2030-06-17"));
                fail();
            } catch (ParseException | CertificateExpiredException | CertificateNotYetValidException ex) {
            }

            cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2017-07-19"));

            try {
                cert.checkValidity(new SimpleDateFormat(DATE_FORMAT).parse("2011-02-17"));
                fail();
            } catch (ParseException | CertificateExpiredException | CertificateNotYetValidException ex) {
            }

            assertEquals("Fri May 20 21:36:50 UTC 2022", cert.getNotAfter().toString());

            assertEquals("C=US,O=GeoTrust Inc.,CN=GeoTrust Global CA",
                    cert.getIssuerDN().getName());

            assertEquals("C=US,O=GeoTrust Inc.,CN=GeoTrust SSL CA - G3",
                    cert.getSubjectDN().getName());

            assertEquals("GeoTrust SSL CA - G3", SSLUtils.getSubjectPart(cert, PrincipalParts.CN.toString()));
            assertEquals("GeoTrust Global CA", SSLUtils.getIssuerPart(cert, PrincipalParts.CN.toString()));
        }
    }

}
