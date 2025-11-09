package com.mojiscan.ocr.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.mojiscan.ocr.R;

public class DataHandlingFragment extends Fragment {
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_handling, container, false);
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
                    toolbar.setTitle(R.string.data_handling);
                    toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                toolbar.setTitle(R.string.data_handling);
                toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
            }
        }

        TextView contentTextView = view.findViewById(R.id.contentTextView);
        contentTextView.setText(getDataHandlingContent());
    }

    private String getDataHandlingContent() {
        return "データの取り扱いについて\n\n" +
                "1. データの送信について\n" +
                "本アプリは、OCR（文字認識）および音声文字起こし処理のために、ユーザーが選択した画像ファイルや音声ファイルをサーバーに送信します。\n\n" +
                "2. サーバーでの処理\n" +
                "送信されたファイルは、自社サーバー上でGoogleのGemini APIを使用してOCR処理または音声文字起こし処理が行われます。処理は自動的に実行され、処理結果（抽出されたテキスト）のみがアプリに返されます。\n\n" +
                "3. ファイルの削除について\n" +
                "OCR処理または音声文字起こし処理が完了した瞬間に、サーバー上のファイルは即座に削除されます。ファイルは一時的な処理のためだけに使用され、サーバー上に保存されることはありません。\n\n" +
                "4. データの保存について\n" +
                "処理結果として抽出されたテキストは、アプリ内のローカルデータベースに保存されます。このデータはユーザーの端末内にのみ保存され、外部サーバーには送信されません。\n\n" +
                "5. セキュリティについて\n" +
                "ファイルの送信はHTTPS通信により暗号化されて行われます。また、サーバー上でのファイルは処理完了後に即座に削除されるため、データ漏洩のリスクを最小限に抑えています。\n\n" +
                "6. データの削除\n" +
                "アプリ内に保存されたテキストデータは、ユーザーがアプリ内で削除することができます。また、アプリをアンインストールすることで、すべてのデータが削除されます。\n\n" +
                "更新日: 2025年11月9日";
    }
}
