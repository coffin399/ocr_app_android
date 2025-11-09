# Javaへの移行ガイド

## 現在の状況

現在、プロジェクトは **Kotlin + Jetpack Compose** で実装されています。

## 重要な注意事項

**Jetpack ComposeはKotlin専用**です。JavaでComposeを使うことはできません。

Javaに全面変更する場合、以下の選択肢があります：

### オプション1: KSPに移行（推奨・実行済み）

- **Kotlinは維持**（Composeを継続使用可能）
- **KAPT → KSP** に変更（KAPTエラーを解決）
- **変更が最小限**
- ✅ **既に実装済み**

### オプション2: Java + 従来のViewシステム

- **すべてのコードをJavaに変更**
- **Jetpack Composeを削除**
- **XMLレイアウト + ViewBinding/Fragmentを使用**
- **大幅な改修が必要**

## オプション2を選択する場合の変更内容

### 1. 削除が必要なもの
- すべてのJetpack Compose関連の依存関係
- すべてのCompose画面（.ktファイル）
- Composeテーマ設定

### 2. 追加が必要なもの
- XMLレイアウトファイル（res/layout/）
- Fragmentクラス（Java）
- ViewBindingまたはDataBinding
- Activityクラス（Java）

### 3. 変更が必要なファイル
- `MainActivity.kt` → `MainActivity.java`
- すべてのViewModel → Java
- すべてのRepository → Java
- すべてのデータクラス → Java

## 推奨

**オプション1（KSP移行）を推奨します**。理由：
- KAPTエラーが解決される
- Composeの利点を維持
- 変更が最小限
- モダンなUIを維持

Javaに変更する場合は、オプション2を選択してください。

