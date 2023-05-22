package com.twitter.sdk.android.core.models

import com.google.gson.annotations.SerializedName

class TweetContent(text: String?, mediaId: String?) {

    @SerializedName("text")
    val text: String?

    @SerializedName("media")
    val media: AttachedMedia?

    init {
        this.text = text

        this.media = if (mediaId.isNullOrEmpty()) {
            null
        } else {
            AttachedMedia(listOf(mediaId), null)
        }
    }

    class AttachedMedia(
        @SerializedName("media_ids")
        val mediaIds: List<String>,
        @SerializedName("tagged_user_ids")
        val taggedUserIds: List<String>?
    )
}