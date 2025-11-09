# スマホアプリ向けOCR・音声文字起こし API ドキュメント

## エンドポイント

### POST `/api/v1/process` (推奨)

ファイルを受け取り、ファイルタイプに応じて自動的にOCRまたは音声文字起こしを実行します。

#### リクエスト

- **メソッド**: `POST`
- **Content-Type**: `multipart/form-data`
- **パラメータ**:
  - `file` (必須): アップロードするファイル（画像、PDF、音声）
  - `process_type` (オプション): 処理タイプ（"auto", "ocr", "transcribe"）
    - "auto": ファイルタイプに応じて自動判定（デフォルト）
    - "ocr": 強制的にOCR処理
    - "transcribe": 強制的に音声文字起こし処理

#### サポートされるファイル形式

- **OCR**: 画像（JPEG, PNG, GIF, BMP, WebP）、PDF
- **音声文字起こし**: 音声（WAV, MP3, FLAC, OGG, M4A, AAC, WebM, Opus）

#### リクエスト例

##### cURL
```bash
# 自動判定
curl -X POST "https://mojiscan.online/api/v1/process" \
  -F "file=@/path/to/file.jpg"

# OCRを強制
curl -X POST "https://mojiscan.online/api/v1/process" \
  -F "file=@/path/to/image.jpg" \
  -F "process_type=ocr"

# 音声文字起こしを強制
curl -X POST "https://mojiscan.online/api/v1/process" \
  -F "file=@/path/to/audio.wav" \
  -F "process_type=transcribe"
```

##### JavaScript (Fetch API)
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('process_type', 'auto'); // オプション

const response = await fetch('https://mojiscan.online/api/v1/process', {
  method: 'POST',
  body: formData
});

const result = await response.json();
console.log(result.text); // OCR/文字起こし結果
```

#### レスポンス

##### 成功時 (200 OK)

```json
{
  "success": true,
  "text": "抽出されたテキスト",
  "filename": "file.jpg",
  "process_type": "ocr",
  "model": "gemini-2.5-flash",
  "processing_time": 1.23,
  "timestamp": "2024-01-01T12:00:00.000000"
}
```

### POST `/api/v1/ocr`

画像をアップロードして、OCR処理を実行し、結果を即座に返します。

#### リクエスト

- **メソッド**: `POST`
- **Content-Type**: `multipart/form-data`
- **パラメータ**:
  - `file` (必須): アップロードする画像ファイルまたはPDF

#### サポートされるファイル形式

- 画像: JPEG, PNG, GIF, BMP, WebP
- ドキュメント: PDF

#### リクエスト例

##### cURL
```bash
curl -X POST "https://mojiscan.online/api/v1/ocr" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/image.jpg"
```

##### JavaScript (Fetch API)
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const response = await fetch('https://mojiscan.online/api/v1/ocr', {
  method: 'POST',
  body: formData
});

const result = await response.json();
console.log(result);
```

##### Python (requests)
```python
import requests

url = "https://mojiscan.online/api/v1/ocr"
with open("image.jpg", "rb") as f:
    files = {"file": f}
    response = requests.post(url, files=files)
    result = response.json()
    print(result)
```

##### Swift (iOS)
```swift
import Foundation

func uploadImage(imageData: Data, completion: @escaping (Result<OCRResult, Error>) -> Void) {
    let url = URL(string: "https://mojiscan.online/api/v1/ocr")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    
    let boundary = UUID().uuidString
    request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
    
    var body = Data()
    body.append("--\(boundary)\r\n".data(using: .utf8)!)
    body.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n".data(using: .utf8)!)
    body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
    body.append(imageData)
    body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
    
    request.httpBody = body
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            completion(.failure(error))
            return
        }
        
        guard let data = data else {
            completion(.failure(NSError(domain: "No data", code: -1)))
            return
        }
        
        do {
            let result = try JSONDecoder().decode(OCRResult.self, from: data)
            completion(.success(result))
        } catch {
            completion(.failure(error))
        }
    }.resume()
}

struct OCRResult: Codable {
    let success: Bool
    let text: String
    let filename: String
    let model: String
    let processingTime: Double
    let confidence: Double
    let timestamp: String
    let pages: Int?
    
    enum CodingKeys: String, CodingKey {
        case success, text, filename, model, confidence, timestamp, pages
        case processingTime = "processing_time"
    }
}
```

##### Kotlin (Android)
```kotlin
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun uploadImage(imageFile: File, callback: (Result<OCRResult>) -> Unit) {
    val client = OkHttpClient()
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",
            imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaType())
        )
        .build()
    
    val request = Request.Builder()
        .url("https://mojiscan.online/api/v1/ocr")
        .post(requestBody)
        .build()
    
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(Result.failure(e))
        }
        
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val result = gson.fromJson(
                    response.body?.string(),
                    OCRResult::class.java
                )
                callback(Result.success(result))
            } else {
                callback(Result.failure(IOException("Unexpected code $response")))
            }
        }
    })
}

data class OCRResult(
    val success: Boolean,
    val text: String,
    val filename: String,
    val model: String,
    @SerializedName("processing_time") val processingTime: Double,
    val confidence: Double,
    val timestamp: String,
    val pages: Int?
)
```

#### レスポンス

