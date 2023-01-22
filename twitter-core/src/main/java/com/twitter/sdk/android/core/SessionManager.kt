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
package com.twitter.sdk.android.core

/**
 * SessionManager for managing sessions.
 */
interface SessionManager<T : Session<*>> {

    /**
     * @return the active session, restoring saved session if available
     */
    fun getActiveSession(): T?

    /**
     * Sets the active session.
     */
    fun setActiveSession(session: T)

    /**
     * Clears the active session.
     */
    fun clearActiveSession()

    /**
     * @return the session associated with the id.
     */
    fun getSession(id: Long): T?

    /**
     * Sets the session to associate with the id. If there is no active session, this session also
     * becomes the active session.
     */
    fun setSession(id: Long, session: T)

    /**
     * Clears the session associated with the id.
     */
    fun clearSession(id: Long)

    /**
     * @return the session map containing all managed sessions
     */
    fun getSessionMap(): Map<Long, T>
}