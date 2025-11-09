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

public class PrivacyPolicyFragment extends Fragment {
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false);
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
                    toolbar.setTitle(R.string.privacy_policy);
                    toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                toolbar.setTitle(R.string.privacy_policy);
                toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
            }
        }

        TextView contentTextView = view.findViewById(R.id.contentTextView);
        contentTextView.setText(getPrivacyPolicyContent());
    }

    private String getPrivacyPolicyContent() {
        return "プライバシーポリシー\n\n" +
                "最終更新日: 2025年11月9日\n\n" +
                "1. はじめに\n" +
                "本プライバシーポリシーは、本アプリ（以下「本アプリ」といいます。）の利用において、ユーザーの個人情報がどのように取り扱われるかを説明するものです。\n\n" +
                "2. 収集する情報\n" +
                "本アプリは、以下の情報を収集する場合があります。\n" +
                "• 画像ファイルおよび音声ファイル（OCR処理および文字起こし処理のため）\n" +
                "• 処理結果として抽出されたテキスト（アプリ内でのみ保存）\n" +
                "• アプリの利用状況に関する情報（クラッシュレポート、パフォーマンスデータなど）\n\n" +
                "3. 情報の利用目的\n" +
                "収集した情報は、以下の目的でのみ使用されます。\n" +
                "• OCR処理および音声文字起こし処理の提供\n" +
                "• アプリの機能向上および不具合の修正\n" +
                "• サービスの品質向上\n\n" +
                "4. 情報の保存および削除\n" +
                "• アップロードされたファイル: OCR処理または音声文字起こし処理が完了した瞬間に、サーバー上から即座に削除されます。ファイルはサーバー上に保存されることはありません。\n" +
                "• 処理結果のテキスト: ユーザーの端末内のローカルデータベースにのみ保存されます。外部サーバーには送信されません。\n" +
                "• ユーザーは、アプリ内でテキストデータを削除することができます。\n\n" +
                "5. 第三者への情報提供\n" +
                "本アプリは、以下の場合を除き、ユーザーの情報を第三者に提供することはありません。\n" +
                "• 法令に基づく開示が求められた場合\n" +
                "• 人の生命、身体または財産の保護のために必要がある場合\n" +
                "• 本アプリの処理のため、GoogleのGemini APIを使用する場合（ファイルは処理後に即座に削除されます）\n\n" +
                "6. データのセキュリティ\n" +
                "本アプリは、ユーザーの情報のセキュリティを確保するために、以下の対策を講じています。\n" +
                "• ファイルの送信はHTTPS通信により暗号化\n" +
                "• サーバー上でのファイルは処理完了後に即座に削除\n" +
                "• 処理結果のテキストは端末内のローカルデータベースにのみ保存\n\n" +
                "7. ユーザーの権利\n" +
                "ユーザーは、以下の権利を有します。\n" +
                "• アプリ内に保存されたテキストデータの閲覧、編集、削除\n" +
                "• アプリのアンインストールによるすべてのデータの削除\n\n" +
                "8. お問い合わせ\n" +
                "本プライバシーポリシーに関するお問い合わせは、アプリ内の「ご要望受付フォーム」からお願いいたします。\n\n" +
                "9. プライバシーポリシーの変更\n" +
                "当方は、必要に応じて、本プライバシーポリシーを変更することがあります。変更があった場合には、アプリ内で通知いたします。";
    }
}
