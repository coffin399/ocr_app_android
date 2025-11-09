# 文字起こしアプリ (OCR App)

Android向けのOCR・音声文字起こしアプリです。

## 機能

- **OCR機能**: 画像ファイルからテキストを抽出
- **音声文字起こし**: 音声ファイルをテキストに変換
- **音声録音**: アプリ内で音声を録音して文字起こし
- **検索機能**: 保存されたテキストを検索
- **データ管理**: ローカルデータベースにテキストを保存

## 技術スタック

- **言語**: Java
- **UI**: Material Design Components (従来のViewシステム)
- **アーキテクチャ**: MVVM
- **データベース**: Room
- **ネットワーク**: Retrofit2, OkHttp3
- **依存性注入**: なし（ViewModel直接インスタンス化）

## 要件

- Android Studio Hedgehog | 2023.1.1 以降
- JDK 8 以降
- Android SDK 24 以降 (Android 7.0+)
- Gradle 8.2 以降

## セットアップ

1. リポジトリをクローン
```bash
git clone <your-private-repo-url>
cd ocr_app_android
```

2. API設定（必須）
   - `app/src/main/java/com/mojiscan/ocr/network/RetrofitClient.java` の `BASE_URL` を個人用APIサーバーのURLに変更してください。

3. Android Studioで開く

4. ビルド
```bash
./gradlew build
```

5. 実行
- Android Studioから実行
- または `./gradlew installDebug` でデバイスにインストール

## API設定

このアプリは個人用のプライベートAPIを使用しています。APIの詳細は非公開です。

## ビルド設定

### リリースビルド

```bash
./gradlew assembleRelease
```

APKは `app/build/outputs/apk/release/` に生成されます。

### デバッグビルド

```bash
./gradlew assembleDebug
```

APKは `app/build/outputs/apk/debug/` に生成されます。

## プロジェクト構造

```
app/
├── src/
│   └── main/
│       ├── java/com/mojiscan/ocr/
│       │   ├── data/
│       │   │   ├── model/          # APIレスポンスモデル
│       │   │   ├── entity/         # Roomエンティティ
│       │   │   ├── dao/            # Room DAO
│       │   │   └── database/       # Roomデータベース
│       │   ├── network/            # Retrofit APIクライアント
│       │   ├── repository/         # リポジトリ
│       │   ├── ui/
│       │   │   ├── fragment/       # Fragmentクラス
│       │   │   ├── adapter/        # RecyclerViewアダプター
│       │   │   └── viewmodel/      # ViewModel
│       │   ├── billing/            # Google Play Billing
│       │   ├── util/               # ユーティリティ
│       │   ├── MainActivity.java
│       │   └── OcrApplication.java
│       └── res/                    # リソースファイル
└── build.gradle
```

## ライセンス

MIT License

## 注意事項

このリポジトリは個人用のプライベートリポジトリです。APIエンドポイントや設定情報は非公開です。

## 作者

coffin299

## 謝辞

- Google Gemini API (非公開サーバー経由)
- Material Design Components
- Retrofit2
- Room Database

