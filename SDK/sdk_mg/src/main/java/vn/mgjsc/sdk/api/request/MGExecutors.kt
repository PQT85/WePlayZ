package vn.mgjsc.sdk.api.request

import android.os.Handler
import android.os.Looper
import android.os.Process
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * Create by weed songpq on 20/11/2019.
 */

object MGExecutors {
    private val WORKER_THREAD_EXECUTOR =
        Executors.newFixedThreadPool(3) { r ->
            Thread(Runnable {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                r.run()
            })
        }

    private val MAIN_THREAD_EXECUTOR = object : Executor {
        private val handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }

    fun worker(): Executor {
        return WORKER_THREAD_EXECUTOR
    }

    fun mainThread(): Executor {
        return MAIN_THREAD_EXECUTOR
    }
}