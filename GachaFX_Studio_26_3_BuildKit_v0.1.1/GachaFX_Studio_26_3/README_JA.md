# Gacha FX Studio 0.1.0 — World Journeyとは別のFabric Mod

Minecraft Java **26.3** / Fabric Loader **0.19.5** / Fabric API **0.160.7+26.3** / Java **25**向けのソースコードプロジェクトです。

## 内容

- **60種類** = テーマ5種 × 動き12種。各組み合わせに固有のIDあり。
- ガチャのデモ演出は **HUD上のクライアント表示**。景品・確率・課金・サーバーセーブは変更しません。
- イントロ → 集約／チャージ → レアリティ表示・結果発表 → フェードアウト。
- World Journeyへは直接依存しない独立Mod ID `gachafx`。ワールドや既存ModのJARを書き換えません。
- 現段階は**2D演出プロトタイプ**であり、3Dカメラ・専用音楽・音声・高解像度テクスチャは未実装です。

## 使い方（ビルドに成功した後）

1. Java 25 と Gradle 9.6以降（Loom 1.17対応）をインストール。
2. ZIPを展開し、そのフォルダで `gradle build` を実行。
3. 生成された `build/libs/gachafx-studio-0.1.0.jar` をクライアントの `.minecraft/mods` に置く（`-sources.jar` ではない方）。
4. Minecraft 26.3 + Fabric APIで起動。
5. ワールドで `/gachafx random` または `/gachafx next` を実行。 `/gachafx list` でカテゴリ表示。
6. `/gachafx play celestial_portal`、`/gachafx stop` も対応。

**注意：この配布物にビルド済みJARは含まれていません。** 作成環境にJava 25/GradleがないためMinecraft実機ビルド・実機動作の検証は未実施です。`gradle build` でエラーが出る場合は全文を共有してください。

## 演出テーマ（各12種類）

| ID | テーマ | 色 |
|---|---|---|
| celestial | 星空 | 青紫＋金 |
| sakura | 桜花 | ピンク＋白 |
| abyss | 深淵 | 濃紺＋紫紅 |
| solar | 太陽 | オレンジ＋金 |
| ocean | 深海 | 水色＋エメラルド |

## 12種類の動き

| ID | 動作 |
|---|---|
| burst | 放射拡散 |
| orbit | 楕円公転 |
| rain | 星の雨 |
| portal | 回転ゲート |
| shatter | 破片飛散 |
| spiral | 螺旋収束 |
| pulse | 脈動 |
| comet | 彗星横断 |
| constellation | 星座配置 |
| roulette | 高速輪転 |
| wave | 波紋 |
| ascend | 光粒上昇 |

命名規則：`<theme>_<motion>`。すべての組み合わせで60種類。

## World Journeyに将来連携する方法

- 連携側クライアントが `GachaFxClient.play("celestial_portal", "Freesia Card", "UR")` を呼び出せるようにする。
- ただしこれは**Javaクラスを参照して呼び出す開発用API**です。実運用では World Journey 側に `gachafx` の任意依存判定・クライアント専用イベント・Mod間の安全な連携を追加する必要があります。
- multiplayerでサーバーがプレイヤーへ演出指示するネットワーク送受信や、実際の抽選完了と同期する処理は**この版にはありません**。
- アニメーションは報酬の抽選とは完全分離。抽選結果を受け取り表示する責務だけを担当します。

## テスト状況

- Pythonによるテキスト構造・全ID一意性チェックを同梱。
- Java25でのコンパイル、Minecraft起動、マルチサーバー統合は**未検証**。
