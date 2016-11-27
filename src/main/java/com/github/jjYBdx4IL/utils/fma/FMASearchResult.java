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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author jjYBdx4IL
 */
public class FMASearchResult {
    
    public List<FMATrack> aTracks;

    public FMASearchResult() {
        this.aTracks = new ArrayList<>();
    }

    public int size() {
        return aTracks.size();
    }

    public void merge(FMASearchResult _result) {
        aTracks.addAll(_result.aTracks);
    }

    public void postprocess() {
        removeDupes();
        sort();
    }

    // sort by: artist name, album title, track title
    private void sort() {
        aTracks.sort(new Comparator<FMATrack>() {
            @Override
            public int compare(FMATrack o1, FMATrack o2) {
                String artist1 = o1.artist_name != null ? o1.artist_name.toLowerCase(Locale.ROOT) : "";
                String artist2 = o2.artist_name != null ? o2.artist_name.toLowerCase(Locale.ROOT) : "";
                if (artist1.compareTo(artist2) != 0) {
                    return o1.artist_name.compareTo(o2.artist_name);
                }
                String album1 = o1.album_title != null ? o1.album_title.toLowerCase(Locale.ROOT) : "";
                String album2 = o2.album_title != null ? o2.album_title.toLowerCase(Locale.ROOT) : "";
                if (album1.compareTo(album2) != 0) {
                    return o1.album_title.compareTo(o2.album_title);
                }
                String track1 = o1.track_title != null ? o1.track_title.toLowerCase(Locale.ROOT) : "";
                String track2 = o2.track_title != null ? o2.track_title.toLowerCase(Locale.ROOT) : "";
                return track1.compareTo(track2);
            }
        });
    }

    private void removeDupes() {
        Set<FMATrack> tracks = new HashSet<>();
        tracks.addAll(aTracks);
        aTracks.clear();
        aTracks.addAll(tracks);
    }
}