##### 成功時 (200 OK)

```json
{
  "success": true,
  "text": "抽出されたテキスト内容",
  "filename": "image.jpg",
  "model": "gemini-2.5-flash",
  "processing_time": 1.23,
  "confidence": 1.0,
  "timestamp": "2024-01-01T12:00:00.000000",
  "pages": null
}
```

##### エラー時

###### 400 Bad Request - 無効なファイル形式
```json
{
  "detail": "サポートされていないファイル形式: application/octet-stream. サポート形式: image/jpeg, image/png, ..."
}
```

###### 413 Payload Too Large - ファイルサイズが大きすぎる
```json
{
  "detail": "ファイルサイズが大きすぎます。最大サイズ: 50.0MB"
}
```

###### 503 Service Unavailable - APIキーが設定されていない、またはOCR処理エラー
```json
{
  "detail": "OCR処理に失敗しました: All API keys failed. Last error: ..."
}
```

###### 500 Internal Server Error - サーバーエラー
```json
{
  "detail": "サーバーエラーが発生しました: ..."
}
```

#### レスポンスフィールド

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `success` | boolean | 処理が成功したかどうか |
| `text` | string | 抽出されたテキスト |
| `filename` | string | アップロードされたファイル名 |
| `model` | string | 使用されたOCRモデル（常に "gemini-2.5-flash"） |
| `processing_time` | number | 処理にかかった時間（秒） |
| `confidence` | number | 信頼度（0.0-1.0、現在は常に1.0） |
| `timestamp` | string | 処理が完了した時刻（ISO 8601形式） |
| `pages` | number \| null | PDFの場合はページ数、画像の場合はnull |

### POST `/api/v1/transcribe`

音声ファイルをアップロードして、文字起こし処理を実行し、結果を即座に返します。

#### リクエスト

- **メソッド**: `POST`
- **Content-Type**: `multipart/form-data`
- **パラメータ**:
  - `file` (必須): アップロードする音声ファイル
  - `language` (オプション): 言語コード（Whisper: "ja", Google Speech: "ja-JP"）
  - `model` (オプション): 使用するモデル（"whisper" または "google-speech"）

#### サポートされるファイル形式

- 音声: WAV, MP3, FLAC, OGG, M4A, AAC, WebM, Opus

#### リクエスト例

##### cURL
```bash
curl -X POST "https://mojiscan.online/api/v1/transcribe" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/audio.wav" \
  -F "language=ja" \
  -F "model=whisper"
```

##### JavaScript (Fetch API)
```javascript
const formData = new FormData();
formData.append('file', audioInput.files[0]);
formData.append('language', 'ja');
formData.append('model', 'whisper');

const response = await fetch('https://mojiscan.online/api/v1/transcribe', {
  method: 'POST',
  body: formData
});

const result = await response.json();
console.log(result.text); // 文字起こし結果
```

#### レスポンス

##### 成功時 (200 OK)

```json
{
  "success": true,
  "text": "文字起こしされたテキスト",
  "filename": "audio.wav",
  "model": "whisper",
  "language": "ja",
  "processing_time": 2.45,
  "timestamp": "2024-01-01T12:00:00.000000",
  "segments": null
}
```

##### エラー時

###### 400 Bad Request - 無効なファイル形式
```json
{
  "detail": "サポートされていないファイル形式: application/octet-stream. サポート形式: audio/wav, audio/mpeg, ..."
}
```

###### 503 Service Unavailable - 文字起こしモデルが設定されていない
```json
{
  "detail": "No transcription model available. Please configure Whisper or Google Speech-to-Text"
}
```

## その他のエンドポイント

### GET `/api/health`

サーバーのヘルスチェック

#### レスポンス例

```json
{
  "status": "healthy",
  "service": "DeepSeek-OCR",
  "models_available": 1
}
```

## 注意事項

1. **ファイルサイズ制限**: 最大50MBまでアップロード可能
2. **CORS**: デフォルトで全てのオリジンからのアクセスを許可（本番環境では適切に設定してください）
3. **レート制限**: 現在、レート制限は実装されていませんが、Gemini APIのレート制限が適用されます
4. **処理時間**: 
   - OCR処理は通常1-5秒程度かかります（画像のサイズや複雑さにより異なります）
   - 音声文字起こしは通常2-10秒程度かかります（音声ファイルの長さやモデルにより異なります）
5. **音声文字起こしモデル**: 
   - Whisper: ローカルで動作、無料、設定が必要
   - Google Speech-to-Text: クラウドAPI、有料、高精度

## トラブルシューティング

### CORSエラーが発生する場合

`config.yaml`でCORS設定を確認してください：

```yaml
server:
  cors_origins: "*"  # 全てのオリジンを許可（開発環境）
  # または
  cors_origins: "https://mojiscan.online"  # 特定のオリジンのみ許可（本番環境）
```

環境変数でも設定可能：

```bash
export CORS_ORIGINS="*"
```

### APIキーエラーが発生する場合

`config.yaml`でGemini APIキーが正しく設定されているか確認してください：

```yaml
gemini:
  api_key: "YOUR_API_KEY"
  # または複数のキーを使用
  api_key1: "YOUR_API_KEY_1"
  api_key2: "YOUR_API_KEY_2"
```

