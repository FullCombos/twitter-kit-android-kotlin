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

import com.twitter.sdk.android.core.internal.persistence.PreferenceStore
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreStrategy
import com.twitter.sdk.android.core.internal.persistence.SerializationStrategy
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Implementation of [com.twitter.sdk.android.core.SessionManager] that persists sessions.
 */
class PersistedSessionManager<T : Session<*>> internal constructor(
    private val preferenceStore: PreferenceStore,
    private val serializer: SerializationStrategy<T>,
    private val sessionMap: ConcurrentHashMap<Long, T>,
    private val storageMap: ConcurrentHashMap<Long, PreferenceStoreStrategy<T>>,
    private val activeSessionStorage: PreferenceStoreStrategy<T>,
    private val prefKeySession: String
) : SessionManager<T> {

    private val activeSessionRef = AtomicReference<T>()

    @Volatile
    private var restorePending = true

    constructor(
        preferenceStore: PreferenceStore,
        serializer: SerializationStrategy<T>,
        prefKeyActiveSession: String,
        prefKeySession: String
    ) : this(
        preferenceStore,
        serializer,
        ConcurrentHashMap<Long, T>(NUM_SESSIONS),
        ConcurrentHashMap<Long, PreferenceStoreStrategy<T>>(NUM_SESSIONS),
        PreferenceStoreStrategy<T>(preferenceStore, serializer, prefKeyActiveSession),
        prefKeySession
    )

    private fun restoreAllSessionsIfNecessary() {
        // Only restore once
        if (restorePending) {
            restoreAllSessions()
        }
    }

    @Synchronized
    private fun restoreAllSessions() {
        if (restorePending) {
            restoreActiveSession()
            restoreSessions()
            restorePending = false
        }
    }

    private fun restoreSessions() {
        var session: T?
        val preferences = preferenceStore.get().all
        for ((key, value) in preferences) {
            if (isSessionPreferenceKey(key)) {
                session = serializer.deserialize(value as? String)
                if (session != null) {
                    internalSetSession(session.id, session, false)
                }
            }
        }
    }

    private fun restoreActiveSession() {
        val session = activeSessionStorage.restore()
        if (session != null) {
            internalSetSession(session.id, session, false)
        }
    }

    private fun isSessionPreferenceKey(preferenceKey: String): Boolean {
        return preferenceKey.startsWith(prefKeySession)
    }

    /**
     * @return the active session, may return `null` if there's no session.
     */
    override fun getActiveSession(): T? {
        restoreAllSessionsIfNecessary()
        return activeSessionRef.get()
    }

    /**
     * Sets the active session.
     */
    override fun setActiveSession(session: T) {
        restoreAllSessionsIfNecessary()
        internalSetSession(session.id, session, true)
    }

    /**
     * Clears the active session.
     */
    override fun clearActiveSession() {
        restoreAllSessionsIfNecessary()
        val activeSession = activeSessionRef.get()
        if (activeSession != null) {
            clearSession(activeSession.id)
        }
    }

    /**
     * @return the session associated with the id, may return `null` if there's no session.
     */
    override fun getSession(id: Long): T? {
        restoreAllSessionsIfNecessary()
        return sessionMap[id]
    }

    /**
     * Sets the session to associate with the id. If there is no active session, this session also
     * becomes the active session.
     */
    override fun setSession(id: Long, session: T) {
        restoreAllSessionsIfNecessary()
        internalSetSession(id, session, false)
    }

    override fun getSessionMap(): Map<Long, T> {
        restoreAllSessionsIfNecessary()
        return Collections.unmodifiableMap(sessionMap)
    }

    private fun internalSetSession(id: Long, session: T, forceUpdate: Boolean) {
        sessionMap[id] = session
        var storage = storageMap[id]
        if (storage == null) {
            storage = PreferenceStoreStrategy(preferenceStore, serializer, getPrefKey(id))
            storageMap.putIfAbsent(id, storage)
        }
        storage.save(session)
        val activeSession = activeSessionRef.get()
        if (activeSession == null || activeSession.id == id || forceUpdate) {
            synchronized(this) {
                activeSessionRef.compareAndSet(activeSession, session)
                activeSessionStorage.save(session)
            }
        }
    }

    private fun getPrefKey(id: Long): String {
        return prefKeySession + "_" + id
    }

    /**
     * Clears the session associated with the id.
     */
    override fun clearSession(id: Long) {
        restoreAllSessionsIfNecessary()

        val activeSession = activeSessionRef.get()
        if (activeSession != null && activeSession.id == id) {
            synchronized(this) {
                activeSessionRef.set(null)
                activeSessionStorage.clear()
            }
        }
        sessionMap.remove(id)
        storageMap.remove(id)?.clear()
    }

    companion object {
        private const val NUM_SESSIONS = 1
    }
}