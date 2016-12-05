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

/**
 *
 * @author jjYBdx4IL
 */
public class FMATrack {

    public long track_id;
    public Long album_id;
    public String album_title;
    public String album_url;
    public Long artist_id;
    public String artist_name;
    public String artist_url;
    /**
     * beware! the track file url changes regularly and becomes invalid after some point. Use
     * track_url in combination with {@link FMAClient#parseDownloadUrl(java.lang.String)} to get
     * an up-to-date download link.
     */
    public String track_file_url;
    public String track_title;
    public String track_url;
    public Long track_number;
    public String license_title;
    public String license_url;
    public String track_duration;
    public String track_image_file;

    @Override
    public String toString() {
        return "FMATrack{" + "track_id=" + track_id + ", album_id=" + album_id + ", album_title=" + album_title + ", album_url=" + album_url + ", artist_id=" + artist_id + ", artist_name=" + artist_name + ", artist_url=" + artist_url + ", track_file_url=" + track_file_url + ", track_title=" + track_title + ", track_url=" + track_url + ", track_number=" + track_number + ", license_title=" + license_title + ", license_url=" + license_url + ", track_duration=" + track_duration + ", track_image_file=" + track_image_file + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.track_id ^ (this.track_id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FMATrack other = (FMATrack) obj;
        if (this.track_id != other.track_id) {
            return false;
        }
        return true;
    }

}
