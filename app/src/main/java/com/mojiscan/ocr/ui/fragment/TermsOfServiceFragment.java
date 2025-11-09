package com.mojiscan.ocr.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.mojiscan.ocr.R;

public class TermsOfServiceFragment extends Fragment {
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terms_of_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Back icon click listener
        View backIcon = view.findViewById(R.id.backIcon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                if (navController != null) {
                    navController.popBackStack();
                }
            });
        }

        TextView contentTextView = view.findViewById(R.id.contentTextView);
        contentTextView.setText(getTermsOfServiceContent());
    }

    private String getTermsOfServiceContent() {
        return "利用規約\n\n" +
                "最終更新日: 2025年11月9日\n\n" +
                "第1条（適用）\n" +
                "本規約は、本アプリの利用に関して、アプリ提供者（以下「当方」といいます）とユーザーとの間の権利義務関係を定めることを目的とし、ユーザーと当方との間の本アプリの利用に関わる一切の関係に適用されるものとします。\n\n" +
                "第2条（利用登録）\n" +
                "本アプリの利用は、利用登録なしで利用することができます。ただし、アプリの機能を利用するには、必要な権限の許可が必要です。\n\n" +
                "第3条（サービスの内容）\n" +
                "本アプリは、画像ファイルからテキストを抽出するOCR機能、および音声ファイルをテキストに変換する文字起こし機能を提供します。これらの機能は、自社サーバー上でGoogleのGemini APIを使用して処理されます。\n\n" +
                "第4条（ファイルの取り扱い）\n" +
                "ユーザーがアップロードしたファイルは、OCR処理または音声文字起こし処理のためだけに使用され、処理完了後は即座にサーバーから削除されます。ファイルはサーバー上に保存されることはありません。\n\n" +
                "第5条（禁止事項）\n" +
                "ユーザーは、本アプリの利用にあたり、以下の行為をしてはなりません。\n" +
                "1. 法令または公序良俗に違反する行為\n" +
                "2. 犯罪行為に関連する行為\n" +
                "3. 本アプリの内容等、本アプリに含まれる著作権、商標権ほか知的財産権を侵害する行為\n" +
                "4. 本アプリ、ほかのユーザー、またはその他第三者のサーバーまたはネットワークの機能を破壊したり、妨害したりする行為\n" +
                "5. 本アプリに関して、反社会的勢力等に直接または間接に利益を供与する行為\n" +
                "6. その他、当方が不適切と判断する行為\n\n" +
                "第6条（本アプリの提供の停止等）\n" +
                "当方は、以下のいずれかの事由があると判断した場合、ユーザーに事前に通知することなく本アプリの全部または一部の提供を停止または中断することができるものとします。\n" +
                "1. 本アプリにかかるコンピュータシステムの保守点検または更新を行う場合\n" +
                "2. 地震、落雷、火災、停電または天災などの不可抗力により、本アプリの提供が困難となった場合\n" +
                "3. コンピュータまたは通信回線等が事故により停止した場合\n" +
                "4. その他、当方が本アプリの提供が困難と判断した場合\n\n" +
                "第7条（保証の否認および免責）\n" +
                "当方は、本アプリに事実上または法律上の瑕疵（安全性、信頼性、正確性、完全性、有効性、特定の目的への適合性、セキュリティなどに関する欠陥、エラーやバグ、権利侵害などを含みます。）がないことを明示的にも黙示的にも保証しておりません。\n" +
                "当方は、本アプリに起因してユーザーに生じたあらゆる損害について、一切の責任を負いません。\n\n" +
                "第8条（サービス内容の変更等）\n" +
                "当方は、ユーザーに通知することなく、本アプリの内容を変更しまたは本アプリの提供を中止することができるものとし、これによってユーザーに生じた損害について一切の責任を負いません。\n\n" +
                "第9条（利用規約の変更）\n" +
                "当方は、必要と判断した場合には、ユーザーに通知することなくいつでも本規約を変更することができるものとします。なお、本規約の変更後、本アプリの利用を開始した場合には、当該ユーザーは変更後の規約に同意したものとみなします。\n\n" +
                "第10条（準拠法・裁判管轄）\n" +
                "本規約の解釈にあたっては、日本法を準拠法とします。本アプリに関して紛争が生じた場合には、当方の本店所在地を管轄する裁判所を専属的合意管轄とします。";
    }
}
