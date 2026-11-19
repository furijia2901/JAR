# JAR生成手順

このZIPに**実機確認済みJARは含まれていません**。ビルド環境がないため、Minecraftで起動するかは未確認です。

## GitHub Actions（自分のPCにJavaを入れずに作る方法）
1. ZIPを展開し、`GachaFX_Studio_26_3`フォルダ内のファイル一式（`.github`を含む）をGitHubのリポジトリのルートに配置してpush。
2. GitHubリポジトリの Actions → Build Gacha FX Studio JAR → Run workflow を選ぶ。
3. 成功したら実行履歴の Artifacts → `gachafx-studio-mod-jar` を取得。
4. ZIP内のJARを確認して `mods` フォルダに配置。`-sources.jar` や `-dev.jar` は使用しない。
5. Minecraft 26.3 / Fabric Loader と対応Fabric APIで起動。`/gachafx random`でプレビュー。

**注意**: Gradleコンパイルエラーが出る場合はActionsログを共有してください。コードのAPI適合性はまだ未検証です。

## Windowsローカル
Java 25とGradle 9.6を準備し、`BUILD_JAR_WINDOWS.bat` を実行。JARは `build/libs` 内に出力されます。
