# GitHub Actions リリースワークフロー

このプロジェクトでは、GitHub Actionsを使用してAPKを自動ビルド・リリースします。

## ワークフローの種類

### 1. `build.yml` - 継続的インテグレーション（CI）

**トリガー**: 
- `main`ブランチへのプッシュ
- `main`ブランチへのプルリクエスト

**動作**:
- デバッグAPKをビルド
- アーティファクトとしてアップロード（7日間保持）

### 2. `auto-release.yml` - 自動リリース（推奨）⭐

**トリガー**: 
- `main`ブランチへのプッシュ
- `app/build.gradle`が変更された場合

**動作**:
- `app/build.gradle`からバージョン名を抽出
- 対応するタグが存在しない場合、自動的にタグを作成
- タグをプッシュして`release.yml`をトリガー
- **このワークフローを使用すれば、手動でタグを作成する必要がありません**

**使用方法**:
1. `app/build.gradle`の`versionName`を更新（例: `"1.0.0"`）
2. `main`ブランチにコミット・プッシュ
3. 自動的に`v1.0.0`タグが作成され、リリースが開始されます

### 3. `release.yml` - リリースビルド（タグトリガー）

**トリガー**: 
- タグのプッシュ（`v*.*.*`形式、例: `v1.0.0`）
- 手動実行（ワークフローの実行から）

**動作**:
- リリースAPKをビルド
- GitHub Releasesに自動アップロード
- リリースノートを自動生成

**使用方法**:
```bash
# 手動でタグを作成してプッシュ（auto-release.ymlを使わない場合）
git tag v1.0.0
git push origin v1.0.0
```

### 4. `release-signed.yml` - 署名済みリリース（手動実行）

**トリガー**: 
- 手動実行のみ

**動作**:
- 署名済みリリースAPKをビルド
- GitHub Releasesに自動アップロード

**必要な設定**:
GitHub Secretsに以下を設定する必要があります：
- `KEYSTORE_BASE64`: キーストアファイル（Base64エンコード）
- `KEYSTORE_PASSWORD`: キーストアのパスワード
- `KEY_ALIAS`: キーエイリアス
- `KEY_PASSWORD`: キーのパスワード

## リリース手順

### 自動リリース（推奨）⭐

**最も簡単な方法**: `auto-release.yml`ワークフローを使用

1. バージョン番号を更新
   - `app/build.gradle`の`versionCode`と`versionName`を更新
   ```gradle
   versionCode 1
   versionName "1.0.0"
   ```

2. 変更をコミット・プッシュ
   ```bash
   git add app/build.gradle
   git commit -m "Bump version to 1.0.0"
   git push origin main
   ```

3. **自動的に以下が実行されます**:
   - `auto-release.yml`が`app/build.gradle`の変更を検知
   - バージョン名（`1.0.0`）からタグ名（`v1.0.0`）を生成
   - タグが存在しない場合、自動的にタグを作成してプッシュ
   - `release.yml`がトリガーされ、APKをビルドしてリリース

**メリット**:
- 手動でタグを作成する必要がありません
- バージョン番号の更新だけすれば自動的にリリースされます
- 同じバージョンで複数回リリースされることを防ぎます

### 手動リリース（従来の方法）

1. バージョン番号を更新
   - `app/build.gradle`の`versionCode`と`versionName`を更新

2. 変更をコミット・プッシュ
   ```bash
   git add .
   git commit -m "Release v1.0.0"
   git push origin main
   ```

3. タグを作成してプッシュ
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

4. GitHub Actionsが自動的に実行され、リリースが作成されます

### 署名済みAPKのリリース

1. GitHub Secretsを設定
   - リポジトリのSettings → Secrets and variables → Actions
   - 必要なシークレットを追加

2. ワークフローを手動実行
   - GitHubのActionsタブから「Build and Release Signed APK」を選択
   - 「Run workflow」をクリック
   - タグ名を入力（例: `v1.0.0`）
   - 「Run workflow」をクリック

## キーストアの作成

署名済みAPKをリリースするには、キーストアファイルが必要です。

### キーストアの生成

```bash
keytool -genkey -v -keystore ocr-app-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ocr-app
```

### キーストアのBase64エンコード

```bash
# Linux/Mac
base64 -i ocr-app-release.jks -o keystore.txt

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("ocr-app-release.jks")) | Out-File -Encoding ASCII keystore.txt
```

### GitHub Secretsへの追加

1. リポジトリのSettings → Secrets and variables → Actions
2. 以下のシークレットを追加：
   - `KEYSTORE_BASE64`: Base64エンコードされたキーストアファイルの内容
   - `KEYSTORE_PASSWORD`: キーストアのパスワード
   - `KEY_ALIAS`: キーエイリアス（例: `ocr-app`）
   - `KEY_PASSWORD`: キーのパスワード

## build.gradleへの署名設定追加（オプション）

署名済みAPKをビルドするには、`app/build.gradle`に署名設定を追加する必要があります：

```gradle
android {
    ...
    
    signingConfigs {
        release {
            if (project.hasProperty('KEYSTORE_FILE')) {
                storeFile file(KEYSTORE_FILE)
                storePassword KEYSTORE_PASSWORD
                keyAlias KEY_ALIAS
                keyPassword KEY_PASSWORD
            }
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

## トラブルシューティング

### ビルドが失敗する場合

1. GitHub Actionsのログを確認
2. エラーメッセージを確認
3. ローカルでビルドを試す
   ```bash
   ./gradlew assembleRelease
   ```

### リリースが作成されない場合

1. タグが正しくプッシュされているか確認
2. ワークフローが実行されているか確認
3. GitHub Tokenの権限を確認

### 署名が失敗する場合

1. GitHub Secretsが正しく設定されているか確認
2. キーストアファイルが正しくBase64エンコードされているか確認
3. パスワードが正しいか確認

## 注意事項

- キーストアファイルは絶対にリポジトリにコミットしないでください
- GitHub Secretsを使用して安全に管理してください
- リリース前に必ずテストを行ってください

