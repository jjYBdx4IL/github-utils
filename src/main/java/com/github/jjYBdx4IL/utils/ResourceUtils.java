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
package com.github.jjYBdx4IL.utils;

import com.sun.jna.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ResourceUtils {

    private static final Logger log = LoggerFactory.getLogger(ResourceUtils.class);

    protected static final Set<String> libRegistry = new HashSet<>();

    /**
     * Loads native libraries from the classpath. Extracts from jar files if necessary. Follows the path
     * naming convention of JNA, {@link com.sun.jna#Platform.RESOURCE_PREFIX}. Strictly loads libraries from
     * the classpath. Ignores jni.library.path setting. This function will detect repeated loads of the same
     * library and silently skip any further loads using the same libName argument.
     *
     * @param libName the native library to load, ie. "fann" for "libfann.so" on Linux systems
     * @throws RuntimeException checked exception are wrapped in RuntimeException
     */
    public static synchronized void loadLibrary(String libName) {
        if (libRegistry.contains(libName)) {
            log.debug("library " + libName + " already loaded, silently ignoring load request");
            return;
        }

        String mappedLibName = System.mapLibraryName(libName);
        String resourceId = "/" + Platform.RESOURCE_PREFIX + "/" + mappedLibName;
        log.debug("trying to find " + resourceId);
        File extractedLib = null;
        try {
            extractedLib = extractResource(resourceId);
            log.debug("extracted resource to " + extractedLib.getPath());
            System.load(extractedLib.getPath());
            libRegistry.add(libName);
        } catch (IOException ex) {
            log.debug("loading failed", ex);
            throw new RuntimeException(ex);
        } finally {
            if (extractedLib != null && extractedLib.exists()) {
                log.debug("deleting " + extractedLib.getPath());
                if (!extractedLib.delete()) {
                    log.debug("failed to delete " + extractedLib.getPath() + ", scheduling deletion for VM shutdown");
                    extractedLib.deleteOnExit();
                }
            }
        }
    }

    /**
     * Same as getClass().getResource(), but returns a {@link java.io.File} handle to an extracted file copy.
     * Beware: it's your duty to clean up this file, though it's created inside your system's temp folder.
     *
     * @param resourceId an absolute resourceId (ie. starts with "/")
     * @return
     * @throws java.io.IOException
     */
    public static File extractResource(String resourceId) throws IOException {
        if (resourceId == null || resourceId.isEmpty()) {
            throw new IllegalArgumentException("no resourceId given");
        }
        if (!resourceId.substring(0, 1).equals("/")) {
            throw new IllegalArgumentException("resourceId is not absolute");
        }
        try (InputStream is = ResourceUtils.class.getResourceAsStream(resourceId)) {
            if (is == null) {
                throw new IOException("resource not found: " + resourceId);
            }
            File tempFile = File.createTempFile("extractedResource", ".tmp");
            try (OutputStream os = new FileOutputStream(tempFile)) {
                IOUtils.copy(is, os);
            }
            return tempFile;
        }
    }

    private ResourceUtils() {
    }
}
