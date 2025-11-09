package com.mojiscan.ocr.ui.viewmodel;

import android.app.Activity;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mojiscan.ocr.billing.BillingManager;
import com.mojiscan.ocr.billing.BillingManager.BillingState;
import com.mojiscan.ocr.billing.BillingManager.PurchaseState;

public class DonationViewModel extends AndroidViewModel {
    private BillingManager billingManager;
    private MutableLiveData<String> donationAmount = new MutableLiveData<>("10");
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DonationViewModel(Application application) {
        super(application);
        billingManager = new BillingManager(application);
    }

    public LiveData<String> getDonationAmount() {
        return donationAmount;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<BillingState> getBillingState() {
        return billingManager.getBillingState();
    }

    public LiveData<PurchaseState> getPurchaseState() {
        return billingManager.getPurchaseState();
    }

    public void setDonationAmount(String amount) {
        donationAmount.setValue(amount);
        errorMessage.setValue(null);
    }

    public boolean validateAmount() {
        String amountStr = donationAmount.getValue();
        if (amountStr == null || amountStr.trim().isEmpty()) {
            errorMessage.setValue("金額を入力してください");
            return false;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount < 10) {
                errorMessage.setValue("最小金額は10円です");
                return false;
            }
            if (amount > 10000) {
                errorMessage.setValue("最大金額は10,000円です（それ以上の場合は複数回に分けてお願いします）");
                return false;
            }
            errorMessage.setValue(null);
            return true;
        } catch (NumberFormatException e) {
            errorMessage.setValue("有効な金額を入力してください");
            return false;
        }
    }

    public void processDonation(Activity activity) {
        if (validateAmount()) {
            try {
                int amount = Integer.parseInt(donationAmount.getValue());
                billingManager.launchBillingFlow(activity, amount);
            } catch (NumberFormatException e) {
                errorMessage.setValue("有効な金額を入力してください");
            }
        }
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}

