package vn.mgjsc.sdk.iapsdk


import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import vn.mgjsc.sdk.R
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class IAPSDKManager(val activity: Activity, val base64Key: String, val listener: EventIAPManager) {
    //songpq
    private lateinit var  billingClient: BillingClient
    public fun getBillingClient() : BillingClient?{
        return billingClient
    }
    public var isReady = false
    // Data class to wrap responseCode and purchases
    data class PurchaseResult(val responseCode: Int, val purchases: List<Purchase>?)
//    // Channel to receive PurchaseResult
    private val purchaseChannel: Channel<PurchaseResult> = Channel(Channel.UNLIMITED)

    init {
        setupBillingClient(activity)
    }

    private fun setupBillingClient(context: Context) {
        //songpq
        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener { billingResult, purchases ->
                    purchaseChannel.trySend(PurchaseResult(billingResult.responseCode, purchases))
                }.build()
    }

    fun startConnectionIAP() {
//songpq
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {


                    val params = QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                    billingClient.queryPurchasesAsync(params) {billingResult,purchases->
                        if (!purchases.isNullOrEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    purchases.forEach {
                                        if (isPurchaseSignatureValid(it)) {
                                            consume(it.purchaseToken)
                                            listener.onPaymentSuccess(it)
                                        } else {
                                            listener.onPaymentFailed(activity.getString(R.string.mg_purchase_signature_not_validity))
                                        }
                                    }
                                } catch (e: Exception) {
                                    listener.onPaymentFailed(e.message
                                        ?: activity.getString(R.string.mg_text_error_payment))
                                }
                            }
                        } else {
                            isReady = true
                            listener.onPaymentReady()
                        }
                    }

//                    val purchases = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
//                            .purchasesList

                } else {
                    listener.onPaymentFailed("responseCode: ${billingResult.responseCode} " + activity.getString(R.string.mg_connection_errors_payment))
                }
            }

            override fun onBillingServiceDisconnected() {
                if(listener != null)
                    listener.onPaymentFailed(activity.getString(R.string.mg_connection_errors_payment))
            }
        })
    }

    fun callIAP(productID: String) {
//        Toast.makeText(activity,"innit5555", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // product id you want to buy
                val productList = listOf( QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productID)
                    .setProductType(BillingClient.ProductType.INAPP) // hoặc SUBS
                    .build()
                )

                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build();
                //val params = SkuDetailsParams.newBuilder().apply {
                  //  setSkusList(skuList)
                   // setType(BillingClient.SkuType.INAPP)
               // }.build()
                val productDetails = queryProductDetails(params)

                // query the list of SkuDetails
                if (productDetails.isNullOrEmpty()) {
                    listener.onPaymentFailed(activity.getString(R.string.mg_no_matching_packages_found).replace("{s}",productID))
                } else {
                    val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails[0])
                            .build()
                    val billingParamFlow = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productDetailsParams)).build()

                    // start the purchase flow and get the result
                    val purchases: List<Purchase>? = startPurchaseFlow(activity, billingParamFlow).purchases

                    // consume the token
                    if (purchases.isNullOrEmpty()) {
                        listener.onPaymentFailed(activity.getString(R.string.mg_payment_failed))
                    } else {
                        purchases.forEach {
                            if (isPurchaseSignatureValid(it)) {
                                consume(it.purchaseToken)
                                listener.onPaymentSuccess(it)
                            } else {
                                listener.onPaymentFailed(activity.getString(R.string.mg_purchase_signature_not_validity))
                            }
                        }
                    }
                }
                startConnectionIAP()
            } catch (e: Exception) {
                listener.onPaymentFailed(e.message
                        ?: activity.getString(R.string.mg_payment_failed))
            }
        }
    }

    /**
     * Checks purchase signature validity
     */
    private fun isPurchaseSignatureValid(purchase: Purchase): Boolean {
        return SecuritySDK.verifyPurchase(
                base64Key, purchase.originalJson, purchase.signature
        )
    }

    private suspend  fun queryProductDetails(params: QueryProductDetailsParams) : List<ProductDetails> = suspendCoroutine{ continuation ->
        billingClient!!.queryProductDetailsAsync(params) { billingResult,productDetailsList ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                if(!productDetailsList.isNullOrEmpty())
                {
                    continuation.resume(productDetailsList)
                }else{
                    continuation.resumeWithException(Exception (activity.getString(R.string.mg_no_matching_packages_found)))
                }
            }else {
                continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(R.string.mg_text_unknown_error)}"))
            }
        }

    }

    private suspend fun querySkuDetails(params: SkuDetailsParams): List<SkuDetails> = suspendCoroutine { continuation ->
        billingClient!!.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!skuDetailsList.isNullOrEmpty()) {
                    continuation.resume(skuDetailsList)
                } else {
                    continuation.resumeWithException(Exception(activity.getString(R.string.mg_no_matching_packages_found)))
                }
            } else {
                continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(R.string.mg_text_unknown_error)}"))
            }
        }
    }

    suspend fun queryPurchaseHistory(): MutableList<PurchaseHistoryRecord>? {
        return suspendCoroutine { continuation ->
            billingClient!!.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(purchases)
                } else {
                    continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(R.string.mg_text_unknown_error)}"))
                }
            }
        }
    }

    private suspend fun startPurchaseFlow(activity: Activity, params: BillingFlowParams): PurchaseResult {
        billingClient!!.launchBillingFlow(activity, params)
        return purchaseChannel.receive()
    }

    private suspend fun consume(purchaseToken: String): String {
        val consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build()
        return suspendCoroutine { continuation ->
            billingClient!!.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        continuation.resume(purchaseToken)
                    }
                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                        continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(
                            R.string.mg_payments_are_being_processed_please_try_again_in_a_few_minutes)}"))
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(R.string.mg_you_have_canceled_your_payment)}"))
                    }
                    else -> {
                        continuation.resumeWithException(Exception("responseCode: ${billingResult.responseCode} - ${activity.getString(R.string.mg_payment_failed)}"))
                    }
                }
            }
        }
    }

    interface EventIAPManager {
        fun onPaymentSuccess(purchase: Purchase)
        fun onPaymentReady()
        fun onPaymentFailed(message: String)
    }
}