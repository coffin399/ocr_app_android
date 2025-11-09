# 文字起こしアプリ (OCR App)

Android向けのOCR・音声文字起こしアプリです。

## 機能

- **OCR機能**: 画像ファイルからテキストを抽出
- **音声文字起こし**: 音声ファイルをテキストに変換
- **音声録音**: アプリ内で音声を録音して文字起こし
- **検索機能**: 保存されたテキストを検索
- **データ管理**: ローカルデータベースにテキストを保存

## 技術スタック

- **言語**: Kotlin
- **UI**: Jetpack Compose
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
git clone https://github.com/yourusername/ocr_app_android.git
cd ocr_app_android
```

2. Android Studioで開く

3. ビルド
```bash
./gradlew build
```

4. 実行
- Android Studioから実行
- または `./gradlew installDebug` でデバイスにインストール

## API設定

アプリは以下のAPIエンドポイントを使用します:

- ベースURL: `https://mojiscan.online/`
- OCRエンドポイント: `/api/v1/ocr`
- 文字起こしエンドポイント: `/api/v1/transcribe`
- 自動判定エンドポイント: `/api/v1/process`

APIの詳細については、[API_DOCUMENTATION.md](./API_DOCUMENTATION.md) を参照してください。

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
│       │   │   ├── screen/         # 画面コンポーネント
│       │   │   ├── viewmodel/      # ViewModel
│       │   │   ├── navigation/     # ナビゲーション
│       │   │   └── theme/          # テーマ設定
│       │   ├── util/               # ユーティリティ
│       │   ├── MainActivity.kt
│       │   └── OcrApplication.kt
│       └── res/                    # リソースファイル
└── build.gradle
```

## ライセンス

MIT License

## 貢献

プルリクエストを歓迎します。大きな変更の場合は、まずissueを開いて変更内容を議論してください。

## 作者

[あなたの名前]

## 謝辞

- Google Gemini API
- Jetpack Compose
- Retrofit2
- Room Database

