package vn.mgjsc.sdk.api


import vn.mgjsc.sdk.constants.Constants
import vn.mgjsc.sdk.SDKManager
import vn.mgjsc.sdk.api.request.MGExecutors
import java.util.concurrent.Executor

/**
 * Create by weed songpq on 20/11/2019.
 */

open class BaseInteractor {
    companion object {
        @JvmStatic
        private var workerExecutor: Executor? = MGExecutors.worker()

        @JvmStatic
        private var callbackExecutor: Executor? = MGExecutors.mainThread()

        @JvmStatic
        public fun submitSubTask(taskRunnable: Runnable) {
            workerExecutor?.execute(taskRunnable)
        }

        @JvmStatic
        public fun mainThreadCallback(callbackRunnable: Runnable) {
            callbackExecutor?.execute(callbackRunnable)
        }

//        @JvmStatic
//        fun getDomainAPI():String
//        {
//            var domainApi : String = Constants.BASE_URL
//            if(AccountManager.getInstance().configs !=null && !AccountManager.getInstance().configs?.domainApi.isNullOrEmpty())
//                domainApi = AccountManager.getInstance().configs!!.domainApi
//            else
//            {
//                val config = AccountManager.getInstance().getPreviousConfigs()
//                if (config != null && !config?.domainApi.isNullOrEmpty())
//                    domainApi = config!!.domainApi
//            }
//            return domainApi
//        }
        @JvmStatic
        public var isHTTPS = true

        @JvmStatic
        fun proccessExceptionConnection(e: Exception) : Exception
        {
            e.message?.let{
                if (!it.contains(" -- statusCode:",ignoreCase = true)&&(it.contains("Handshake",true) || it.contains("Hand",true) || it.contains("shake",true)))
                {
                    var message = "Lỗi kết nối mạng.Vui lòng kiểm tra mạng và thử lại";
                    if(Constants.isDebug)
                        message = message + it;
                    BaseInteractor.isHTTPS = false
                    val ee= java.lang.Exception(message)
//                mainThreadCallback(Runnable { listener.invoke(null, ee) })
                    return ee
                }else{
//                mainThreadCallback(Runnable { listener.invoke(null, e) })
                    return e
                }
            }
            return e
        }

        @JvmStatic
        fun getDomainAPI():String
        {

            if(isHTTPS) {
                var domainApi: String = Constants.BASE_URL
                if (SDKManager.baseConfigModel != null && SDKManager.baseConfigModel?.DomainAPI != null)
                    domainApi = SDKManager.baseConfigModel!!.DomainAPI
                return domainApi
            }
            else
            {
                var domainApi: String = Constants.BASE_HTTP_URL
                if (SDKManager.baseConfigModel != null && SDKManager.baseConfigModel?.DomainAPI != null)
                    domainApi = SDKManager.baseConfigModel!!.DomainAPI
                if(domainApi.startsWith("https://"))
                    domainApi = domainApi.replaceFirst("https://","https://")
                return domainApi
            }
        }

    }
//    constructor() {
//        this.workerExecutor = MiGameExecutors.worker()
//        this.callbackExecutor = MiGameExecutors.mainThread()
//    }

    constructor() {

    }

    init {
//        BaseInteractor.workerExecutor = MiGameExecutors.worker()
//        BaseInteractor.callbackExecutor = MiGameExecutors.mainThread()
    }

//    protected fun submitSubTask(taskRunnable: Runnable) {
//        workerExecutor?.execute(taskRunnable)
//    }

//    protected fun mainThreadCallback(callbackRunnable: Runnable) {
//        callbackExecutor?.execute(callbackRunnable)
//    }
}