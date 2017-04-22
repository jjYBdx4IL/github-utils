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

import com.github.jjYBdx4IL.diskcache.WebDiskCache;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FMA API docs: https://freemusicarchive.org/api
 *
 * @author jjYBdx4IL
 */
public class FMAClient {

    private static final Logger LOG = LoggerFactory.getLogger(FMAClient.class);
    private static final WebDiskCache cache = new WebDiskCache(FMAClient.class.getName());
    private static final int RESULTS_PER_PAGE = 1000;
    private static final Pattern DL_URL_PAT = Pattern.compile("href=\"(https://freemusicarchive.org/music/download/[0-9a-f]+)\"");

    public static FMASearchResult search(String query, boolean commercialUseAllowedOnly) throws IOException {
        URIBuilder b = new URIBuilder();
        b.setScheme("http");
        b.setHost("freemusicarchive.org");
        b.setPath("/search/.json");
        b.addParameter("duration_from", "");
        b.addParameter("duration_to", "");
        b.addParameter("adv", "1");
        b.addParameter("search-genre", "Genres");
        b.addParameter("sort", "track_date_published");
        b.addParameter("d", "1");

        if (commercialUseAllowedOnly) {
            b.addParameter("music-filter-CC-attribution-only", "on");
            b.addParameter("music-filter-CC-attribution-sharealike", "1");
            b.addParameter("music-filter-CC-attribution-noderivatives", "1");
            b.addParameter("music-filter-public-domain", "1");
            b.addParameter("music-filter-commercial-allowed", "1");
        }

        b.addParameter("quicksearch", query != null ? query : "");
        b.addParameter("per_page", Integer.toString(RESULTS_PER_PAGE));

        int page = 0;
        boolean moreData = true;
        Gson gson = new Gson();
        FMASearchResult result = new FMASearchResult();
        while (moreData) {
            try {
                page++;
                b.setParameter("page", Integer.toString(page));
                byte[] data = cache.retrieve(b.build().toURL());
                FMASearchResult _result = gson.fromJson(new String(data), FMASearchResult.class);
                result.merge(_result);
                moreData = _result.aTracks.size() == RESULTS_PER_PAGE;
            } catch (URISyntaxException ex) {
                throw new IOException(ex);
            }
        }

        return result;
    }

    public static String parseDownloadUrl(String trackUrl) throws IOException {
        byte[] data = cache.retrieve(trackUrl, 3600*1000L);
        LOG.debug("track url: " + trackUrl);
        String page = new String(data, "UTF-8");
        LOG.debug("track url page content: " + page);
        Matcher m = DL_URL_PAT.matcher(page);
        String url = null;
        if (m.find()) {
            url = m.group(1);
        }
        LOG.debug("download url: " + url);
        return url;
    }

    private final FMAConfig config;
    private final HttpClient httpclient;

    public FMAClient() throws IOException {
        config = (FMAConfig) FMAConfig.readConfig("config.xml", FMAConfig.class);
        httpclient = HttpClients.createDefault();
    }
    
    public boolean isConfigInitialized() {
        return config.isInitialized();
    }
    
    public FMATrack getTrack(int trackId) throws IOException {
        if (!config.isInitialized()) {
            throw new RuntimeException("no api_key configured");
        }

        URIBuilder b = new URIBuilder();
        b.setScheme("https");
        b.setHost("freemusicarchive.org");
        b.setPath("/api/get/tracks.json");
        b.addParameter("api_key", config.fmaApiKey);
        b.addParameter("track_id", Integer.toString(trackId));

        LOG.debug("retrieving " + b.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        HttpGet httpGet = new HttpGet(b.toString());
        HttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("url returned status code " + response.getStatusLine().getStatusCode() + ": " + b.toString());
        }
        try (InputStream is = response.getEntity().getContent()) {
            IOUtils.copy(is, baos);
        }

        byte[] data = baos.toByteArray();
        Gson gson = new Gson();
        FMATrackResult _result = gson.fromJson(new String(data), FMATrackResult.class);
        return _result.dataset.get(0);
    }

}
