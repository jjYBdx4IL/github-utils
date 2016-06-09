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
package com.github.jjYBdx4IL.utils.vmmgmt;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class DiskImageCache {

    private static final Logger log = LoggerFactory.getLogger(DiskImageCache.class);
    private static final File cacheDir = new File(System.getProperty("user.home"),
            ".cache" + File.separator + DiskImageCache.class.getName());
    public static final String DOT_XML = ".xml";
    public static final String DOT_TMP = ".tmp";
    public static final String DISKIMAGE_NAME_REGEX = "^[a-z][a-z0-9-]*$";
    public static final Pattern DISKIMAGE_NAME_PATTERN = Pattern.compile(DISKIMAGE_NAME_REGEX, Pattern.CASE_INSENSITIVE);

    public static DiskImage get(String name) {
        if (!DISKIMAGE_NAME_PATTERN.matcher(name).find()) {
            throw new IllegalArgumentException("bad disk image name");
        }
        File xmlFile = new File(cacheDir, name + DOT_XML);
        if (!xmlFile.exists()) {
            return null;
        }
        XStream xstream = new XStream(new StaxDriver());
        DiskImage diskImage = null;
        try {
            diskImage = (DiskImage) xstream.fromXML(xmlFile);
        } catch (StreamException ex) {
            log.error("", ex);
        }
        return diskImage;
    }

    public static DiskImage put(String name, DiskImage diskImage) throws IOException {
        if (!DISKIMAGE_NAME_PATTERN.matcher(name).find()) {
            throw new IllegalArgumentException("bad disk image name");
        }

        if (diskImage.getBackingDiskImage() != null) {
            throw new IllegalArgumentException("images with backing files not supported");
        }

        DiskImage newDiskImage = new DiskImage(
                new File(cacheDir, name),
                diskImage.getFormat(),
                diskImage.getSizeGB(),
                diskImage.getBackingDiskImage());

        if (!newDiskImage.getImage().getParentFile().exists()) {
            newDiskImage.getImage().getParentFile().mkdirs();
        }

        if (newDiskImage.getFormat().equals(DiskImageFormat.qcow2)) {
            diskImage.convert(newDiskImage, true);
        } else {
            Files.move(diskImage.getImage().toPath(), newDiskImage.getImage().toPath(), REPLACE_EXISTING);
        }

        File xmlFile = new File(cacheDir, name + DOT_XML);
        XStream xstream = new XStream(new StaxDriver());
        try {
            try (OutputStream os = new FileOutputStream(xmlFile)) {
                xstream.toXML(newDiskImage, os);
            }
        } catch (Throwable err) {
            if (xmlFile.exists()) {
                xmlFile.delete();
            }
            if (newDiskImage.getImage().exists()) {
                newDiskImage.getImage().delete();
            }
            throw err;
        }

        return newDiskImage;
    }
}
