package com.mojiscan.ocr.billing

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class BillingManager(private val context: Context) : DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener {
    
    private var billingClient: BillingClient? = null
    private val _billingState = MutableStateFlow<BillingState>(BillingState.NotReady)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()
    
    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()
    
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()
    
    // 事前定義された寄付金額オプション（Google Play Consoleで設定する商品ID）
    // 実際の実装では、Google Play Consoleでこれらの商品IDを設定する必要があります
    private val donationProductIds = listOf(
        "donation_10",   // 10円
        "donation_50",   // 50円
        "donation_100",  // 100円
        "donation_500",  // 500円
        "donation_1000", // 1000円
        "donation_5000", // 5000円
        "donation_10000" // 10000円
    )
    
    init {
        initializeBillingClient()
    }
    
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        
        billingClient?.startConnection(this)
    }
    
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _billingState.value = BillingState.Ready
            queryProductDetails()
            queryPurchases()
        } else {
            _billingState.value = BillingState.Error(billingResult.debugMessage)
        }
    }
    
    override fun onBillingServiceDisconnected() {
        _billingState.value = BillingState.NotReady
    }
    
    private fun queryProductDetails() {
        val productList = donationProductIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { productDetails ->
                    productDetailsMap[productDetails.productId] = productDetails
                }
            }
        }
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseState.value = PurchaseState.Cancelled
            }
            else -> {
                _purchaseState.value = PurchaseState.Error(billingResult.debugMessage)
            }
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
            _purchaseState.value = PurchaseState.Success(purchase)
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // 購入の確認が完了
            }
        }
    }
    
    fun launchBillingFlow(activity: Activity, amount: Int) {
        if (_billingState.value != BillingState.Ready) {
            _purchaseState.value = PurchaseState.Error("Billing not ready")
            return
        }
        
        // 金額に最も近い商品IDを選択
        val productId = getClosestProductId(amount)
        val productDetails = productDetailsMap[productId]
        
        if (productDetails == null) {
            _purchaseState.value = PurchaseState.Error("Product not found. Please configure products in Google Play Console.")
            return
        }
        
        _purchaseState.value = PurchaseState.Processing
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        val billingResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
        if (billingResult?.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseState.value = PurchaseState.Error("Failed to launch billing flow")
        }
    }
    
    private fun getClosestProductId(amount: Int): String {
        return when {
            amount <= 10 -> "donation_10"
            amount <= 50 -> "donation_50"
            amount <= 100 -> "donation_100"
            amount <= 500 -> "donation_500"
            amount <= 1000 -> "donation_1000"
            amount <= 5000 -> "donation_5000"
            else -> "donation_10000"
        }
    }
    
    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        
        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
        }
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        billingClient?.endConnection()
    }
}

sealed class BillingState {
    object NotReady : BillingState()
    object Ready : BillingState()
    data class Error(val message: String) : BillingState()
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Processing : PurchaseState()
    data class Success(val purchase: Purchase) : PurchaseState()
    object Cancelled : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
