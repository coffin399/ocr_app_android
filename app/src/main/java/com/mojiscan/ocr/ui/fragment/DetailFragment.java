package com.mojiscan.ocr.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;
import com.mojiscan.ocr.ui.viewmodel.TranscriptionViewModel;

public class DetailFragment extends Fragment {
    private TranscriptionViewModel viewModel;
    private TranscriptionEntity transcription;
    private TextView titleTextView;
    private TextView filenameTextView;
    private TextView processTypeTextView;
    private TextView modelTextView;
    private TextView processingTimeTextView;
    private TextView textTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TranscriptionViewModel.class);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("詳細");
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        }

        titleTextView = view.findViewById(R.id.titleTextView);
        filenameTextView = view.findViewById(R.id.filenameTextView);
        processTypeTextView = view.findViewById(R.id.processTypeTextView);
        modelTextView = view.findViewById(R.id.modelTextView);
        processingTimeTextView = view.findViewById(R.id.processingTimeTextView);
        textTextView = view.findViewById(R.id.textTextView);

        long id = 0;
        if (getArguments() != null) {
            id = getArguments().getLong("id", 0);
        }
        
        if (id > 0) {
            transcription = viewModel.getTranscriptionById(id);
            if (transcription != null) {
                displayTranscription(transcription);
            }
        }
    }

    private void displayTranscription(TranscriptionEntity transcription) {
        titleTextView.setText(transcription.getTitle());
        filenameTextView.setText("ファイル名: " + transcription.getFilename());
        processTypeTextView.setText("処理タイプ: " + transcription.getProcessType());
        modelTextView.setText("モデル: " + transcription.getModel());
        processingTimeTextView.setText("処理時間: " + String.format("%.2f秒", transcription.getProcessingTime()));
        textTextView.setText(transcription.getText());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (transcription == null) return false;

        if (item.getItemId() == R.id.action_copy) {
            copyToClipboard();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            shareText();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("テキスト", transcription.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "コピーしました", Toast.LENGTH_SHORT).show();
    }

    private void shareText() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, transcription.getText());
        startActivity(Intent.createChooser(intent, "共有"));
    }
}
