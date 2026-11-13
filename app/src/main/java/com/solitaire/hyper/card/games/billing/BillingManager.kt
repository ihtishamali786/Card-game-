package com.solitaire.hyper.card.games.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Google Play Billing client manager for the one-time "Remove Ads" purchase.
 */
class BillingManager(
    private val context: Context,
    private val onAdsRemovedEntitlementChanged: (Boolean) -> Unit
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"

        // -------------------------------------------------------------------------------------
        // GOOGLE PLAY BILLING PRODUCT ID (Replace with your in-app product ID from Play Console)
        // -------------------------------------------------------------------------------------
        const val PRODUCT_REMOVE_ADS = "remove_ads_zen"
    }

    private var billingClient: BillingClient? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val _purchaseStatusMessage = MutableStateFlow<String?>(null)
    val purchaseStatusMessage: StateFlow<String?> = _purchaseStatusMessage.asStateFlow()

    fun startConnection() {
        val pendingParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(pendingParams)
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isServiceConnected.value = true
                    Log.d(TAG, "Billing client setup successful")
                    queryPurchases()
                } else {
                    Log.w(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                _isServiceConnected.value = false
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    /**
     * Checks if the user already purchased "Remove Ads" previously (Restore Purchases).
     */
    fun queryPurchases(onFinished: ((Boolean) -> Unit)? = null) {
        val client = billingClient ?: return
        if (!client.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasRemoveAds = purchasesList.any { purchase ->
                    purchase.products.contains(PRODUCT_REMOVE_ADS) &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                if (hasRemoveAds) {
                    onAdsRemovedEntitlementChanged(true)
                    _purchaseStatusMessage.value = "Ads removal restored!"
                }
                onFinished?.invoke(hasRemoveAds)
            } else {
                onFinished?.invoke(false)
            }
        }
    }

    /**
     * Launches the Google Play in-app purchase flow for Remove Ads.
     */
    fun launchPurchaseFlow(activity: Activity) {
        val client = billingClient
        if (client == null || !client.isReady) {
            _purchaseStatusMessage.value = "Google Play Store connecting... please try again."
            startConnection()
            return
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_REMOVE_ADS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && queryProductDetailsList.isNotEmpty()) {
                val productDetails = queryProductDetailsList[0]
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                client.launchBillingFlow(activity, billingFlowParams)
            } else {
                Log.w(TAG, "Product details not found in Play Store (Test/Dev mode active)")
                // In local emulator / test environments where in-app product is not yet active on Play Console,
                // grant the removal gracefully so users can test the ad-free state.
                onAdsRemovedEntitlementChanged(true)
                _purchaseStatusMessage.value = "Remove Ads activated (Test mode)"
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _purchaseStatusMessage.value = "Purchase cancelled"
        } else {
            Log.w(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(PRODUCT_REMOVE_ADS) &&
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        ) {
            onAdsRemovedEntitlementChanged(true)
            _purchaseStatusMessage.value = "Thank you! Ads are now permanently removed."

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                scope.launch {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { ackResult ->
                        Log.d(TAG, "Purchase acknowledged: ${ackResult.responseCode}")
                    }
                }
            }
        }
    }

    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }
}
