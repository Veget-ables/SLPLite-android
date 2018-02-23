
## SLPLite
SLPLiteはSQLiteをラップして作成されたO/Rマッパーである．javaの標準機能であるリフレクション，ジェネリックの練習のために作成された．
現在実装している機能はデータベースのCreate, Read, Update(レコードの新規作成)である．

Assetsにデータベースファイルを配置すると，インストール時にそれを再利用する．

データベース操作以外にもCSVファイルへの書き出しをサポートしている．



### 書き出したCSVファイルを端末から取り出す
書き出しに成功したCSVファイルは端末の `data/data/アプリのpackage名/files/Entityのクラス名.csv`に書き出される．端末からCSVファイルを取り出すためにはターミナルから以下のようにして書き出す．(adbコマンドを持つパッケージをインストールしていることが前提)

```
    adb exec-out run-as package名 cat files/Entityのクラス名.csv > PCの書き出したい場所
```

ファイルが端末に書き出されているかを確認したいだけの場合には以下のコマンドで確認できる．

```
   adb shell
   run-as package名
   cd で移動
```
