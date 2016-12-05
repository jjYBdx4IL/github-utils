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
package com.github.jjYBdx4IL.utils.fma;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author jjYBdx4IL
 */
public class FMAConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FMAConfig.class);
    public static final File CFG_DIR = getConfigDir();

    static File getConfigDir() {
        File configDir = new File(System.getProperty("user.home"), ".config");
        File f = new File(configDir, FMAConfig.class.getName());
        return f;
    }

    public String fmaApiKey = "replace me";

    public FMAConfig() {
    }
    
    public static Object readConfig(String filename, Class<?> clazz) throws IOException {
        try {
            File configFile = new File(CFG_DIR, filename);
            
            XStream xstream = new XStream(new StaxDriver());
            xstream.autodetectAnnotations(true);
            
            if (configFile.exists()) {
                return xstream.fromXML(configFile);
            }
            
            // save empty config so user is able to add his details
            configFile.getParentFile().mkdirs();
            Object config = clazz.newInstance();
            String xml = xstream.toXML(config);
            try (OutputStream os = new FileOutputStream(configFile)) {
                IOUtils.write(formatXml(xml), os);
            }
            return config;
        } catch (InstantiationException|IllegalAccessException ex) {
            throw new IOException(ex);
        }
    }
    
    public static String formatXml(String xml) {

        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());

            serializer.transform(xmlSource, res);

            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());

        } catch (IllegalArgumentException | TransformerException e) {
            LOG.error("", e);
            return xml;
        }
    }

    public boolean isInitialized() {
        return !fmaApiKey.equals("replace me");
    }

}
