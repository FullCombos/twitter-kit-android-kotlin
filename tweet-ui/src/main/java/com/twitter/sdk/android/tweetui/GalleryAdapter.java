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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.twitter.sdk.android.core.LoadImageCallback;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterImageLoader;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.GalleryImageView;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.util.ArrayList;
import java.util.List;

class GalleryAdapter extends PagerAdapter {

    private final List<MediaEntity> items = new ArrayList<>();
    private final Context context;
    private final SwipeToDismissTouchListener.Callback callback;
    @Nullable
    private final TwitterImageLoader imageLoader = Twitter.getInstance().getImageLoader();

    GalleryAdapter(Context context, SwipeToDismissTouchListener.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    void addAll(List<MediaEntity> entities) {
        items.addAll(entities);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final GalleryImageView root = new GalleryImageView(context);
        root.setSwipeToDismissCallback(callback);

        container.addView(root);

        final MediaEntity entity = items.get(position);

        if (imageLoader != null) {
            imageLoader.load(entity.getMediaUrlHttps()).into(root, new LoadImageCallback() {

                @Override
                public void onPrepare(Drawable placeholder) {
                    root.onPrepareLoad(placeholder);
                }

                @Override
                public void onSuccess(@NonNull Bitmap bitmap) {
                    root.onBitmapLoaded(bitmap);
                }

                @Override
                public void onError(Drawable error) {
                    root.onBitmapFailed(error);
                }
            });
        }

        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
