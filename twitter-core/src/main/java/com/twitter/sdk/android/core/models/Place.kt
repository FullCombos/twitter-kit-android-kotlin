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
package com.twitter.sdk.android.core.models

import com.google.gson.annotations.SerializedName

/**
 * A place is a specific, named location with corresponding geo coordinates.
 */
class Place(
    attributes: Map<String, String>,
    boundingBox: BoundingBox,
    country: String,
    countryCode: String,
    fullName: String,
    id: String,
    name: String,
    placeType: String,
    url: String
) {

    /**
     * Place Attributes are metadata about places. An attribute is a key-value pair of arbitrary
     * strings, but with some conventions.
     */
    @SerializedName("attributes")
    val attributes: Map<String, String>

    /**
     * A bounding box of coordinates which encloses this place.
     */
    @SerializedName("bounding_box")
    val boundingBox: BoundingBox

    /**
     * Name of the country containing this place.
     */
    @SerializedName("country")
    val country: String

    /**
     * Shortened country code representing the country containing this place.
     */
    @SerializedName("country_code")
    val countryCode: String

    /**
     * Full human-readable representation of the place's name.
     */
    @SerializedName("full_name")
    val fullName: String

    /**
     * ID representing this place. Note that this is represented as a string, not an integer.
     */
    @SerializedName("id")
    val id: String

    /**
     * Short human-readable representation of the place's name.
     */
    @SerializedName("name")
    val name: String

    /**
     * The type of location represented by this place.
     */
    @SerializedName("place_type")
    val placeType: String

    /**
     * URL representing the location of additional place metadata for this place.
     */
    @SerializedName("url")
    val url: String

    init {
        this.attributes = ModelUtils.getSafeMap(attributes)
        this.boundingBox = boundingBox
        this.country = country
        this.countryCode = countryCode
        this.fullName = fullName
        this.id = id
        this.name = name
        this.placeType = placeType
        this.url = url
    }

    class BoundingBox(coordinates: List<List<List<Double>>>, type: String) {

        /**
         * A series of longitude and latitude points, defining a box which will contain the Place
         * entity this bounding box is related to. Each point is an array in the form of
         * [longitude, latitude]. Points are grouped into an array per bounding box. Bounding box
         * arrays are wrapped in one additional array to be compatible with the polygon notation.
         */
        @SerializedName("coordinates")
        val coordinates: List<List<List<Double>>>?

        /**
         * The type of data encoded in the coordinates property. This will be "Polygon" for bounding
         * boxes.
         */
        @SerializedName("type")
        val type: String?

        init {
            this.coordinates = ModelUtils.getSafeList(coordinates)
            this.type = type
        }
    }
}