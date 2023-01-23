/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.UrlEntity;

class FormattedUrlEntity {
    int start;
    int end;
    final String displayUrl;
    final String url;
    final String expandedUrl;

    FormattedUrlEntity(int start, int end, String displayUrl, String url, String expandedUrl) {
        this.start = start;
        this.end = end;
        this.displayUrl = displayUrl;
        this.url = url;
        this.expandedUrl = expandedUrl;
    }

    static FormattedUrlEntity createFormattedUrlEntity(UrlEntity entity) {
        return new FormattedUrlEntity(entity.getStart(), entity.getEnd(), entity.getDisplayUrl(),
                entity.getUrl(), entity.getExpandedUrl());
    }

    static FormattedUrlEntity createFormattedUrlEntity(HashtagEntity hashtagEntity) {
        final String url = TweetUtils.getHashtagPermalink(hashtagEntity.getText());
        return new FormattedUrlEntity(hashtagEntity.getStart(), hashtagEntity.getEnd(),
                "#" + hashtagEntity.getText(), url, url);
    }

    static FormattedUrlEntity createFormattedUrlEntity(MentionEntity mentionEntity) {
        final String url = TweetUtils.getProfilePermalink(mentionEntity.getScreenName());
        return new FormattedUrlEntity(mentionEntity.getStart(), mentionEntity.getEnd(),
                "@" + mentionEntity.getScreenName(), url, url);
    }

    static FormattedUrlEntity createFormattedUrlEntity(SymbolEntity symbolEntity) {
        final String url = TweetUtils.getSymbolPermalink(symbolEntity.getText());
        return new FormattedUrlEntity(symbolEntity.getStart(), symbolEntity.getEnd(),
                "$" + symbolEntity.getText(), url, url);
    }
}
