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
package com.twitter.sdk.android.core.internal

import com.twitter.sdk.android.core.Twitter
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong

internal class ExecutorUtils private constructor() {

    companion object {

        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = CPU_COUNT + 1
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE = 1L
        private const val DEFAULT_TERMINATION_TIMEOUT = 1L

        fun buildThreadPoolExecutorService(name: String): ExecutorService {
            val threadFactory = getNamedThreadFactory(name)
            val executor = ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                LinkedBlockingQueue(), threadFactory
            )
            addDelayedShutdownHook(name, executor)
            return executor
        }

        fun buildSingleThreadScheduledExecutorService(name: String): ScheduledExecutorService {
            val threadFactory = getNamedThreadFactory(name)
            val executor = Executors.newSingleThreadScheduledExecutor(threadFactory)
            addDelayedShutdownHook(name, executor)
            return executor
        }

        private fun getNamedThreadFactory(threadNameTemplate: String): ThreadFactory {
            val count = AtomicLong(1)
            return ThreadFactory {
                val thread = Executors.defaultThreadFactory().newThread(it)
                thread.name = threadNameTemplate + count.getAndIncrement()
                thread
            }
        }

        fun addDelayedShutdownHook(serviceName: String, service: ExecutorService) {
            addDelayedShutdownHook(
                serviceName,
                service,
                DEFAULT_TERMINATION_TIMEOUT,
                TimeUnit.SECONDS
            )
        }

        fun addDelayedShutdownHook(
            serviceName: String,
            service: ExecutorService,
            terminationTimeout: Long,
            timeUnit: TimeUnit
        ) {
            Runtime.getRuntime().addShutdownHook(
                Thread(
                    {
                        try {
                            service.shutdown()
                            if (!service.awaitTermination(terminationTimeout, timeUnit)) {
                                Twitter.getLogger().d(
                                    Twitter.TAG,
                                    "$serviceName did not shutdown in the allocated time. Requesting immediate shutdown."
                                )
                                service.shutdownNow()
                            }
                        } catch (e: InterruptedException) {
                            Twitter.getLogger().d(
                                Twitter.TAG,
                                String.format(
                                    Locale.US,
                                    "Interrupted while waiting for %s to shut down. Requesting immediate shutdown.",
                                    serviceName
                                )
                            )
                            service.shutdownNow()
                        }
                    },
                    "Twitter Shutdown Hook for $serviceName"
                )
            )
        }
    }
}