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

import com.github.jjYBdx4IL.diskcache.DiskCache;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FMA API docs: https://freemusicarchive.org/api
 *
 * @author jjYBdx4IL
 */
public class FMAClient {

    private static final Logger LOG = LoggerFactory.getLogger(FMAClient.class);
    private static final DiskCache cache = new DiskCache(FMAClient.class.getName());
    private static final int RESULTS_PER_PAGE = 1000;

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
    
    private FMAClient() {
    }

}
