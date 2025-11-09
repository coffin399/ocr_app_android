package com.mojiscan.ocr.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.billing.BillingManager;
import com.mojiscan.ocr.billing.BillingManager.BillingState;
import com.mojiscan.ocr.billing.BillingManager.PurchaseState;
import com.mojiscan.ocr.ui.viewmodel.DonationViewModel;

public class DonationFragment extends Fragment {
    private DonationViewModel viewModel;
    private NavController navController;
    private TextInputEditText amountEditText;
    private TextInputLayout amountInputLayout;
    private ChipGroup amountChipGroup;
    private Button donateButton;
    private MaterialCardView errorCard;
    private TextView errorTextView;
    private MaterialCardView processingCard;
    private TextView processingTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(DonationViewModel.class);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() == null) {
                try {
                    activity.setSupportActionBar(toolbar);
                    toolbar.setTitle(R.string.donation_title);
                    toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                toolbar.setTitle(R.string.donation_title);
                toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
            }
        }

        amountEditText = view.findViewById(R.id.amountEditText);
        amountInputLayout = view.findViewById(R.id.amountInputLayout);
        amountChipGroup = view.findViewById(R.id.amountChipGroup);
        donateButton = view.findViewById(R.id.donateButton);
        errorCard = view.findViewById(R.id.errorCard);
        errorTextView = view.findViewById(R.id.errorTextView);
        processingCard = view.findViewById(R.id.processingCard);
        processingTextView = view.findViewById(R.id.processingTextView);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDonationAmount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Chip amount100Chip = view.findViewById(R.id.amount100Chip);
        Chip amount500Chip = view.findViewById(R.id.amount500Chip);
        Chip amount1000Chip = view.findViewById(R.id.amount1000Chip);
        Chip amount5000Chip = view.findViewById(R.id.amount5000Chip);

        amount100Chip.setOnClickListener(v -> amountEditText.setText("100"));
        amount500Chip.setOnClickListener(v -> amountEditText.setText("500"));
        amount1000Chip.setOnClickListener(v -> amountEditText.setText("1000"));
        amount5000Chip.setOnClickListener(v -> amountEditText.setText("5000"));

        donateButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                viewModel.processDonation(getActivity());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(error);
            } else {
                errorCard.setVisibility(View.GONE);
            }
        });

        viewModel.getPurchaseState().observe(getViewLifecycleOwner(), purchaseState -> {
            if (purchaseState == PurchaseState.Processing) {
                processingCard.setVisibility(View.VISIBLE);
                processingTextView.setText(getString(R.string.donation_processing));
                donateButton.setEnabled(false);
            } else if (purchaseState instanceof PurchaseState.Success) {
                processingCard.setVisibility(View.GONE);
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(getString(R.string.donation_success));
                errorTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                donateButton.setEnabled(true);
            } else if (purchaseState instanceof PurchaseState.Error) {
                processingCard.setVisibility(View.GONE);
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(((PurchaseState.Error) purchaseState).getMessage());
                errorTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
                donateButton.setEnabled(true);
            } else if (purchaseState == PurchaseState.Cancelled) {
                processingCard.setVisibility(View.GONE);
                donateButton.setEnabled(true);
            } else {
                processingCard.setVisibility(View.GONE);
                donateButton.setEnabled(true);
            }
        });

        viewModel.getBillingState().observe(getViewLifecycleOwner(), billingState -> {
            if (billingState instanceof BillingState.Error) {
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(((BillingState.Error) billingState).getMessage());
                donateButton.setEnabled(false);
            } else if (billingState == BillingState.NotReady) {
                errorCard.setVisibility(View.VISIBLE);
                errorTextView.setText(getString(R.string.donation_not_available));
                donateButton.setEnabled(false);
            } else {
                errorCard.setVisibility(View.GONE);
                donateButton.setEnabled(true);
            }
        });
    }
}
