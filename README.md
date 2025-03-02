# MusicMotionAnimetion_Test
# 🎵 Android 音楽プレイヤーアプリ開発記録

このプロジェクトは、**ローカルの音楽ファイルを再生する Android 音楽プレイヤーアプリ** です。  
曲リストの表示、再生・一時停止・スキップ機能を実装し、最終的には **アニメーション付きアートワーク表示** を組み込む予定です。

---

## 📌 プロジェクト概要

### 本アプリの主な機能：
- ローカルの音楽を `MediaStore` から取得
- `MediaPlayer` を使用して音楽を再生
- RecyclerView / ListView に曲を表示
- 再生・一時停止・スキップ機能
- アニメーション付きアートワークの表示
- UI のカスタマイズ
- YouTube のショート動画を活用する実験的機能（予定）

---

## 📌 今回の開発で目指すこと

### 🎯 ステップ 1：基本的な音楽プレイヤー機能を作成
- `MediaPlayer` を使用してローカルの音楽を再生
- `MediaStore` を活用して音楽の情報（タイトル・アーティスト・URI）を取得
- `RecyclerView` に曲をリスト表示し、タップで再生できるようにする

### 🎯 ステップ 2：プレイヤー機能を充実
- 一時停止・スキップ機能（次の曲 / 前の曲）
- シークバーを追加して曲の進行状況を表示 & 変更可能にする

### 🎯 ステップ 3：アニメーション付きアートワークの実装
- 音楽再生画面にアートワークを表示
- アニメーション（フェード、ズーム、回転など）を適用
- 動的なビジュアライザーの導入

### 🎯 ステップ 4：YouTube のショート動画と連携（実験的）
- 曲のタイトル・アーティスト名・アルバム名を YouTube で検索
- ヒットした動画をショート動画として再生画面でモーション化
- YouTube Data API のクォータ制限を回避しながら最適化

---

## 📌 実装ステップ

### ✅ 1. MediaPlayer のセットアップ
- `MediaPlayer` を使って 1曲を再生 & 停止
- `MediaStore` から ローカル音楽ファイルの URI を取得
- `setDataSource()` に `content://` 形式の URI をセット

### ✅ 2. 曲のリストを `RecyclerView` で表示
- `MediaStore` から 曲のタイトル・アーティスト・URI を取得
- `RecyclerView` にリストを表示し、クリックで再生できるようにする

### ✅ 3. 再生ボタンの表示 & 動作
- `activity_main.xml` にボタンを追加し、画面上に明確に表示
- `LinearLayout` を活用し、ボタンを最下部に固定
- `RecyclerView` の `layout_weight` を調整して 画面からボタンが消えないようにする

### ✅ 4. 再生・一時停止機能
- ボタンを押すと、再生・停止を切り替える
- 曲をタップ → `Uri` を保存 → ボタンで再生
- ボタンのテキストを「再生/停止」に変更

---

## 📌 よく間違ったポイント

### 🔸 `MediaPlayer` の解放忘れ
`release()` を忘れるとクラッシュが発生するため、再生前に適切に解放する。

### 🔸 `content://` URI を取得しないと `FileNotFoundException`
`MediaStore.Audio.Media._ID` を `projection` に含め、正しい URI を取得する。

### 🔸 `notifyDataSetChanged()` を忘れるとリストが更新されない
RecyclerView に反映するため、データを変更したら必ず呼び出す。

### 🔸 `setContentView()` を `findViewById()` の前に呼ぶ
`findViewById()` を実行する前に `setContentView()` を設定しないと、`NullPointerException` が発生する。

---

## 📌 次にやること

### ✅ 1. 一時停止・スキップ機能の追加
- 「次の曲」「前の曲」ボタンを追加
- ボタンを押したら `MediaPlayer` で曲をスキップ
- リストの範囲を超えたら最初 or 最後の曲に戻る

### ✅ 2. シークバーの追加
- 再生中の時間をリアルタイムで更新
- シークバーを動かして好きな位置から再生

### ✅ 3. アニメーション付きアートワークの実装
- 音楽再生画面にアートワークを表示
- アニメーション（フェード、ズーム、回転など）を適用
- 動的なビジュアライザーの導入

---

## 🎯 まとめ

このアプリでは、ローカルの音楽を再生する基本機能を作成し、UI やアニメーションを充実させることを目指します。  
最終的には **YouTube ショート動画との連携も検討しながら、動的な音楽プレイヤーを実現** することをゴールとしています 🚀🎵