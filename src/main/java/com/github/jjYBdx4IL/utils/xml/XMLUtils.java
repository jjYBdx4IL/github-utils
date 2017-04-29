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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.junit.Assert;

public class XMLUtils {

    /**
     * Dirty XML comparison. See {@link #strip(String)}.
     *
     * @param xml1
     * @param xml2
     */
    public static void assertEquals(String xml1, String xml2) {
        Assert.assertEquals(strip(xml1), strip(xml2));
    }

    /**
     * Trim whitespace in XML where pure whitespace shares the same parent with
     * xml nodes and there are no text nodes with the same parent that have non-whitespace in them.
     * Beware! Whitespace is significant in most parts of XML! Don't
     * use this if you are not an XML pro! This is only intended for certain
     * areas of testing. 
     *
     * @param xml UTF-8 encoding is assumed
     * @return
     */
    public static String strip(String xml) {
        try {
            Document dom = null;
            try (InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"))) {
                dom = new SAXReader().read(is);
            }

            Visitor visitor = new VisitorSupport() {
                public void visit(Text node) {
                    Element parent = node.getParent();
                    if (hasNonTextChilds(parent) && !hasTextChildsWithNonWhitespace(parent)) {
                        node.setText(node.getText().trim());
                    }
                }
            };

            dom.accept(visitor);
            return dom.asXML();
        } catch (IOException | DocumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String stripH(String xml) {
        return stripXMLHeader(strip(xml));
    }

    public static boolean hasNonTextChilds(Element element) {
        for (Object node : element.content()) {
            Node e = (Node) node;
            if (e.getNodeType() != Node.TEXT_NODE) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasTextChildsWithNonWhitespace(Element element) {
        for (Object node : element.content()) {
            Node e = (Node) node;
            if (e.getNodeType() != Node.TEXT_NODE) {
                continue;
            }
            Text textNode = (Text) e;
            if (!textNode.getText().trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static String stripXMLHeader(String xml) {
        xml = xml.replaceAll("^<\\?xml.*?\\?>\r?\n?", "");
        return xml;
    }

}
