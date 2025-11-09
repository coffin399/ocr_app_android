# ビルドエラー解決ガイド

## 実施した修正

### 1. **テーマをAppCompatベースに変更**
   - `Theme.MaterialComponents.*` → `Theme.AppCompat.Light.DarkActionBar`
   - AppCompatテーマを使用（Material Componentsライブラリが必要ですが、Gradle Sync後に動作します）

### 2. **Toolbar属性の修正**
   - `app:title` → Javaコードで設定（レイアウトから削除）
   - `app:navigationIcon` → Javaコードで設定（レイアウトから削除）
   - `?attr/actionBarSize` → `wrap_content` + `minHeight="56dp"`

### 3. **Material Componentsテキストスタイルの置き換え**
   - `textAppearanceHeadline5` → `textSize="24sp"`
   - `textAppearanceHeadline6` → `textSize="20sp"`
   - `textAppearanceBody1` → `textSize="16sp"`
   - `textAppearanceBody2` → `textSize="14sp"`
   - `textAppearanceCaption` → `textSize="12sp"`
   - `textAppearanceSubtitle1` → `textSize="16sp"`

### 4. **属性の置き換え**
   - `app:tint` → `android:tint`
   - `app:backgroundTint` → `android:backgroundTint`
   - `app:defaultNavHost` → 削除（Javaコードで設定）

### 5. **appbar_scrolling_view_behavior文字列の修正**
   - Material Componentsの正しいクラス名に更新

## 次のステップ

### 1. Android StudioでGradle Syncを実行（必須）
1. Android Studioを開く
2. 「File」→「Sync Project with Gradle Files」をクリック
3. 同期が完了するまで待つ（数分かかる場合があります）

### 2. プロジェクトをクリーンビルド
1. 「Build」→「Clean Project」をクリック
2. 「Build」→「Rebuild Project」をクリック

### 3. Toolbarのタイトルとナビゲーションアイコンの設定

Toolbarのタイトルとナビゲーションアイコンは、各FragmentのJavaコードで設定する必要があります。

例：
```java
@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    // Toolbarの設定
    Toolbar toolbar = view.findViewById(R.id.toolbar);
    toolbar.setTitle("タイトル");
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    toolbar.setNavigationOnClickListener(v -> {
        // ナビゲーション処理
    });
}
```

## 注意事項

- Material Componentsウィジェット（MaterialCardView、TextInputLayoutなど）は引き続き使用できます
- Material Componentsライブラリは`build.gradle`に含まれているので、Gradle Sync後にMaterial Componentsテーマも使用可能になります
- 現在はAppCompatテーマを使用していますが、Gradle Sync後にMaterial Componentsテーマに変更することもできます

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
