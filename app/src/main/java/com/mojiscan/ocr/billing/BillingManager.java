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
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            billingState.postValue(BillingState.Ready);
            queryProductDetails();
            queryPurchases();
        } else {
            String errorMessage = getErrorMessage(responseCode, billingResult.getDebugMessage());
            billingState.postValue(new BillingState.Error(errorMessage));
        }
    }
    
    private String getErrorMessage(int responseCode, String debugMessage) {
        switch (responseCode) {
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                return "アプリ内課金が利用できません。Google Play ストアを更新してください。";
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                return "サービスが一時的に利用できません。後でもう一度お試しください。";
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "サービスが切断されました。アプリを再起動してください。";
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "開発者エラー: プロダクトがGoogle Play Consoleで正しく設定されているか確認してください。";
            default:
                return debugMessage != null ? debugMessage : "エラーが発生しました (コード: " + responseCode + ")";
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
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                for (ProductDetails productDetails : productDetailsList) {
                    productDetailsMap.put(productDetails.getProductId(), productDetails);
                }
                // プロダクトが見つからない場合のエラーハンドリング
                if (productDetailsList.isEmpty() && !productDetailsMap.isEmpty()) {
                    // プロダクトが見つからないが、これは正常（Google Play Consoleで設定が必要）
                }
            } else if (responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                String errorMsg = "プロダクトがGoogle Play Consoleで設定されていません。以下のプロダクトIDを設定してください: " + 
                    String.join(", ", DONATION_PRODUCT_IDS);
                billingState.postValue(new BillingState.Error(errorMsg));
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
            } else {
                // 既に確認済みの場合は成功を通知
                purchaseState.postValue(new PurchaseState.Success(purchase));
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            purchaseState.postValue(new PurchaseState.Error("購入が保留中です。後でもう一度確認してください。"));
        }
    }
    
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.getPurchaseToken())
            .build();
        
        billingClient.acknowledgePurchase(params, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // 購入の確認が完了
                purchaseState.postValue(new PurchaseState.Success(purchase));
            } else {
                String errorMsg = "購入の確認に失敗しました: " + billingResult.getDebugMessage();
                purchaseState.postValue(new PurchaseState.Error(errorMsg));
            }
        });
    }
    
    public void launchBillingFlow(Activity activity, int amount) {
        if (billingState.getValue() != BillingState.Ready) {
            purchaseState.postValue(new PurchaseState.Error("アプリ内課金が準備できていません。しばらく待ってからもう一度お試しください。"));
            return;
        }
        
        String productId = getClosestProductId(amount);
        ProductDetails productDetails = productDetailsMap.get(productId);
        
        if (productDetails == null) {
            String errorMsg = "プロダクト「" + productId + "」が見つかりません。\n" +
                "Google Play Consoleで以下のプロダクトIDを設定してください:\n" +
                "donation_10, donation_50, donation_100, donation_500, donation_1000, donation_5000, donation_10000";
            purchaseState.postValue(new PurchaseState.Error(errorMsg));
            return;
        }
        
        purchaseState.postValue(PurchaseState.Processing);
        
        // Billing Library 5.0+ のAPIを使用（In-app product）
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        
        // In-app product用のパラメータ
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build();
        productDetailsParamsList.add(productDetailsParams);
        
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build();
        
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        int responseCode = billingResult.getResponseCode();
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            String errorMsg = getErrorMessage(responseCode, billingResult.getDebugMessage());
            purchaseState.postValue(new PurchaseState.Error(errorMsg));
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

