package com.mojiscan.ocr.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.ui.adapter.TranscriptionAdapter;
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel;

import java.util.List;

public class HomeFragment extends Fragment {
    private TranscriptionViewModel viewModel;
    private TranscriptionAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private TextInputEditText searchEditText;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            navController = Navigation.findNavController(view);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            viewModel = new ViewModelProvider(requireActivity()).get(TranscriptionViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() == null) {
                try {
                    activity.setSupportActionBar(toolbar);
                    toolbar.setTitle(R.string.app_name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        searchEditText = view.findViewById(R.id.searchEditText);

        if (recyclerView == null || emptyTextView == null || searchEditText == null) {
            return;
        }

        adapter = new TranscriptionAdapter();
        adapter.setOnItemClickListener(transcription -> {
            if (navController != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", transcription.getId());
                    navController.navigate(R.id.action_homeFragment_to_detailFragment, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        adapter.setOnDeleteClickListener(transcription -> {
            if (viewModel != null) {
                viewModel.deleteTranscription(transcription.getId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                if (navController != null) {
                    try {
                        navController.navigate(R.id.action_homeFragment_to_addTranscriptionFragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (viewModel != null) {
                        viewModel.setSearchQuery(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (viewModel != null) {
            viewModel.getTranscriptions().observe(getViewLifecycleOwner(), transcriptions -> {
                if (adapter != null && recyclerView != null && emptyTextView != null) {
                    adapter.submitList(transcriptions);
                    if (transcriptions == null || transcriptions.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyTextView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if (navController != null) {
                try {
                    navController.navigate(R.id.action_homeFragment_to_settingsFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
