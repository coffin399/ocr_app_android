package com.mojiscan.ocr.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("プライバシーポリシー") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "プライバシーポリシー",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "最終更新日: 2024年1月1日",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "1. はじめに",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本プライバシーポリシーは、本アプリ（以下「本アプリ」といいます。）の利用において、ユーザーの個人情報がどのように取り扱われるかを説明するものです。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "2. 収集する情報",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本アプリは、以下の情報を収集する場合があります。\n" +
                        "• 画像ファイルおよび音声ファイル（OCR処理および文字起こし処理のため）\n" +
                        "• 処理結果として抽出されたテキスト（アプリ内でのみ保存）\n" +
                        "• アプリの利用状況に関する情報（クラッシュレポート、パフォーマンスデータなど）",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "3. 情報の利用目的",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "収集した情報は、以下の目的でのみ使用されます。\n" +
                        "• OCR処理および音声文字起こし処理の提供\n" +
                        "• アプリの機能向上および不具合の修正\n" +
                        "• サービスの品質向上",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "4. 情報の保存および削除",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• アップロードされたファイル: OCR処理または音声文字起こし処理が完了した瞬間に、サーバー上から即座に削除されます。ファイルはサーバー上に保存されることはありません。\n" +
                        "• 処理結果のテキスト: ユーザーの端末内のローカルデータベースにのみ保存されます。外部サーバーには送信されません。\n" +
                        "• ユーザーは、アプリ内でテキストデータを削除することができます。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "5. 第三者への情報提供",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本アプリは、以下の場合を除き、ユーザーの情報を第三者に提供することはありません。\n" +
                        "• 法令に基づく開示が求められた場合\n" +
                        "• 人の生命、身体または財産の保護のために必要がある場合\n" +
                        "• 本アプリの処理のため、GoogleのGemini APIを使用する場合（ファイルは処理後に即座に削除されます）",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "6. データのセキュリティ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本アプリは、ユーザーの情報のセキュリティを確保するために、以下の対策を講じています。\n" +
                        "• ファイルの送信はHTTPS通信により暗号化\n" +
                        "• サーバー上でのファイルは処理完了後に即座に削除\n" +
                        "• 処理結果のテキストは端末内のローカルデータベースにのみ保存",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "7. ユーザーの権利",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ユーザーは、以下の権利を有します。\n" +
                        "• アプリ内に保存されたテキストデータの閲覧、編集、削除\n" +
                        "• アプリのアンインストールによるすべてのデータの削除",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "8. お問い合わせ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本プライバシーポリシーに関するお問い合わせは、アプリ内の「ご要望受付フォーム」からお願いいたします。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "9. プライバシーポリシーの変更",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "当方は、必要に応じて、本プライバシーポリシーを変更することがあります。変更があった場合には、アプリ内で通知いたします。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

