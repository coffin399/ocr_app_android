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
fun TermsOfServiceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("利用規約") },
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
                text = "利用規約",
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
                text = "第1条（適用）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本規約は、本アプリの利用に関して、アプリ提供者（以下「当方」といいます）とユーザーとの間の権利義務関係を定めることを目的とし、ユーザーと当方との間の本アプリの利用に関わる一切の関係に適用されるものとします。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第2条（利用登録）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本アプリの利用は、利用登録なしで利用することができます。ただし、アプリの機能を利用するには、必要な権限の許可が必要です。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第3条（サービスの内容）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本アプリは、画像ファイルからテキストを抽出するOCR機能、および音声ファイルをテキストに変換する文字起こし機能を提供します。これらの機能は、自社サーバー上でGoogleのGemini APIを使用して処理されます。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第4条（ファイルの取り扱い）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ユーザーがアップロードしたファイルは、OCR処理または音声文字起こし処理のためだけに使用され、処理完了後は即座にサーバーから削除されます。ファイルはサーバー上に保存されることはありません。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第5条（禁止事項）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ユーザーは、本アプリの利用にあたり、以下の行為をしてはなりません。\n" +
                        "1. 法令または公序良俗に違反する行為\n" +
                        "2. 犯罪行為に関連する行為\n" +
                        "3. 本アプリの内容等、本アプリに含まれる著作権、商標権ほか知的財産権を侵害する行為\n" +
                        "4. 本アプリ、ほかのユーザー、またはその他第三者のサーバーまたはネットワークの機能を破壊したり、妨害したりする行為\n" +
                        "5. 本アプリに関して、反社会的勢力等に直接または間接に利益を供与する行為\n" +
                        "6. その他、当方が不適切と判断する行為",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第6条（本アプリの提供の停止等）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "当方は、以下のいずれかの事由があると判断した場合、ユーザーに事前に通知することなく本アプリの全部または一部の提供を停止または中断することができるものとします。\n" +
                        "1. 本アプリにかかるコンピュータシステムの保守点検または更新を行う場合\n" +
                        "2. 地震、落雷、火災、停電または天災などの不可抗力により、本アプリの提供が困難となった場合\n" +
                        "3. コンピュータまたは通信回線等が事故により停止した場合\n" +
                        "4. その他、当方が本アプリの提供が困難と判断した場合",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第7条（保証の否認および免責）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "当方は、本アプリに事実上または法律上の瑕疵（安全性、信頼性、正確性、完全性、有効性、特定の目的への適合性、セキュリティなどに関する欠陥、エラーやバグ、権利侵害などを含みます。）がないことを明示的にも黙示的にも保証しておりません。\n" +
                        "当方は、本アプリに起因してユーザーに生じたあらゆる損害について、一切の責任を負いません。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第8条（サービス内容の変更等）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "当方は、ユーザーに通知することなく、本アプリの内容を変更しまたは本アプリの提供を中止することができるものとし、これによってユーザーに生じた損害について一切の責任を負いません。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第9条（利用規約の変更）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "当方は、必要と判断した場合には、ユーザーに通知することなくいつでも本規約を変更することができるものとします。なお、本規約の変更後、本アプリの利用を開始した場合には、当該ユーザーは変更後の規約に同意したものとみなします。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "第10条（準拠法・裁判管轄）",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本規約の解釈にあたっては、日本法を準拠法とします。本アプリに関して紛争が生じた場合には、当方の本店所在地を管轄する裁判所を専属的合意管轄とします。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

