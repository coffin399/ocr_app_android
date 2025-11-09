package com.mojiscan.ocr.billing;

import android.app.Activity;
import android.content.Context;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.billingclient.api.*;

import java.util.*;

public class BillingManager implements DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener {
    private BillingClient billingClient;
    private MutableLiveData<BillingState> billingState = new MutableLiveData<>(BillingState.NotReady);
    private MutableLiveData<PurchaseState> purchaseState = new MutableLiveData<>(PurchaseState.Idle);
    private Map<String, ProductDetails> productDetailsMap = new HashMap<>();
    
    private static final List<String> DONATION_PRODUCT_IDS = Arrays.asList(
        "donation_10", "donation_50", "donation_100", "donation_500",
        "donation_1000", "donation_5000", "donation_10000"
    );
    
    public BillingManager(Context context) {
        initializeBillingClient(context);
    }
    
    private void initializeBillingClient(Context context) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build();
        
        billingClient.startConnection(this);
    }
    
    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            billingState.postValue(BillingState.Ready);
            queryProductDetails();
            queryPurchases();
        } else {
            billingState.postValue(new BillingState.Error(billingResult.getDebugMessage()));
        }
    }
    
    @Override
    public void onBillingServiceDisconnected() {
        billingState.postValue(BillingState.NotReady);
    }
    
    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        for (String productId : DONATION_PRODUCT_IDS) {
            productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        }
        
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build();
        
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (ProductDetails productDetails : productDetailsList) {
                    productDetailsMap.put(productDetails.getProductId(), productDetails);
                }
            }
        });
    }
    
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            if (purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            purchaseState.postValue(PurchaseState.Cancelled);
        } else {
            purchaseState.postValue(new PurchaseState.Error(billingResult.getDebugMessage()));
        }
    }
    
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                acknowledgePurchase(purchase);
            }
            purchaseState.postValue(new PurchaseState.Success(purchase));
        }
    }
    
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.getPurchaseToken())
            .build();
        
        billingClient.acknowledgePurchase(params, billingResult -> {
            // 購入の確認が完了
        });
    }
    
    public void launchBillingFlow(Activity activity, int amount) {
        if (billingState.getValue() != BillingState.Ready) {
            purchaseState.postValue(new PurchaseState.Error("Billing not ready"));
            return;
        }
        
        String productId = getClosestProductId(amount);
        ProductDetails productDetails = productDetailsMap.get(productId);
        
        if (productDetails == null) {
            purchaseState.postValue(new PurchaseState.Error("Product not found. Please configure products in Google Play Console."));
            return;
        }
        
        purchaseState.postValue(PurchaseState.Processing);
        
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = Arrays.asList(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        );
        
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build();
        
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            purchaseState.postValue(new PurchaseState.Error("Failed to launch billing flow"));
        }
    }
    
    private String getClosestProductId(int amount) {
        if (amount <= 10) return "donation_10";
        if (amount <= 50) return "donation_50";
        if (amount <= 100) return "donation_100";
        if (amount <= 500) return "donation_500";
        if (amount <= 1000) return "donation_1000";
        if (amount <= 5000) return "donation_5000";
        return "donation_10000";
    }
    
    private void queryPurchases() {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build();
        
        billingClient.queryPurchasesAsync(params, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            }
        });
    }
    
    public LiveData<BillingState> getBillingState() {
        return billingState;
    }
    
    public LiveData<PurchaseState> getPurchaseState() {
        return purchaseState;
    }
    
    @Override
    public void onDestroy(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
    
    // BillingState classes
    public static class BillingState {
        public static final BillingState NotReady = new BillingState();
        public static final BillingState Ready = new BillingState();
        
        public static class Error extends BillingState {
            private String message;
            public Error(String message) {
                this.message = message;
            }
            public String getMessage() {
                return message;
            }
        }
    }
    
    // PurchaseState classes
    public static class PurchaseState {
        public static final PurchaseState Idle = new PurchaseState();
        public static final PurchaseState Processing = new PurchaseState();
        public static final PurchaseState Cancelled = new PurchaseState();
        
        public static class Success extends PurchaseState {
            private Purchase purchase;
            public Success(Purchase purchase) {
                this.purchase = purchase;
            }
            public Purchase getPurchase() {
                return purchase;
            }
        }
        
        public static class Error extends PurchaseState {
            private String message;
            public Error(String message) {
                this.message = message;
            }
            public String getMessage() {
                return message;
            }
        }
    }
}

