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

### 2. `release.yml` - 自動リリース（未署名）

**トリガー**: 
- タグのプッシュ（`v*.*.*`形式、例: `v1.0.0`）
- 手動実行（ワークフローの実行から）

**動作**:
- リリースAPKをビルド
- GitHub Releasesに自動アップロード
- タグを作成してリリース

**使用方法**:
```bash
# タグを作成してプッシュ
git tag v1.0.0
git push origin v1.0.0
```

### 3. `release-signed.yml` - 署名済みリリース（手動実行）

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

### 未署名APKのリリース

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

