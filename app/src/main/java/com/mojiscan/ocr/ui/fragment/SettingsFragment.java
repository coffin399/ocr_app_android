package com.mojiscan.ocr.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.mojiscan.ocr.R;

public class SettingsFragment extends Fragment {
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() == null) {
                try {
                    activity.setSupportActionBar(toolbar);
                    toolbar.setTitle(R.string.settings);
                    toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                toolbar.setTitle(R.string.settings);
                toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
            }
        }

        MaterialCardView updateInfoCard = view.findViewById(R.id.updateInfoCard);
        MaterialCardView feedbackCard = view.findViewById(R.id.feedbackCard);
        MaterialCardView donationCard = view.findViewById(R.id.donationCard);
        MaterialCardView dataHandlingCard = view.findViewById(R.id.dataHandlingCard);
        MaterialCardView termsOfServiceCard = view.findViewById(R.id.termsOfServiceCard);
        MaterialCardView privacyPolicyCard = view.findViewById(R.id.privacyPolicyCard);

        updateInfoCard.setOnClickListener(v -> {
            String url = getString(R.string.github_url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        feedbackCard.setOnClickListener(v -> {
            String url = getString(R.string.feedback_url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        donationCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_settingsFragment_to_donationFragment);
        });

        dataHandlingCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_settingsFragment_to_dataHandlingFragment);
        });

        termsOfServiceCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_settingsFragment_to_termsOfServiceFragment);
        });

        privacyPolicyCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_settingsFragment_to_privacyPolicyFragment);
        });
    }
}
