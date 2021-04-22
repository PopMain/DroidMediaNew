package com.popmain.droidmedia.util

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object AsyncRunnableWorker {

    private val asyncExecutor by lazy {
        ThreadPoolExecutor(2, // 核心线程2
                10,  // 最大线程数 10
                60,  // 线程KeepLive时长 60S
                TimeUnit.SECONDS,
                LinkedBlockingQueue<Runnable>(), // 无界队列
                AsyncLoaderThreadFactory())
    }

    @JvmStatic
    fun execute(runnable: Runnable) {
        asyncExecutor.execute(runnable)
    }

    class AsyncLoaderThreadFactory : ThreadFactory {
        companion object {
            const val THREAD_PREV = "popmain-async-workder"
            val threadsCount = AtomicInteger()
        }
        override fun newThread(r: Runnable?): Thread {
            // 这里有字符串拼接，有性能损耗
            val thread = Thread(r, THREAD_PREV +  threadsCount.getAndIncrement())
            thread.isDaemon = false
            return thread
        }

    }
}