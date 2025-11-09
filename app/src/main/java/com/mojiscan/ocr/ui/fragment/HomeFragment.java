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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

        viewModel = new ViewModelProvider(this).get(TranscriptionViewModel.class);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        searchEditText = view.findViewById(R.id.searchEditText);

        adapter = new TranscriptionAdapter();
        adapter.setOnItemClickListener(transcription -> {
            if (getView() != null) {
                HomeFragmentDirections.ActionHomeFragmentToDetailFragment action =
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(transcription.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        adapter.setOnDeleteClickListener(transcription -> {
            viewModel.deleteTranscription(transcription.getId());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if (getView() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_addTranscriptionFragment);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        viewModel.getTranscriptions().observe(getViewLifecycleOwner(), transcriptions -> {
            adapter.submitList(transcriptions);
            if (transcriptions == null || transcriptions.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if (getView() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_settingsFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

