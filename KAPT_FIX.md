# KAPT エラー解決ガイド

## 問題
Java 17+ でKAPTが動作しないエラーが発生しています。

## 解決方法

### 方法1: Kotlin Daemonを停止して再起動（推奨）

1. **Android Studioで:**
   - File → Invalidate Caches / Restart
   - "Invalidate and Restart" を選択

2. **コマンドラインで:**
   ```bash
   # Kotlin Daemonを停止
   ./gradlew --stop
   
   # プロジェクトをクリーン
   ./gradlew clean
   
   # ビルド
   ./gradlew build
   ```

### 方法2: Java バージョンの確認

Android Studioで使用しているJavaバージョンを確認:

1. File → Settings → Build, Execution, Deployment → Build Tools → Gradle
2. Gradle JDK が **17** または **11** に設定されているか確認
3. **17を使用している場合**: 上記のgradle.properties設定が適用されているか確認
4. **11を使用している場合**: この問題は発生しません

### 方法3: Java 11を使用する（最も確実）

Java 11を使用すれば、この問題は発生しません:

1. Java 11をインストール
2. Android Studio → File → Settings → Build, Execution, Deployment → Build Tools → Gradle
3. Gradle JDK を **11** に変更
4. プロジェクトを再ビルド

### 方法4: KSPに移行（将来的な解決策）

KAPTの代わりにKSP（Kotlin Symbol Processing）を使用:

1. `app/build.gradle`を更新:
   ```gradle
   plugins {
       id 'com.google.devtools.ksp' version '1.9.20-1.0.14'
   }
   
   dependencies {
       ksp 'androidx.room:room-compiler:2.6.1'
   }
   ```

2. `kapt`を`ksp`に置き換え

## 現在の設定

`gradle.properties`に以下の設定を追加しました:

- JVM起動オプションに`--add-exports`と`--add-opens`を追加
- Kotlin DaemonのJVMオプションにも同じ設定を追加
- KAPTの最適化オプションを有効化

## トラブルシューティング

### まだエラーが発生する場合

1. **Gradle Daemonを停止:**
   ```bash
   ./gradlew --stop
   ```

2. **.gradleディレクトリを削除:**
   ```bash
   rm -rf .gradle
   rm -rf app/build
   ```

3. **Android Studioを再起動**

4. **Gradle Syncを実行**

5. **クリーンビルド:**
   ```bash
   ./gradlew clean build
   ```

### それでも解決しない場合

Java 11を使用することを強く推奨します。Java 17+ でKAPTを使用する場合は、上記の設定が必要ですが、完全に解決しない場合があります。

