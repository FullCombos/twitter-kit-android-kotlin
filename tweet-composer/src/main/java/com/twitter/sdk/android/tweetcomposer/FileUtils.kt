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
package com.twitter.sdk.android.tweetcomposer

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File

/**
 * Utilities for resolving various Uri's to file paths and MIME types.
 */
internal class FileUtils private constructor() {

    companion object {

        fun getPath(context: Context, uri: Uri?): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                DocumentsContract.isDocumentUri(context, uri)
            ) {
                val documentId = DocumentsContract.getDocumentId(uri) // e.g. "image:1234"
                val parts = documentId.split(":".toRegex()).toTypedArray()
                val type = parts[0]

                if (isExternalStorageDocument(uri)) {
                    if ("primary".equals(type, ignoreCase = true)) {
                        @Suppress("DEPRECATION")
                        return "${Environment.getExternalStorageDirectory()}/${parts[1]}"
                    }
                } else if (isDownloadsDocument(uri)) {
                    // Starting with Android O, this "id" is not necessarily a long (row number),
                    // but might also be a "raw:/some/file/path" URL
                    if (documentId != null && documentId.startsWith("raw:/")) {
                        val rawUri = Uri.parse(documentId)
                        return rawUri.path
                    } else {
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix), documentId.toLong()
                            )
                            val path = resolveInfo(context, contentUri)
                            if (!path.isNullOrEmpty()) {
                                return path
                            }
                        }
                    }
                } else if (isMediaDocument(uri)) {
                    val contentUri = when (type) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }

                    // query content resolver for MediaStore id column
                    val selection = "_id=?"
                    val args = arrayOf(parts[1])
                    return resolveInfo(
                        context,
                        contentUri,
                        selection = selection,
                        args = args
                    )
                }
            }

            if (isContentScheme(uri)) {
                return resolveInfo(context, uri)
            } else if (isFileScheme(uri)) {
                return uri?.path
            }
            return uri?.path
        }

        private fun isExternalStorageDocument(uri: Uri?): Boolean {
            return "com.android.externalstorage.documents".equals(uri?.authority, ignoreCase = true)
        }

        private fun isDownloadsDocument(uri: Uri?): Boolean {
            return "com.android.providers.downloads.documents".equals(
                uri?.authority, ignoreCase = true
            )
        }

        private fun isMediaDocument(uri: Uri?): Boolean {
            return "com.android.providers.media.documents".equals(uri?.authority, ignoreCase = true)
        }

        private fun isContentScheme(uri: Uri?): Boolean {
            return ContentResolver.SCHEME_CONTENT.equals(uri?.scheme, ignoreCase = true)
        }

        private fun isFileScheme(uri: Uri?): Boolean {
            return ContentResolver.SCHEME_FILE.equals(uri?.scheme, ignoreCase = true)
        }

        private fun resolveInfo(
            context: Context,
            uri: Uri?,
            @Suppress("DEPRECATION")
            projectionStr: String = MediaStore.Images.Media.DATA,
            selection: String? = null,
            args: Array<String>? = null
        ): String? {
            uri ?: return null

            val projection = arrayOf(projectionStr)
            return context.contentResolver.query(uri, projection, selection, args, null)?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(projectionStr)
                    if (index >= 0) {
                        return it.getString(index)
                    }
                }
                return null
            }
        }

        fun getFileName(
            context: Context,
            uri: Uri?,
            projectionStr: String
        ): String? {
            return resolveInfo(context, uri, projectionStr)
        }

        /**
         * @return The MIME type for the given file.
         */
        fun getMimeType(file: File): String? {
            val ext = getExtension(file.name)
            return if (!ext.isNullOrEmpty()) {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
            } else {
                // default from https://dev.twitter.com/rest/public/uploading-media
                "application/octet-stream"
            }
        }

        /**
         * @return the extension of the given file name, excluding the dot. For example, "png", "jpg".
         */
        fun getExtension(filename: String?): String? {
            if (filename == null) {
                return null
            }
            val i = filename.lastIndexOf(".")
            return if (i < 0) "" else filename.substring(i + 1)
        }
    }
}