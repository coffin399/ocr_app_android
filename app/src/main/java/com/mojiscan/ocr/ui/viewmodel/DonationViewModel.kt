package com.mojiscan.ocr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mojiscan.ocr.billing.BillingManager
import com.mojiscan.ocr.billing.BillingState
import com.mojiscan.ocr.billing.PurchaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DonationViewModel(application: Application) : AndroidViewModel(application) {
    private val billingManager = BillingManager(application)
    
    private val _donationAmount = MutableStateFlow("10")
    val donationAmount: StateFlow<String> = _donationAmount.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val billingState = billingManager.billingState
    val purchaseState = billingManager.purchaseState
    
    fun setDonationAmount(amount: String) {
        _donationAmount.value = amount
        _errorMessage.value = null
    }
    
    fun validateAmount(): Boolean {
        val amount = _donationAmount.value.toIntOrNull()
        return when {
            amount == null -> {
                _errorMessage.value = "金額を入力してください"
                false
            }
            amount < 10 -> {
                _errorMessage.value = "最小金額は10円です"
                false
            }
            amount > 10000 -> {
                _errorMessage.value = "最大金額は10,000円です（それ以上の場合は複数回に分けてお願いします）"
                false
            }
            else -> {
                _errorMessage.value = null
                true
            }
        }
    }
    
    fun processDonation(activity: android.app.Activity) {
        if (validateAmount()) {
            val amount = _donationAmount.value.toInt()
            billingManager.launchBillingFlow(activity, amount)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        // BillingManagerはActivityのライフサイクルで管理されるため、ここでは特に処理しない
    }
}

