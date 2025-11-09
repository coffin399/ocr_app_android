# ビルドエラー解決ガイド

## 現在の状況

テーマを`android:Theme.Material.Light.DarkActionBar`に変更し、すべてのテーマ属性参照を直接の色参照に置き換えました。

## 実施した修正

1. **テーマの変更**
   - `Theme.MaterialComponents.*` → `android:Theme.Material.Light.DarkActionBar`
   - AndroidネイティブのMaterialテーマを使用

2. **テーマ属性の置き換え**
   - `?attr/colorPrimary` → `@color/primary_blue`
   - `?attr/colorError` → `@color/error`
   - `?attr/colorSurface` → `@color/surface`
   - テーマオーバーレイの参照を削除

3. **Material Componentsスタイルの削除**
   - `Widget.MaterialComponents.*`スタイル参照をすべて削除
   - Material Componentsウィジェットは引き続き使用可能（デフォルトスタイルが適用）

## 次のステップ

### 1. Android StudioでGradle Syncを実行
1. Android Studioを開く
2. 「File」→「Sync Project with Gradle Files」をクリック
3. 同期が完了するまで待つ（数分かかる場合があります）

### 2. プロジェクトをクリーンビルド
1. 「Build」→「Clean Project」をクリック
2. 「Build」→「Rebuild Project」をクリック

### 3. それでも解決しない場合

以下のコマンドをAndroid Studioのターミナルで実行してください：

```bash
gradlew clean
gradlew build --refresh-dependencies
```

## 注意事項

- `android:Theme.Material`はAPI 21（Android 5.0）以上で利用可能です
- プロジェクトの`minSdk`は24なので、問題なく動作するはずです
- Material Componentsウィジェットは引き続き使用できますが、テーマはAndroidネイティブのMaterialテーマを使用しています

## トラブルシューティング

### まだエラーが出る場合

1. **Android Studioを再起動**
   - 「File」→「Invalidate Caches / Restart」→「Invalidate and Restart」

2. **Gradleのキャッシュをクリア**
   ```bash
   gradlew clean
   ```

3. **依存関係を再取得**
   ```bash
   gradlew build --refresh-dependencies
   ```

### Material Componentsテーマを使用したい場合

Gradle Syncが成功した後、`themes.xml`の親テーマを以下に変更できます：

```xml
<style name="Theme.OCRApp" parent="Theme.MaterialComponents.Light.DarkActionBar">
```

ただし、この場合はGradle Syncが正しく完了している必要があります。

