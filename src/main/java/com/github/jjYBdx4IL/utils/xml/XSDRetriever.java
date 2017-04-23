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
package com.github.jjYBdx4IL.utils.xml;

/*
 * #%L
 * Shared Package
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class XSDRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(XSDRetriever.class);

    public static void retrieve(String startRelFile, String parentUrl, String outDir) throws IOException {

        List<String> relFiles = new ArrayList<>();
        relFiles.add(startRelFile);

        if (!parentUrl.endsWith("/")) {
            parentUrl += "/";
        }

        Set<String> processed = new HashSet<>();
        boolean done = false;

        while (!done) {
            done = true;
            for (String relFile : relFiles.toArray(new String[]{})) {
                relFiles.remove(relFile);

                if (processed.contains(relFile)) {
                    continue;
                }

                processed.add(relFile);

                File outFile = new File(outDir, relFile.replace("/", File.separator));
                if (outFile.exists()) {
                    outFile.delete();
                }

                LOG.info("retrieving: " + relFile);
                done = false;

                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }

                try (InputStream is = new URL(parentUrl + relFile).openStream()) {
                    try (OutputStream os = new FileOutputStream(outFile)) {
                        IOUtils.copy(is, os);
                    }
                }
                try (InputStream is = new FileInputStream(outFile)) {
                    List<String> list = getIncludeImportSchemaLocations(is);
                    relFiles.addAll(list);
                } catch (DocumentException ex) {
                    throw new IOException(ex);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getIncludeImportSchemaLocations(InputStream xsdStream) throws DocumentException {
        List<String> list = new ArrayList<>();

        Document dom = new SAXReader().read(xsdStream);

        for (Element el : (List<Element>) dom.getRootElement().elements("include")) {
            String schemaLocation = el.attributeValue("schemaLocation");
            list.add(schemaLocation);
        }
        for (Element el : (List<Element>) dom.getRootElement().elements("import")) {
            String schemaLocation = el.attributeValue("schemaLocation");
            list.add(schemaLocation);
        }

        return list;
    }

    public static void main(String[] args) throws IOException {
        String outDir = args[0];
        String parentUrl = args[1];
        for (int i = 2; i < args.length; i++) {
            retrieve(args[i], parentUrl, outDir);
        }
    }
}
