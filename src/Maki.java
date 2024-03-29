import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * Maki.
 *
 * 最小限のミニマムでかつシンプルな Sphinx ライクなもの.
 * とにかく機能もシンプルで，プログラムもこのクラスのみ.
 * 他のライブラリなどにも依存せず，JDK のみでコンパイルと
 * 実行が可能なドキュメントフォーマッタ.
 *
 * -------------------------------------------------------
 * 初版(1.0.0) 2017/08/25 Fri
 * ミニマムな機能を実装。打ち合わせ資料などをコンバートできる
 * 状態にまで完成した。
 * -------------------------------------------------------
 * Version 1.1.0 2017/08/29 Tue
 * 機能追加
 *   ・行中にマーキングしたソース ``public`` に対応。
 *   ・ファイル名にバージョン番号を付与することにした。
 *   ・入力ファイル名をタイトルに設定するようにした。
 * -------------------------------------------------------
 * Version 1.2.0 2017/08/30 Wed
 * 機能追加
 *   ・行中の脚注機能を実装。
 *   ・スタイルシートを適用。
 * -------------------------------------------------------
 * Version 1.3.0 2017/08/31 Thr
 * 機能追加
 *   ・ソースコードの表示にスタイルシートを適用。
 *   ・脚注リンクに相互のページ内リンクを実装。
 *   ・画像の表示に対応。
 *   ・h3 の見出しを追加。
 *   ・脚注の書き出しで，ソースなどのブロックの後にも行って
 *     しまっていた処理を修正した。
 *   ・ファイルの読み込みに対しても UTF-8 を指定するように
 *     修正した。(bat からの実行時に影響)
 * -------------------------------------------------------
 * Version 1.4.0 2017/09/06 Wed
 * 機能追加
 *   ・テーブルの実装。
 *   ・デザイン上の微調整。
 * -------------------------------------------------------
 * Version 1.4.1 2017/09/26 Tue
 * 機能追加
 *   ・障害修正: 文末にソースブロックがあると出力されない
 *     事象へ対応。
 *   ・障害修正: 行中にソース表記と脚注が同時にあると脚注
 *     しか反映されない事象へ対応。
 *   ・障害修正: ページタイトルの次の行に箇条書きを書いて
 *     も表示されず，1行あける必要があった事象に対応。
 *   ・脚注のタイトルを若干右へ移動。
 *   ・脚注と文章の区切り線の開始を右へ移動し，色を濃い目
 *     に設定した。
 *   ・ソースブロックの囲み線の色を濃い目に変更。
 * -------------------------------------------------------
 * Version 1.4.2 2017/09/28 Thr
 * 機能追加
 *   ・Eli の開発のためにページタイトルや見出しをリストに
 *     格納しておき，最後にファイルに出力する処理を追加。
 * -------------------------------------------------------
 * Version 1.4.3 2017/10/11 Wed
 * 機能追加
 *   ・Eli 開発停止による 1.4.2 で追加した機能の削除。
 *   ・修正に伴うリファクタリングの実施。
 *   ・アノテーションを付与して警告を削除
 *   ・H2 の見出しの角を丸くスタイル変更。
 *   ・フォントサイズを 0.1 小さくした。
 *   ・コードの囲みの角を丸くスタイル変更。
 * -------------------------------------------------------
 * Version 1.4.4 2017/11/20 Mon
 * 機能追加
 *   ・画像の表示サイズを 400px に変更。
 *   ・executeメソッドのファイル読み込み部で Auto-Commit に対応。
 *   ・コンパイラを Java9 に変更。
 *   ・Any Edit ツールでタブを空白に変更。
 * -------------------------------------------------------
 * Version 1.4.5 2017/12/12 Tue
 * 機能追加
 *   ・出力先のパスを指定できるように修正。指定しない場合は
 *     入力のパスに出す(従来通り)。
 * -------------------------------------------------------
 * Version 1.4.6 2018/03/12 Mon
 * 機能追加
 *   ・脚注に見出し語を付けられるように機能追加。
 * -------------------------------------------------------
 * Version 1.4.7 2018/05/10 Thr
 * 機能追加
 *   ・デザイン変更: h2 の見出しに背景色を追加。
 *   ・デザイン変更: 行中のコードに色を付与。
 * -------------------------------------------------------
 * Version 1.4.8 2018/05/17 Thr
 * 機能追加
 *   ・コードの表示ボックスで横方向にはみ出る場合に
 *     スクロールを表示するように変更。
 * -------------------------------------------------------
 * Version 1.4.9 2018/06/26 Tue
 * 機能追加
 *   ・コードディレクティブ内に Javadoc コメントなどの * を
 *     使用するコメントが記述されているときに、箇条書きと
 *     誤って認識することにより indexOutOfBoundsException
 *     が発生する不具合の解消。
 * -------------------------------------------------------
 * Version 1.4.10 2018/06/27 Wed
 * 機能追加
 *   ・コードディレクティブの最初と最後の空行を非表示に。
 *   ・コードボックスの上下の padding 値を見直し。
 * -------------------------------------------------------
 * Version 1.5.0 2018/06/29 Fri
 * 機能追加
 *   ・目玉機能「本文」に対応。
 *   ・Version 1.5 にした。
 * -------------------------------------------------------
 * Version 1.5.1 2018/06/30 Sat
 * 機能追加
 *   ・p タグで 1 文字だけインデント(字下げ)するように修正。
 * -------------------------------------------------------
 * Version 1.5.2 2018/07/03 Tue
 * 機能追加
 *   ・Java 言語用の簡易的なシンタックスハイライトを仮実装。
 *   ・箇条書きをインデントで少しだけ右寄せするようにした。
 * -------------------------------------------------------
 * Version 1.5.3 2018/07/04 Wed
 * 機能追加
 *   ・目次出力処理を実装。
 *   ・本文出力機能でテーブルの前後で出力順序が不正となる
 *     障害を改修。
 * -------------------------------------------------------
 * Version 1.5.4 2018/07/13 Fri
 * 機能追加
 *   ・Nozomi として実装していた機能を Maki の別モードとして
 *     実装。同時にモードの概念を導入。
 *     (1)nozomi: 指定フォルダ配下全ファイルをビルド
 *     (2)maki  : 標準。指定したファイル 1 ファイルをビルド
 *     (3)eli   : 指定したフォルダ全ファイルの Lv1 見出しを
 *                抽出してサイト全体の目次を作成する(未実装)
 * -------------------------------------------------------
 * Version 1.5.5 2018/07/14 Sat
 * 機能追加
 *   ・旧構想 Eli の実装。eli モードで実行することで、
 *     指定フォルダ内の maki ドキュメントの目次を作成する。
 * -------------------------------------------------------
 * Version 1.5.6 2018/07/17 Tue
 * 機能追加
 *   ・基本的な文中にリンクを張る機能を追加。
 *   ・テーブルの文字の bold を normal に変更。
 * -------------------------------------------------------
 * Version 1.5.7 2018/07/18 Wed
 * 機能追加
 *   ・各見出しの右端にページトップへ戻るリンクを表示。
 * -------------------------------------------------------
 * Version 1.5.8 2018/07/19 Thrs
 * 機能追加
 *   ・BOX形式の注釈を書けるようにディレクティブを追加。
 * -------------------------------------------------------
 * Version 1.5.9 2018/07/23 Mon
 * 機能追加
 *   ・daily update: 最終生成時刻を画面最下部に表示するようにした。
 * -------------------------------------------------------
 * Version 1.5.10 2018/07/28 Sat
 * 機能追加
 *   ・バグ改修。本文の見出しレベル判定でチェック用の行中変換が
 *     うまくいかない場合があるため、オリジナルの行データを保持
 *     するように改修した。
 *   ・リンクに「参考」と「→」のバッジを表示するようにした。
 *   ・スクロールバーの装飾を実施。
 * -------------------------------------------------------
 * Version 1.5.11 2018/08/07 Tue
 * 機能追加
 *   ・リンクのバッジデザインを修正(CSS修正)。
 *   ・注釈のタイトルデザインを修正(CSS修正)。
 * -------------------------------------------------------
 * Version 1.5.12 2018/08/14 Tue
 * 機能追加
 *   ・コードブロック内で行中コマンドが効かないようにした。
 *     (簡易的なエスケープシーケンス対応)
 * -------------------------------------------------------
 * Version 1.5.13 2018/09/11 Tue
 * 機能追加
 *   ・.maki ファイルの格納場所が階層構造に対応。
 *   ・出力する Eli 目次が階層構造に対応。
 * -------------------------------------------------------
 * Version 1.5.14 2018/09/15 Sat
 * 機能追加
 *   ・階層構造に対応した際のパスの区切り文字が環境依存
 *     していたため、File クラスの定数を使用するように修正
 * -------------------------------------------------------
 * Version 1.5.15 2018/09/30 Sun
 * 機能追加
 *   ・太字と蛍光ペンによるマーカーの組み合わせによる強調
 *     表示ができるようにした。
 * -------------------------------------------------------
 * Version 1.5.16 2018/10/02 Tue
 * 機能追加
 *   ・バグ改修: 太字と蛍光ペンの指定が % だと 100% などの
 *     表現ができなくなるため、## で指定するものとした。
 * -------------------------------------------------------
 * Version 1.5.17 2018/10/03 Wed
 * 機能追加
 *   ・太文字+赤文字+アンダーラインの指定が @@ でできる
 *     ように機能追加。
 * -------------------------------------------------------
 * Version 1.5.18 2018/10/04 Thrs
 * 機能追加
 *   ・URLのリンクの記述を「&」から「!」に変更。URLパラメタ
 *     の存在に配慮。
 * -------------------------------------------------------
 * Version 1.5.19 2018/10/12 Fri
 * 機能追加
 *   ・画像サイズを横幅まで広げるが、実寸サイズよりは拡大
 *     しないように CSS を見直し。
 * -------------------------------------------------------
 * Version 1.5.20 2018/11/16 Fri
 * 機能追加
 *   ・画像の周囲に 1px の枠線を表示するように CSS を修正。
 * -------------------------------------------------------
 * Version 1.5.21 2018/11/27 Tue
 * 機能追加
 *   ・引用(.. quote::)を追加。
 * -------------------------------------------------------
 * Version 1.5.22 2018/12/19 Wed
 * 機能追加
 *   ・バグFix(ソースコードブロック中に $ を使うとエラー)
 * -------------------------------------------------------
 * Version 1.5.23 2018/12/19 Wed
 * 機能追加
 *   ・バグFix(1.5.22 のデグレード対応)
 * -------------------------------------------------------
 * Version 1.5.24 2019/01/12 Sat
 * 機能追加
 *   ・軽微なリファクタリング(メソッド分割)
 * -------------------------------------------------------
 * Version 1.5.25 2019/04/15 Mon
 * 機能追加
 *   ・Last Generated の値の出力がマイクロ秒までの詳細すぎる
 *     出力結果になっていたため Date 型を使用するように変更。
 * -------------------------------------------------------
 * Version 1.5.26 2019/05/07 Tue
 * 機能追加
 *   ・HTMLタグをエスケープする処理を追加。
 * -------------------------------------------------------
 * Version 1.5.27 2019/06/26 Wed
 * 機能追加
 *   ・脚注の本文中に付く番号を上付き文字になるようにスタイル
 *     を修正。
 *   ・行中のコードのスタイルを見直し(boxの追加)。
 * -------------------------------------------------------
 * Version 1.5.28 2019/07/16 Tue
 * 機能追加
 *   ・macOS で動かした場合、目次の順序が正常に降順にならない
 *     ため、ファイル名でソートする処理を追加。
 *     (これによりコンパイル後の class ファイルが 2 つになる)
 * -------------------------------------------------------
 * Version 1.5.29 2019/07/17 Wed
 * 機能追加
 *   ・引用(.. quote::)を用いた時にリボンの折り返しに描かれる
 *     フォントが正常に表示されていないので修正(Windowsでも起
 *     こる事象)。CSSを修正して「Quote」を表示する。
 * -------------------------------------------------------
 * Version 1.5.30 2019/07/18 Thrs
 * 機能追加
 *   ・文章全体のフォントを見直し(某サイトを参考)。
 * -------------------------------------------------------
 * Version 1.5.31 2019/07/26 Fri
 * 機能追加
 *   ・文章全体のフォントサイズを見直し。
 *   ・Rust 版移行前の最終 Fix 版(Java 最終版)
 *     (以降は重大バグ以外の修正は実施せず)
 * -------------------------------------------------------
 * Version 1.5.32 2019/07/30 Tue
 * 機能追加
 *   ・文章全体・目次全体のフォント及びサイズの見直し。
 * -------------------------------------------------------
 * Version 1.5.33 2019/09/04 Wed
 * 機能追加
 *   ・文章全体・目次全体のフォント及びサイズの見直し。
 *   ・游明朝に設定
 * -------------------------------------------------------
 * Version 1.5.34 2019/09/06 Fri
 * 機能追加
 *   ・目次のリンクをマウスホバー時に下線が表示されるように修正。
 * -------------------------------------------------------
 * Version 1.5.35 2019/09/09 Mon
 * 機能追加
 *   ・文章全体のフォントを見直し(結局元に戻した)。
 *   ・脚注のフォントサイズをやや小さめに。
 * -------------------------------------------------------
 * Version 1.5.36 2019/09/10 Tue
 * 機能追加
 *   ・取り消し線(%%で囲む)の機能を追加。
 * -------------------------------------------------------
 * Version 1.5.37 2019/09/20 Fri
 * 機能追加
 *   ・脚注のリンクにマウスカーソルを当てるとツールチップを
 *     表示するように修正した。
 * -------------------------------------------------------
 * Version 1.5.38 2019/10/23 Wed
 * 機能追加
 *   ・起動パラメータの見直し(一部)
 *   ・ログ出力機能の実装
 * -------------------------------------------------------
 * Version 1.5.39 2019/10/25 Fri
 * 機能追加
 *   ・ログ出力を定数値により出力制御が可能とした。
 *   ・ボックスリンクの初版を作成(.. link::)。
 *   ・ボックスリンク用のアノテーションを追加。
 * -------------------------------------------------------
 * Version 1.5.40 2019/10/28 Mon
 * 機能追加
 *   ・画像の横幅を指定できるように機能追加。
 * -------------------------------------------------------
 * Version 1.5.41 2019/11/11 Mon
 * 機能追加
 *   ・Inner Link の表示内容を少々変更。
 *   ・太字 + underline の文字装飾(&&)を追加。
 * -------------------------------------------------------
 * Version 1.5.42 2019/11/27 Wed
 * 機能追加
 *   ・画面最下部に文字装飾のヘルプを表示.
 * -------------------------------------------------------
 * Version 1.5.43 2019/11/29 Fri
 * 機能追加
 *   ・文字装飾(__)にて強調・大型文字をサポート.
 * -------------------------------------------------------
 * Version 1.5.44 2019/12/13 Fri
 * 機能追加
 *   ・コードハイライト(仮)の予約語追加
 * -------------------------------------------------------
 * Version 1.5.45 2019/12/19 Thurs
 * 機能追加
 *   ・画像の右寄せ・左寄せ・中央寄せを可能にした。
 *     (右・左寄せの場合はテキストが回り込む設定にした)
 * -------------------------------------------------------
 * Version 1.5.46 2020/01/09 Thurs
 * 機能追加
 *   ・画像の右寄せ時の左側に来るテキストとの余白を微調整。
 * -------------------------------------------------------
 * Version 1.5.47 2020/02/20 Thurs
 * 機能追加
 *   ・link のパス指定が macOS だとエラーになる問題を修正
 * -------------------------------------------------------
 * Version 1.6.0 2020/03/22 Sunday
 * 機能追加
 *   ・@category のアノテーションを追加。目次にカテゴリ
 *     リストを表示するように機能追加した。
 * -------------------------------------------------------
 * Version 1.6.1 2020/06/06 Saturday
 * 機能追加
 *   ・目次を見出しで折りたためるように修正。
 *   ・カテゴリの表示順を自然順序（TreeMap）に変更。
 * -------------------------------------------------------
 * Version 1.6.2 2020/06/15 Monday
 * 機能追加
 *   ・カテゴリ目次の出力で、key と value のセパレータを
 *     「:」にしていたが、Windows だとドライブレターと
 *     重複するため「;」に変更した。
 *   ・カテゴリファイルの読み書きで UTF-8 を指定するように
 *     改修した（日本語の文字化け対応）。
 * -------------------------------------------------------
 * Version 1.6.3 2020/08/06 Thursday
 * 機能追加
 *   ・quote の記載で先頭行に改行を入れるように修正。
 * -------------------------------------------------------
 * Version 1.6.4 2020/09/03 Thursday
 * 機能追加
 *   ・目次にタイトル以外にページ概要も表示するように修正
 * -------------------------------------------------------
 * Version 1.7.0 2020/12/25 Friday
 * 機能追加
 *   ・英数字のフォントを Slack が使用している Lato に変更
 * -------------------------------------------------------
 * Version 1.7.1 2021/06/29 Tuesday
 * 機能追加
 *   ・未使用だった定数 KOTORI を削除
 * -------------------------------------------------------
 * Version 1.7.2 2021/07/31 Saturday
 * 機能追加
 *   ・行中リンクを行頭に使用すると変換されない事象を改修
 *
 * @author tomohiko37_i
 * @version 1.7.2
 */
public class Maki {

    /**
     * モード: 全ビルド NOZOMI.
     */
    private static final String MODE_NOZOMI = "nozomi";

    /**
     * モード: 単一ビルド: MAKI.
     */
    private static final String MODE_MAKI   = "maki";

    /**
     * モード: 目次作成: ELI.
     */
    private static final String MODE_ELI    = "eli";

    /**
     * 起動パラメータの順序: モード.
     */
    private static final int PARAM_NUM_MODE     = 0;

    /**
     * 起動パラメータの順序: 入力ファイル.
     */
    private static final int PARAM_NUM_IN_FILE  = 1;

    /**
     * 起動パラメータの順序: 出力ファイル.
     */
    private static final int PARAM_NUM_OUT_FILE = 2;

    /**
     * 起動パラメータの順序: ルートディレクトリ.
     */
    private static final int PARAM_NUM_ROOT_DIR = 2;

    /**
     * 箇条書きリスト.
     */
    private List<Object> itemList = new ArrayList<Object>();

    /**
     * 書き出し用のバッファ.
     */
    private BufferedWriter bw = null;

    /**
     * 見出しの文字列を一時的に格納するテンポラリ.
     */
    private String tempStr;

    /**
     * 改行文字の定数.
     */
    private static final String CONST_CRLF = "\n";

    /**
     * ブロックの範囲内かどうか判定するフラグ.
     */
    private boolean blockZoneFlg = false;

    /**
     * ブロック中の文字列を格納する.
     */
    private List<String> blockList = new ArrayList<String>();

    /**
     * 現在の Maki のバージョン.
     */
    private static final String CONST_VERSION = "1.7.2";

    /**
     * タイトル(処理するファイル名).
     */
    private String title;

    /**
     * 脚注を保持するリスト.
     */
    private List<String> footNoteList = new ArrayList<String>();

    /**
     * 脚注参照番号.
     * (ページ内で通番)
     */
    private int footNoteCount = 0;

    /**
     * テーブルのヘッダ部を保存しておく.
     */
    private String tableHeader = "";

    /**
     * テーブルのデータ部を保存しておく.
     */
    private List<String> tableDataList = new ArrayList<String>();

    /**
     * 読み込んだ行データをそのまますべて保存しておくリスト.
     */
    private List<String> backupList = new ArrayList<>();

    /**
     * シンタックスハイライトの対象(Java).
     */
    private String[] syntaxHighlighting4J = {  "@param",  "@return", "@author",    "@version", "public ",
                                               "private ", "protected ",  "void ",   "return",
                                               "final ",  "this",    "super",      "if ",     "else ",
                                               "for ",    "while ",   "switch ",     "try ",    "catch ",
                                               "new ",    "finally ", "null",       "static ", "class ",
                                               "true",   "false",   "instanceof ", "int ",    "double ",
                                               "float",  "short",   "long",       "break",  "continue",
                                               "throws ",  "throw ",  "boolean ", "package", "import"};

    /**
     * テーブル生成のための状況.
     *  0: 未作成
     *  1: ヘッダ
     *  2: データ
     */
    private int tableStatus = 0;

    /**
     * ブロックがコードブロックかどうか.
     */
    private boolean isCodeBlock = true;

    /**
     * 見出し1のカウント.
     */
    private int h1Cnt = 0;

    /**
     * 見出し2のカウント.
     */
    private int h2Cnt = 0;

    /**
     * 見出し3のカウント.
     */
    private int h3Cnt = 0;

    /**
     * ルートディレクトリ.
     */
    private static String rootDir = "";

    /**
     * 定数:ログ出力:OFF.
     */
    private static String LOG_LEVEL_OFF = "off";

    /**
     * ログ出力制御の定数.
     * ログを出力したいときはここを on にすること.
     */
    private static String LOG_OUTPUT = "off";

    /**
     * 各ページに書かれたカテゴリを保持するマップ。
     * クラス変数にしている点に注意。
     */
    private static Map<String, List<String>> categoryMap = new TreeMap<>();

    /**
     * 読み込むファイルのパス.
     * カテゴリ機能の追加によりインスタンス変数に格上げ.
     */
    private String inputFilePath  = "";

    /**
     * 簡易的な Sphinx ジェネレータ.
     *
     * @param args 起動パラメータ
     */
    public static void main(final String[] args) {
        if (args.length == 3) {
            // パラメータ3個なので NOZOMI モード
            rootDir = args[PARAM_NUM_ROOT_DIR]; // 目次ファイル用
        }
        new Maki(args);

        // 最後にここへ来るため、
        // ここでカテゴリ・マップを
        // ファイルに出力しておく
        if (!args[0].equals("eli")) {
            try (FileOutputStream fos = new FileOutputStream(new File(rootDir + "/tmp_category.txt"));
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                 BufferedWriter bw = new BufferedWriter(osw);) {

               Iterator<String> iter = categoryMap.keySet().iterator();
               while (iter.hasNext()) {
                   StringBuilder sb = new StringBuilder();
                   String key = iter.next();

                   sb.append(key);
                   sb.append(";");
                   List<String> paths = categoryMap.get(key);
                   int cnt = 0;
                   for (String path : paths) {
                       sb.append(path);
                       if (cnt != (paths.size() - 1)) {
                           sb.append(",");
                       }
                   }
                   bw.write(sb.toString() + "\n");
               }

               bw.flush();
           } catch (IOException e) {
               e.printStackTrace();
           }
        }
    }

    /**
     * コンストラクタ.
     *
     * @param args 起動パラメータ
     */
    public Maki(final String[] args) {
        this.execute(args);
    }

    /**
     * 主処理.
     *
     * @param args 起動パラメータ
     */
    private void execute(final String[] args) {
        this.log("execute --- START");

        if (args.length == 0 || args.length == 1) {
            // パラメータは最低限でもモードと入力ファイルの
            // パスは必要なので、個数が満たない場合はエラー
            this.log("パラメータの数が不足しています。");
            this.log("パラメータは「モード」「ファイルパス」の 2 つが必要です。");
            return;
        }

        // モードを取得する
        String mode = args[PARAM_NUM_MODE]; // 0番目
        this.log("Execute Mode: [" + mode + "]");

        String outputFilePath = "";  // 出力ファイルのパス
        if (args.length == 2) {
            // ELI モードの場合のパラメータ受け取り
            this.log("number of parameters: 2");
            inputFilePath  = args[PARAM_NUM_IN_FILE];  // 1番目
            outputFilePath = inputFilePath;
        } else if (args.length == 3) {
            // NOZOMI モードの場合のパラメータ受け取り
            // MAKI モードの場合もここが動く(モード判定の場合は注意)
            this.log("number of parameters: 3");
            inputFilePath  = args[PARAM_NUM_IN_FILE];   // 1番目
            outputFilePath = args[PARAM_NUM_OUT_FILE];  // 2番目
        } else {
            // ここに来るのはパラメータの数が多すぎる場合
            this.log("指定されたパラメータの数が多すぎます。");
            return;
        }
        // 入力ファイル・出力ファイルのパスから格納するフォルダの
        // パスを取得する(ディレクトリパスの指定の場合はそのまま)
        String inDirPath = this.getDirPath(inputFilePath);
        // tocFilePath は各ページの最上部にある「top」の
        // クリック時に戻る目次ファイルとして使用している
        // 目次ファイル出力先にも使用するように修正した
        this.tocFilePath = rootDir + "/index.maki.html";

        this.log("inputFilePath: [" + inputFilePath    + "]");
        this.log("outputFilePath:[" + outputFilePath   + "]");
        this.log("tocFilePath:   [" + this.tocFilePath + "]");

        // ELI, NOZOMI の両方のモードを同時に実行すべき
        // (目次を出力しない場合なんかないので)
        if (MODE_NOZOMI.equals(mode)) {
            // NOZOMI モードの場合
            this.log("NOZOMI MODE... allFileBuild start");
            // 処理の実行
            this.allFileBuild(inDirPath);
            return;
        } else if (MODE_ELI.equals(mode)) {
            // ELI モードの場合
            this.log("ELI MODE... createIndex start");
            // フォルダ内の全ファイルの見出し1を抽出した
            // index.maki.html を作成する
            // 目次の出力
            this.createIndex(inDirPath);
            this.log("execute --- ELI MODE END");
            return;
        }

        this.log("MAKI MODE... file convert start");
        // 読み込むファイルの準備
        File file = new File(inputFilePath);
        this.title = file.getName();
        this.log("★" + file.getName());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "utf-8"));) {

            // 書き出すファイルの準備(UTF-8で出力する)
            this.bw = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream(new File(outputFilePath)), "utf-8"));

            // 初期処理
            this.initWrite();

            // 事前に全部読み込んでリストへ保持
            // これで過去に振り替えることができる
            while (br.ready()) {
                String line = br.readLine();
                line = this.escapeHtml(line);
                this.backupList.add(line);
            }

            // 目次出力処理
            this.createTOC();

            // 編集処理
            for (String line : this.backupList) {
                //System.out.println(line);
                // 編集と書き出し
                this.editAndWrite(this.editLine(line), line);
            }

            // 最終行の書き込み
            this.itemListWrite();
            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }

            // 終了処理
            this.blockWrite();
            this.footNoteWrite();
            this.finalWrite();

            // ファイルのクローズ
            this.bw.flush();
            this.bw.close();

        } catch (FileNotFoundException e) {
            this.log(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            this.log(e.getMessage());
            e.printStackTrace();
        }
        this.log("execute --- OTHER MODE END");
    }

    /**
     * HTMLのタグをエスケープする処理.
     * @param line 1行の文字列
     * @return エスケープ済み文字列
     */
    private String escapeHtml(final String line) {
        String tmp1 = line.replace("<", "&lt;");
        String tmp2 = tmp1.replace(">", "&gt;");
        return tmp2;
    }


    /**
     * 指定されたパスからディレクトリパスを取得して返却する.
     * 最初からディレクトリパスの場合はそのまま返す.
     * (ファイルパスの場合はファイルが格納されているディレクトリ
     * のパスを返す、ということ)
     * @param path パス
     * @return ディレクトリパス
     */
    private String getDirPath(final String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return path;
        } else {
            return file.getParent();
        }
    }

    /**
     * 目次出力用ファイル.
     */
    private BufferedWriter eli = null;

    /**
     * 目次ファイルパス.
     */
    private String tocFilePath = "";

    /**
     * 全ファイルの目次を作成する.
     * @param inDirPath 入力ディレクトリ
     * @param outDirPath 出力ディレクトリ
     */
    private void createIndex(final String inDirPath) {
        File inputDirFile = new File(inDirPath);
        try {
            this.eli = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(inDirPath + "/index.maki.html")), "utf-8"));

            eli.write("<!DOCTYPE html>" + CONST_CRLF);
            eli.write("<html lang=\"ja\">" + CONST_CRLF);
            eli.write("<head>" + CONST_CRLF);
            eli.write("  <meta charset=\"UTF-8\"/>" + CONST_CRLF);
            eli.write("  <title>Maki メニュー</title>" + CONST_CRLF);
            eli.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"https://fonts.googleapis.com/css?family=Lato\" />");
            eli.write("  <style type=\"text/css\">" + CONST_CRLF);
            eli.write("      h1 {position: relative; margin: 0 0 1.5em; padding: 0.8em; " +
                      "          background: #B92A2C; color: #fff; font-size: 1.143em; font-weight: bold; " +
                      "          border-radius: 5px; -webkit-border-radius: 5px; -moz-border-radius: 5px;}" + CONST_CRLF);
            eli.write("      h1:after {position: absolute; bottom: -15px; left: 10%; z-index: 90; " +
                      "                margin-left: -15px; border-top: 15px solid #B92A2C; border-left: 15px solid transparent; " +
                      "                border-bottom: 0; content: \"\";}" + CONST_CRLF);
            eli.write("      body {margin-right: auto; margin-left: auto; width: 800px; " +
                      "            background-color: #fedadf; font-family: Lato, 'メイリオ', 'Meiryo'," +
                      "            'ヒラギノ丸ゴ Pro W4','ヒラギノ丸ゴ Pro','Hiragino Maru Gothic Pro','ヒラギノ角ゴ Pro W3', 'Hiragino Kaku Gothic Pro', sans-self " +
                      "            !important; font-size: 0.8em; -webkit-font-smoothing: antialiased;}" + CONST_CRLF);
            eli.write("      .toc {font-size: 1.0em;}" + CONST_CRLF);
            eli.write("      .tocHeader {font-size: 1.2em; font-weight: bolder; "
                             + "background: linear-gradient(transparent 50%, yellow 50%);}" + CONST_CRLF);
            eli.write("      a { text-decoration: none; }" + CONST_CRLF);
            eli.write("      a:hover { text-decoration: underline; }");

            eli.write("  </style>" + CONST_CRLF);
            eli.write("</head>" + CONST_CRLF);
            eli.write("<body>" + CONST_CRLF);
            eli.write("<h1>Maki Main Menu</h1>" + CONST_CRLF);
            eli.write("  <ul class=\"toc\">" + CONST_CRLF);

            this.toc(inputDirFile);

            eli.write("  </ul>" + CONST_CRLF);

            this.category(inDirPath);

            eli.write("</body>" + CONST_CRLF);
            eli.write("</html>" + CONST_CRLF);

        } catch (UnsupportedEncodingException e2) {
            this.log(e2.getMessage());
            e2.printStackTrace();
        } catch (FileNotFoundException e2) {
            this.log(e2.getMessage());
            e2.printStackTrace();
        } catch (IOException e1) {
            this.log(e1.getMessage());
            e1.printStackTrace();
        } finally {
            try {
                this.eli.flush();
                this.eli.close();
            } catch (IOException e) {
                this.log(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 目次作成処理.
     * @param dir ディレクトリ
     * @throws IOException 例外
     */
    private void toc(final File dir) throws IOException {
        if (dir.isDirectory()) {
            this.eli.append("<details>\n");
            this.eli.append("    <summary><span  class=\"tocHeader\">" + dir.getName() + "</span></summary>\n");
            this.eli.append("<ul>\n");
            File[] files = dir.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File file1, File file2) {
                    return file1.getName().compareTo(file2.getName());
                }
            });

            for (File file : files) {
                this.toc(file);
            }
            this.eli.append("</ul>\n");
            this.eli.append("</details>\n");
        } else {
            int idx = dir.getName().lastIndexOf(".");
            String ext = dir.getName().substring(idx);
            if (".maki".equals(ext)) {
                this.createContents(dir);
            }
        }
    }

    /**
     * カテゴリのリンクリストを作成する.
     */
    private void category(final String inputDirPath) {
        // 中間ファイルの存在を確認して読み込む
        File file = new File(inputDirPath + "/tmp_category.txt");
        if (file.exists()) {
            // ファイルが存在した場合、読み込む
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, "utf-8");
                 BufferedReader br = new BufferedReader(isr);) {
                while (br.ready()) {
                    String line = br.readLine();
                    String[] tokens = line.split(";");
                    if (tokens.length == 2) {
                        String category = tokens[0];
                        String[] paths = tokens[1].split(",");
                        this.eli.write("<ul>\n");
                        this.eli.write("    <details>\n");
                        this.eli.write("        <summary><span class=\"tocHeader\">#" + category + "</span></summary>\n");
                        //this.eli.write("  <li><span class=\"tocHeader\">#" + category + "</span></li>\n");
                        this.eli.write("  <ul>\n");
                        for (String path : paths) {
                            // path のファイルを読み込んで @page_title を取得する
                            File categoryFile = new File(path);
                            try (FileInputStream cfis = new FileInputStream(categoryFile);
                                 InputStreamReader cisr = new InputStreamReader(cfis, "utf-8");
                                 BufferedReader cbr = new BufferedReader(cisr);) {
                                String pageTitle = "";
                                String pageOutline = "";
                                while (cbr.ready()) {
                                    String categoryLine = cbr.readLine();
                                    if (categoryLine.indexOf("@page_title") != -1) {
                                        pageTitle = categoryLine.split(":")[1];
                                    } else if (categoryLine.indexOf("@page_outline") != -1) {
                                        pageOutline = categoryLine.split(":")[1].trim();
                                    }
                                }
                                String html = path.replace(".maki", ".html");
                                this.eli.write("    <li><a href=\"" + html + "\">" + pageTitle + "</a>&nbsp;" + pageOutline + "</li>\n");
                            }
                        }
                        this.eli.write("  </ul>\n");
                        this.eli.write("  </details>");
                        this.eli.write("</ul>\n");
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 目次の要素作成.
     * @param dir ディレクトリ
     */
    private void createContents(final File dir) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(dir.getPath()), "utf-8"))) {
            String tmpLine = "";
            String tmpOutline = "";
            while (br.ready()) {
                String line = br.readLine();
                if (line == null || line.equals("")) {
                    continue;
                }
                String prefix = line.substring(0, 1);
                if ("=".equals(prefix)) {
                    this.eli.write("    <li><a href=\"" + dir.getParent() + File.separator
                                                        + dir.getName().replace(".maki", ".html") + "\">"
                                                        + tmpLine + "</a>&nbsp; " + tmpOutline + "</li>" + CONST_CRLF);
                } else if ("@".equals(prefix)) {
                    if (line.indexOf("@page_outline") != -1) {
                        tmpOutline = line.split(":")[1].trim();
                    }
                } else {
                    tmpLine = line;
                }
            }
        } catch (FileNotFoundException e) {
            this.log(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            this.log(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 全ファイルビルド用の再帰呼びのツリー作成処理.
     * @param dir ディレクトリパス
     */
    private void tree(final File dir) {
        if (dir.isDirectory()) {
            for (String child : dir.list()) {
                this.tree(new File(dir, child));
            }
        } else {
            int idx = dir.getName().lastIndexOf(".");
            String ext = dir.getName().substring(idx);
            if (".maki".equals(ext)) {
                String outputFilePath = dir.getParent() + File.separator + dir.getName().replace(".maki", ".html");
                String[] args = {MODE_MAKI,
                                 dir.getPath(),
                                 outputFilePath};
                new Maki(args);
            }
        }
    }

    /**
     * 旧 NOZOMI の機能. 全ファイルビルド.
     * @param inDirPath 入力ディレクトリパス
     * @param outDirPath 出力ディレクトリパス
     */
    private void allFileBuild(final String inDirPath) {
        this.tree(new File(inDirPath));
    }

    /**
     * 編集と書き出し.
     *
     * @param line 1行データ
     * @param orgLine オリジナルの1行データ
     * @throws IOException ファイル書き込み時の例外
     */
    private void editAndWrite(final String line,
                              final String orgLine) throws IOException {

        if (this.blockZoneFlg && line != null && line.length() == 0) {
            this.blockList.add("");
        }

        // 空行の場合はスルーする
        if (line == null || line.length() == 0) {
            return;
        }

        // 先頭の文字列を取得する
        String token = line.substring(0, 1);

        if ("=".equals(token)) {

            this.itemListWrite();
            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }
            this.footNoteWrite();

            // ページタイトル
            this.bw.write("<div class=\"page_top\"><a href=\"#\">^page top</a></div>" + CONST_CRLF);
            this.bw.write("<h1 " + "id=\"h1_" + h1Cnt + "\">" + this.tempStr + "</h1>" + CONST_CRLF);
            h1Cnt++;

        } else if ("-".equals(token)) {

            this.itemListWrite();
            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }
            this.footNoteWrite();

            // 見出しレベル2
            this.bw.write("<div class=\"page_top\"><a href=\"#\">^page top</a></div>" + CONST_CRLF);
            this.bw.write("<h2 " + "id=\"h2_" + h2Cnt + "\">" + this.tempStr + "</h2>" + CONST_CRLF);
            h2Cnt++;

        } else if ("~".equals(token)) {

            this.itemListWrite();
            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }
            this.footNoteWrite();

            // 見出しレベル3
            this.bw.write("<div class=\"page_top\"><a href=\"#\">^page top</a></div>" + CONST_CRLF);
            this.bw.write("<h3 " + "id=\"h3_" + h3Cnt + "\">" + this.tempStr + "</h3>" + CONST_CRLF);
            h3Cnt++;

        } else if ("@".equals(token)) {
            // アノテーションの場合

            if (line.indexOf("@category:") != -1) {
                // 「カテゴリ」アノテーションの場合
                // カンマ(,)区切りでカテゴリが書かれている場合は、
                // それぞれ分断して全部 Map に保存しておく。
                String[] categories = line.substring(10).split(",");
                for (String category : categories) {
                    category = category.trim();
                    this.log("●category: " + category);

                    List<String> categoryList = null;
                    if (categoryMap.containsKey(category)) {
                        // すでにカテゴリが登録されている場合
                        this.log("●すでにカテゴリが登録されている場合");
                        categoryList = categoryMap.get(category);
                    } else {
                        // カテゴリが存在しない場合
                        this.log("●カテゴリが存在しない場合");
                        categoryList = new ArrayList<>();
                    }
                    categoryList.add(this.inputFilePath);
                    categoryMap.put(category, categoryList);
                }
            }

        } else if ("*".equals(token)) {

            this.blockWrite();

            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }

            // レベル1箇条書き
            this.itemList.add(line.trim().substring(2));

        } else if (".".equals(token)) {

            this.itemListWrite();
            //this.footNoteWrite();

            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }

            // .. code-block:: かどうか
            if (line.indexOf("code-block::") != -1) {
                this.blockZoneFlg = true;
                this.isCodeBlock = true;
            }

            // .. quote:: かどうか
            if (line.indexOf("quote::") != -1) {
                this.blockZoneFlg = true;
                this.isCodeBlock = false;

                String cite = line.substring(11);
                this.blockList.add("cite:" + cite);
            }

            // .. image:: かどうか
            if (line.indexOf("image::") != -1) {

                // 画像ファイル(パス)名を取得する
                String imagePath = line.substring(11);
                String width = "";
                String align = "";

                if (imagePath.indexOf(",") != -1) {
                    String[] tokens = imagePath.split(",");
                    imagePath = tokens[0];
                    width = tokens[1].trim();

                    // right or left or center までの設定
                    if (tokens.length == 3) {
                        align = tokens[2].trim();
                    }
                }

                if (!align.equals("")) {

                    if ("center".equals(align)) {
                        this.bw.write("<div style=\"text-align:center\">" + CONST_CRLF);
                    } else {
                        if ("right".equals(align)) {
                            this.bw.write("<div style=\"float:right;margin-left:20px;margin-bottom:10px\">" + CONST_CRLF);
                        } else if ("left".equals(align)) {
                            this.bw.write("<div style=\"float:left; margin-left: 35px; margin-right:20px;margin-bottom:10px\">" + CONST_CRLF);
                        } else {
                            this.bw.write("<div style=\"text-align:left; margin-left: 35px;\">" + CONST_CRLF);
                        }
                    }

                    if (!width.equals("")) {
                        this.bw.write("<a href=\"" + imagePath + "\"><img src=\"" + imagePath + "\" width=\"" + width + "\"/></a>");
                    } else {
                        this.bw.write("<a href=\"" + imagePath + "\"><img src=\"" + imagePath + "\"/></a>");
                    }
                    this.bw.write("</div>" + CONST_CRLF);
                } else {
                    this.bw.write("<div style=\"margin-left: 35px;\">" + CONST_CRLF);
                    if (!width.equals("")) {
                        this.bw.write("<a href=\"" + imagePath + "\"><img src=\"" + imagePath + "\" width=\"" + width + "\"/></a>");
                    } else {
                        this.bw.write("<a href=\"" + imagePath + "\"><img src=\"" + imagePath + "\"/></a>");
                    }
                    this.bw.write("</div>" + CONST_CRLF);
                }
            }

            // .. note:: かどうか
            if (line.indexOf("note::") != -1) {
                // タイトルと本文を取得する
                String note = line.substring(10);
                StringTokenizer noteToken = new StringTokenizer(note, ",");
                String title = noteToken.nextToken();
                String noteSentence = noteToken.nextToken();

                this.bw.write("<br>" + CONST_CRLF);
                this.bw.write("<div class=\"box27\">" + CONST_CRLF);
                this.bw.write("    <span class=\"box-title\">" + title + "</span>" + CONST_CRLF);
                this.bw.write("    <p>" + noteSentence + "</p>" + CONST_CRLF);
                this.bw.write("</div>" + CONST_CRLF);
            }

            // .. link:: かどうか
            if (line.indexOf("link::") != -1) {

                // リンク先ファイルの相対パスを取得
                String link = line.substring(10);
                if (".".equals(link.substring(0, 1))) {
                    // 先頭が . の場合は除去する
                    link = link.substring(1);
                }

                // リンク先のファイルを読み込み
                BufferedReader tmpBr =  new BufferedReader(new InputStreamReader(
                                            new FileInputStream(new File(rootDir + link)), "utf-8"));

                // リンク先のアノテーションを取得する
                // ※インスタンス変数の pageTitle, pageOutline は当該ページのもの
                String linkTitle = "Linked page annotation(@page_title) is not set.";
                String outline = "Linked page annotation(@page_outline) is not set.";

                while (tmpBr.ready()) {
                    String tmpLine = tmpBr.readLine();
                    if (tmpLine.indexOf("@page_title") != -1) {
                        linkTitle = tmpLine.split(":")[1];
                    } else if (tmpLine.indexOf("@page_outline") != -1) {
                        outline = tmpLine.split(":")[1];
                    }
                }

                this.bw.write("<div class=\"inner-link-box\">" + CONST_CRLF);
                this.bw.write("  <span style=\"font-weight: bold; font-size: 0.8em;\">blog.tmp.maki</span><br>" + CONST_CRLF);
                this.bw.write("  <span class=\"link-badge\">Inner Link</span><a href=\""
                                    + rootDir + link.replace(".maki", ".html")
                                    +  "\" target=\"_blank\"><b>" + linkTitle + "</b></a><br>" + CONST_CRLF);
                this.bw.write("  <div class=\"inner-link-outline\">" + outline + "</div>" + CONST_CRLF);
                this.bw.write("  <span style=\"font-weight: bold; font-size: 0.6em; text-align: right;\">■created by @tomohiko37_i </span>" + CONST_CRLF);
                this.bw.write("</div>" + CONST_CRLF);

                tmpBr.close();
            }

        } else if ("+".equals(token)) {

            this.itemListWrite();

            // テーブル対応(1)
            // 先頭が + の場合は一番上か，一番下か，ヘッダとデータの区切りか，
            // 行の区切りのいずれか。
            if (this.tableStatus == 0) {
                // 1 行目が来た
                this.tableStatus = 1; // ヘッダが始まった
            } else if (this.tableStatus == 1) {
                // データに切り替わる
                this.tableStatus = 2;
            } else {
                // 状況: 2 の場合
                // データ部編集中
            }

        } else if ("|".equals(token)) {

            // テーブルの中の実際の要素行
            // 状況の値を判別しながら処理する
            if (this.tableStatus == 0) {
                // この状況はありえない
            } else if (this.tableStatus == 1) {
                // この行はヘッダ部である。
                this.tableHeader = line;
            } else if (this.tableStatus == 2) {
                // データ部である
                this.tableDataList.add(line);
            }

        } else if (" ".equals(token)) {

            // ブロックのエリア内であれば先頭空白の行は全部保持する
            if (this.blockZoneFlg) {
                //this.blockList.add(line);
                this.blockList.add(orgLine);
                // (廃止→)オリジナル行を使用したときに、オリジナル行に
                // (廃止→)脚注要素があればリストから削除しておく
            } else {
                // 先頭が空白の場合はレベル2以降の箇条書き
                if (line.indexOf("    * ") != -1) {

                    // レベル3

                    // 最後の要素を取得する
                    Object obj = this.itemList.get(this.itemList.size() - 1);
                    if (obj instanceof String) {

                        // レベル1の次にいきなりレベル3はないはず

                    } else if (obj instanceof List) {

                        // レベル2のリストを取得する
                        @SuppressWarnings("unchecked")
                        List<Object> tmpLv2 = (List<Object>) obj;

                        // レベル2のリストの最後を取得する
                        Object objL2Last = tmpLv2.get(tmpLv2.size() - 1);
                        if (objL2Last instanceof String) {

                            // 最後が文字列だったら新しいリストを作って格納する
                            List<String> tmp = new ArrayList<String>();
                            tmp.add(line.trim().substring(2));
                            tmpLv2.add(tmp);

                        } else if (objL2Last instanceof List) {

                            // リストだったら追加する
                            @SuppressWarnings("unchecked")
                            List<Object> l3List = (List<Object>) objL2Last;
                            l3List.add(line.trim().substring(2));
                        }
                    }

                } else if (line.indexOf("  * ") != -1) {

                    // レベル2

                    // 最後の要素を取得する
                    Object obj = this.itemList.get(this.itemList.size() - 1);
                    if (obj instanceof String) {

                        // 最後が文字列だったら新しいリストを作って格納する
                        List<Object> tmp = new ArrayList<Object>();
                        tmp.add(line.trim().substring(2));
                        this.itemList.add(tmp);

                    } else if (obj instanceof List) {

                        // 最後がリストだったらリストへ追加する
                        @SuppressWarnings("unchecked")
                        List<Object> tmp = (List<Object>) obj;
                        tmp.add(line.trim().substring(2));
                    }

                } else {
                    // それ以外
                }
            }
        } else {

            // 見出し行か本文かを判別する
            if (this.isHeading(line)) {
                // 見出し行
                this.tempStr = line;
            } else {
                // 本文
                if (this.itemList.size() == 0) {
                    this.blockWrite();
                    if (this.tableStatus == 2) {
                        // テーブルが終わった
                        this.tableWrite();
                        this.tableStatus = 0;
                    }
                    int lv = this.checkHeadingLevel(line, orgLine);
                    if (lv == 0) {
                        this.bw.write("<p>" + line + "</p>");
                    } else {
                        this.bw.write("<p class=\"heading_lv" + lv + "\">" + line + "</p>");
                    }
                } else {
                    this.itemListWrite();
                    if (this.tableStatus == 2) {
                        // テーブルが終わった
                        this.tableWrite();
                        this.tableStatus = 0;
                    }
                    int lv = this.checkHeadingLevel(line, orgLine);
                    if (lv == 0) {
                        this.bw.write("<p>" + line + "</p>");
                    } else {
                        this.bw.write("<p class=\"heading_lv" + lv + "\">" + line + "</p>");
                    }
                }
            }

            this.blockWrite();
            if (this.tableStatus == 2) {
                // テーブルが終わった
                this.tableWrite();
                this.tableStatus = 0;
            }
        }
    }

    /**
     * 指定した本文文字列がどの見出し配下にあるか判定.
     * @param line 1行データ
     * @param orgLine オリジナルの1行データ
     * @return 見出しレベル. 0 なら見出しではない.
     */
    private int checkHeadingLevel(final String line,
                                  final String orgLine) {
        for (int i = 0; i < this.backupList.size(); i++) {
            String backup = this.backupList.get(i);
            // 本文中に脚注などの指定があると、backup は変換前、
            // line は変換後の文字列になっていて一致しなくなる.
            // そのため orgLine を使用する
            //String checkBackup = this.editLine(backup, false);
            //if (checkBackup.equals(line)) {
               if (backup.equals(orgLine)) {
                // 前行に遡って見出し行が出てくるのを探す
                for (int j = i; j >= 0; j--) {
                    String preBackup = this.backupList.get(j);
                    if (preBackup != null && !preBackup.equals("")) {
                        String prefix = preBackup.substring(0, 1);
                        if (prefix.equals("~")) {
                            return 3;
                        } else if (prefix.equals("-")) {
                            return 2;
                        } else if (prefix.equals("=")) {
                            return 1;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 指定した文字が見出し行かどうか判別する.
     * @param line 対象文字
     * @return 見出し行なら true を返す
     */
    private boolean isHeading(final String line) {
        for (int i = 0; i < this.backupList.size(); i++) {
            String backup = this.backupList.get(i);
            if (backup.equals(line)) {
                // 一致した文字列が最終行かどうか
                if (this.backupList.size() == (i + 1)) {
                    // 最終行であれば見出しではなく本文
                    return false;
                } else {
                    // 最終行でなければ次行を参照する
                    String nextBackup = this.backupList.get(i + 1);
                    // 次行の先頭文字を取得
                    if (nextBackup != null && !nextBackup.equals("")) {
                        String prefix = nextBackup.substring(0, 1);
                        if (prefix.equals("=") || prefix.equals("-") || prefix.equals("~")) {
                            // 見出し行である
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        // 一致するものがないことはないと思うが、
        // 見出しではないので false
        return false;
    }

    /**
     * 箇条書きの書き出し.
     *
     * @throws IOException
     */
    private void itemListWrite() throws IOException {

        this.bw.write("<ul class=\"left_indent\">" + CONST_CRLF);
        for (int i = 0; i < this.itemList.size(); i++) {

            Object lv1Obj = this.itemList.get(i);
            if (lv1Obj instanceof String) {

                this.bw.write("  <li>" + String.valueOf(lv1Obj) + "</li>" + CONST_CRLF);

            } else if (lv1Obj instanceof List) {

                this.bw.write("    <ul>" + CONST_CRLF);

                @SuppressWarnings("unchecked")
                List<Object> lv2List = (List<Object>) lv1Obj;
                for (int j = 0; j < lv2List.size(); j++) {

                    Object lv2Obj = lv2List.get(j);
                    if (lv2Obj instanceof String) {

                        this.bw.write("      <li>" + lv2Obj + "</li>" + CONST_CRLF);

                    } else if (lv2Obj instanceof List) {

                        this.bw.write("      <ul>" + CONST_CRLF);

                        @SuppressWarnings("unchecked")
                        List<String> lv3List = (List<String>) lv2Obj;
                        for (int k = 0; k < lv3List.size(); k++) {

                            this.bw.write("      <li>" + lv3List.get(k) + "</li>" + CONST_CRLF);
                        }

                        this.bw.write("      </ul>" + CONST_CRLF);
                    }
                }

                this.bw.write("    </ul>" + CONST_CRLF);
            }
        }

        this.bw.write("</ul>" + CONST_CRLF);
        this.itemList = new ArrayList<Object>();
    }

    /**
     * ブロック情報の書き出し.
     *
     * @throws IOException
     */
    private void blockWrite() throws IOException {

        this.blockZoneFlg = false;

        if (this.blockList.size() == 0) {
            return;
        }

        if (this.isCodeBlock) {
            this.bw.write("<pre class=\"code-box deco\"><code>");
            for (int i = 0; i < this.blockList.size(); i++) {
                String line = this.editSyntaxHighlight4J(this.blockList.get(i));
                if (i == 0 || (i == this.blockList.size() - 1)) {
                    if (line.length() != 0) {
                        this.bw.write(line + CONST_CRLF);
                    }
                } else {
                    this.bw.write(line + CONST_CRLF);
                }
            }
            this.bw.write("</code></pre>" + CONST_CRLF);
        } else {
            String cite = "";
            this.bw.write("<blockquote><p><br>" + CONST_CRLF);
            for (int i = 0; i < this.blockList.size(); i++) {
                String line = this.blockList.get(i);
                if (line.indexOf("cite:") != -1) {
                    cite = line.substring(5);
                } else {
                    if (i == 0 || (i == this.blockList.size() - 1)) {
                        if (line.length() != 0) {
                            this.bw.write(line + "<br>");
                        }
                    } else {
                        this.bw.write(line + "<br>");
                    }
                }
            }
            this.bw.write("<cite>" + cite + "</cite>");
            this.bw.write("</p></blockquote>");
        }
        // 初期化
        this.blockList = new ArrayList<String>();
    }

    /**
     * コードブロックに表示する Java 言語のシンタックスハイライト処理.
     * @param line 対象
     * @return 変換後
     */
    private String editSyntaxHighlight4J(final String line) {
        String ret = line;
        for (int i = 0; i < this.syntaxHighlighting4J.length; i++) {
            ret = ret.replace(syntaxHighlighting4J[i], "<span style=\"color: #B92A2C;\">"
                            + syntaxHighlighting4J[i] + "</span>");
        }
        return ret;
    }

    /**
     * 初期の書き出し.
     *
     * @throws IOException
     */
    private void initWrite() throws IOException {

        this.bw.write("<!DOCTYPE html>" + CONST_CRLF);
        this.bw.write("<html lang=\"ja\">" + CONST_CRLF);
        this.bw.write("<head>" + CONST_CRLF);
        this.bw.write("  <meta charset=\"UTF-8\"/>" + CONST_CRLF);
        this.bw.write("  <title>" + this.title + "</title>" + CONST_CRLF);
        this.bw.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://fonts.googleapis.com/css?family=Lato\" />");
        this.bw.write("  <style type=\"text/css\">" + CONST_CRLF);
        this.bw.write("      body {margin-right: auto; margin-left: auto; width: 800px; " +
                "background-color: #fedadf; font-family: Lato, 'メイリオ', 'Meiryo',"+
                "'ヒラギノ丸ゴ Pro W4','ヒラギノ丸ゴ Pro','Hiragino Maru Gothic Pro', 'ヒラギノ角ゴ Pro W3', 'Hiragino Kaku Gothic Pro', sans-self " +
                "!important; font-size: 1.0em; -webkit-font-smoothing: antialiased;}" + CONST_CRLF);
        this.bw.write("      h1 {position: relative; margin: 0 0 1.5em; padding: 0.8em; " +
                "background: #B92A2C; color: #fff; font-size: 1.143em; font-weight: bold; " +
                "border-radius: 5px; -webkit-border-radius: 5px; -moz-border-radius: 5px;}" + CONST_CRLF);
        this.bw.write("      h1:after {position: absolute; bottom: -15px; left: 10%; z-index: 90; " +
                "margin-left: -15px; border-top: 15px solid #B92A2C; border-left: 15px solid transparent; " +
                "border-bottom: 0; content: \"\";}" + CONST_CRLF);
        this.bw.write("      h2 {position: relative; margin: 10 10 1.5em; padding: 0.8em 0 0.8em 1.5em; " +
                "border: 1px solid #B92A2C; font-size: 1.143em; font-weight: bold; background: #ffb7b8;" +
                "border-radius: 5px; -webkit-border-radius: 5px; -moz-border-radius: 5px;}" + CONST_CRLF);
        this.bw.write("      h2:before {content: \"\"; position: absolute; background: #B92A2C; " +
                "top: 50%; left: 0.5em; margin-top: -15px; height: 30px; width: 8px; " +
                "border-radius: 2px; -webkit-border-radius: 2px; -moz-border-radius: 2px;}" + CONST_CRLF);
        this.bw.write("      h3 {margin: 0 0 1.0em; padding: 0.4em; border-left: 7px solid #B92A2C; " +
                "border-bottom: 1px dashed #B92A2C; font-size: 1.0em; font-weight: bold; margin-left: 25px;}");
        this.bw.write("      code {font-family: Menlo, Consolas, 'DejaVu Sans Mono', monospace; " +
                "font-size: 14px; line-height: 1.4;}");
        this.bw.write("      .code-box {margin-left: 35px; background-color: #eee; padding: 0.8em; " +
                "border:1px solid #A8A8A8;border-radius: 6px; -webkit-border-radius: 6px; -moz-border-radius: 6px; overflow: auto;}");


        this.bw.write("      .inner-link-box {margin-left: 35px; background-color: #eee; padding: 0.8em; border:1px solid #A8A8A8;}");
        this.bw.write("      .inner-link-outline {margin-left: 85px; font-size: 0.7em;}");


        this.bw.write("      hr {margin-left: 35px; border: none; border-top: dashed 1px #A8A8A8; height: 1px; " +
                "color: #FFFFFF; margin: 0 6 0 6;}" + CONST_CRLF);
        this.bw.write("      .footnote {font-size: 0.8em;}" + CONST_CRLF);
        this.bw.write("      .supText {font-size: 0.6em; vertical-align: super; " +
                "position: relative; top: -0.1em; color: #B92A2C}" + CONST_CRLF);
        this.bw.write("      .footer {width: auto; height: 25px; line-height: 25px; " +
                "background-color: #B92A2C; color: #fff; font-size: 0.9em; text-align: right;}" + CONST_CRLF);
        this.bw.write("      table.type01 {margin-left: 35px; border-collapse: separate;" +
                " border-spacing: 1px; text-align: center; line-height: 1.5;}" + CONST_CRLF);
        this.bw.write("      table.type01 th {padding: 10px; font-weight: normal; vertical-align: top;" +
                " color: #fff; background: #B92A2C;}" + CONST_CRLF);
        this.bw.write("      table.type01 td {padding: 10px; font-weight: normal; vertical-align: top;" +
                " border-bottom: 1px solid #ccc; background: #eee;}" + CONST_CRLF);
        //this.bw.write("      img {margin-left: 35px; max-width: 95%; border: 1px #B92A2C solid;}" + CONST_CRLF);
        this.bw.write("      img {max-width: 95%; border: 1px #B92A2C solid;}" + CONST_CRLF);
        this.bw.write("      .footnote_title {margin-left: 35px; font-weight: bold;}" + CONST_CRLF);
        this.bw.write("      .heading_lv1 {margin-left: 15px;}" + CONST_CRLF);
        this.bw.write("      .heading_lv2 {margin-left: 15px;}" + CONST_CRLF);
        this.bw.write("      .heading_lv3 {margin-left: 25px;}" + CONST_CRLF);
        this.bw.write("     p {text-indent: 1em;}" + CONST_CRLF);
        this.bw.write("     a {text-decoration: none;}" + CONST_CRLF);
        this.bw.write("      .left_indent {margin-left: 20px;}" + CONST_CRLF);
        this.bw.write("      .toc {font-size: 0.8em;}" + CONST_CRLF);
        this.bw.write("      .page_top {font-size: 0.8em; text-align: right; }" + CONST_CRLF);
        this.bw.write("      .box27 {margin-left: 35px; position: relative; background-color: #eee; padding: 0.8em; border: solid 2px #B92A2C;}" + CONST_CRLF);
        this.bw.write("      .box27 .box-title {position: absolute; display: inline-block; top: -27px;"
                           + " left: -2px; padding: 0 9px; height: 25px; line-height: 25px; vertical-align: middle;"
                           + " font-size: 12px; background: #B92A2C; color: #ffffff; font-weight: normal;"
                           + " border-radius: 5px 5px 0 0;}" + CONST_CRLF);
        this.bw.write("      .box27 p { margin: 0; padding: 0;}" + CONST_CRLF);
        this.bw.write("      .sankou-badge, .link-badge { padding: 1px 6px; margin-right: 8px; margin-left: 1px; font-size: 70%; color: white; "
                           + "border-radius: 2px; box-shadow: 0 0 3px #ddd; white-space: nowrap; }" + CONST_CRLF);

        this.bw.write("      .link-badge { background-color: #58ACFA; }");
        this.bw.write("      .sankou-badge { background-color: #B92A2C; }");
        this.bw.write("      .code-box.deco::-webkit-scrollbar { height: 10px; }" + CONST_CRLF);
        this.bw.write("      .code-box.deco::-webkit-scrollbar-track { border-radius: 50px; background: #eee; }" + CONST_CRLF);
        this.bw.write("      .code-box.deco::-webkit-scrollbar-thumb { border-radius: 10px; background: #A8A8A8; }" + CONST_CRLF);
        this.bw.write("      .marker_yellow_hoso { background: linear-gradient(transparent 60%, #ffff66 60%); font-weight: bold;}" + CONST_CRLF);
        this.bw.write("      .important_sentence { color: red; text-decoration: underline; font-weight: bold;}" + CONST_CRLF);
        this.bw.write("      .bold_sentence { text-decoration: underline; font-weight: bold;}" + CONST_CRLF);
        this.bw.write("      .text_strike {text-decoration: line-through;}" + CONST_CRLF);
        this.bw.write("      .strong_impact { background: linear-gradient(transparent 80%, #ffff66 80%); font-weight: bold; font-size: 2.5em}" + CONST_CRLF);

        this.bw.write("       blockquote { margin-left: 35px; position: relative; padding: 5px 10px 5px 32px; width: 721px; "
                           + "font-style: italic; background: #ffcce5; border-bottom: solid 3px #B92A2C; border-top: solid 1px #B92A2C; }" + CONST_CRLF);
        this.bw.write("       blockquote:before{ display: inline-block; position: absolute; top: 10px; left: -15px; "
                           + "width: 100px; height: 30px; vertical-align: middle; text-align: center; content: \"Quote\"; "
                           + "font-family: FontAwesome; color: #FFF; font-size: 18px; line-height: 30px; background: #B92A2C; font-weight: 900; }" + CONST_CRLF);
        this.bw.write("       blockquote:after{ position: absolute; content: ''; top: 40px; left: -15px; border: none; "
                           + "border-bottom: solid 8px transparent; border-right: solid 15px #daaa64; }" + CONST_CRLF);
        this.bw.write("       blockquote p { position: relative; padding: 0; margin: 10px 0; z-index: 3; line-height: 1.7; }" + CONST_CRLF);
        this.bw.write("       blockquote cite { display: block; text-align: right; color: #888888; font-size: 0.9em; }" + CONST_CRLF);
        this.bw.write("      .help {font-size: 0.4em; text-align: right}" + CONST_CRLF);

        this.bw.write("  </style>" + CONST_CRLF);
        this.bw.write("</head>" + CONST_CRLF);
        this.bw.write("<body>" + CONST_CRLF);
    }

    /**
     * 最後の書き出し.
     *
     * @throws IOException
     */
    private void finalWrite() throws IOException {

        // 生成処理時刻を求める
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedLdt = sdf.format(new Date());

        this.bw.write("<br>" + CONST_CRLF);
        this.bw.write("<div class=\"page_top\"><a href=\"#\">^page top</a></div>" + CONST_CRLF);
        this.bw.write("<div class=\"footer\">Last Generated:" + formattedLdt + "／Generated by Maki "
                + CONST_VERSION + "&nbsp;</div>" + CONST_CRLF);
        this.bw.write("<div class=\"help\"><code>[``]: code</code>&nbsp;&nbsp;&nbsp;"
                + "<code>[##]: ym + u</code>&nbsp;&nbsp;&nbsp;"
                + "<code>[@@]: r + b + u</code>&nbsp;&nbsp;&nbsp;<code>"
                + "[&&]: b + u</code>&nbsp;&nbsp;&nbsp;<code>"
                + "[%%]: strike</code>&nbsp;&nbsp;&nbsp;<code>"
                + "[__]: strong big</code></div>");
        this.bw.write("</body>" + CONST_CRLF);
        this.bw.write("</html>" + CONST_CRLF);
    }

    /**
     * 行の中に指定された記号をタグに変換する.
     * (本文判定用のチェックモード指定版)
     * @param line 対象の行
     * @return 編集後の文字列
     */
    private String editLine(final String line) {
        return this.editLine(line, true);
    }

    /**
     * 【改訂版】インラインの編集(汎用).
     * @param line 行データ
     * @param delim 区切り文字
     * @param startTag 置換する開始タグ
     * @param endTag 置換する終了タグ
     * @return 編集後の行データ
     */
    private String editInline(final String line,
                              final String delim,
                              final String startTag,
                              final String endTag) {

        String retLine = line;
        if (line.indexOf(delim) != -1) {
            String[] tokens = line.split(delim);
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < tokens.length; i++) {
                if (i % 2 == 0) {
                    if (i == (tokens.length - 1)) {
                        ret.append(tokens[i]);
                    } else {
                        ret.append(tokens[i] + startTag);
                    }
                } else {
                    ret.append(tokens[i] + endTag);
                }
            }
            retLine = ret.toString();
        }
        return retLine;
    }

    /**
     * 行中の脚注の変換.
     * @param line 1行(各種変換済み)
     * @param mode モード
     * @return 変換後1行
     */
    private String editInlineFootnote(final String line, final boolean mode) {
        //  行中の脚注の変換
        if (line.indexOf("$") != -1
            && !(line.length() > 0 && line.substring(0, 1).equals(" ") && line.indexOf("*") == -1)) {
            // 行中に脚注が含まれているため，脚注部分の文字列を抜き出し，
            // 代わりに *1 のような参照番号を付与する.
            // 複数個所ある可能性もあるので，取り出し方には留意する.
            // 脚注の入れ子は現時点では対象外とする.

            // Maki で脚注は $ で挟むことにした
            // StringTokenizer で区切れば本文と脚注が交互に取れる
            StringTokenizer st = new StringTokenizer(line, "$");

            int i = 0;

            // 返却用の文字列
            StringBuffer ret = new StringBuffer();
            while (st.hasMoreTokens()) {

                if (i % 2 == 0) {

                    // 本文
                    ret.append(st.nextToken());

                } else {

                    String tmp = st.nextToken();
                    StringBuffer headingWord = new StringBuffer();
                    if (tmp.substring(0, 1).equals("{")) {
                        // 見出し語が定義されているので，閉じかっこまで検索する
                        for (int j = 0; j < tmp.length(); j++) {
                            String token = tmp.substring(j, j+1);
                            if (token.equals("}")) {
                                tmp = tmp.substring(j+1);
                                break;
                            } else {
                                if (!token.equals("{")) {
                                    headingWord.append(token);
                                }
                            }
                        }
                    }

                    // 脚注書き出し処理
                    if (mode) {
                        // 脚注の文章をリストへ保存する
                        if (headingWord.toString().equals("")) {
                            this.footNoteList.add("<span id=\"" + (this.footNoteCount + 1)
                                    + "\"><a href=\"#o"+ (this.footNoteCount + 1)
                                    + "\">[" + (this.footNoteCount + 1) + "]</a></span>: " + tmp);

                        } else {
                            this.footNoteList.add("<span id=\"" + (this.footNoteCount + 1)
                                    + "\"><a href=\"#o"+ (this.footNoteCount + 1)
                                    + "\">[" + (this.footNoteCount + 1) + "]</a></span>: <b><u>" + headingWord + "</u></b>: " + tmp);
                        }
                    }

                    // 脚注リンクの上にマウスカーソルを置いた時のツールチップ表示用の脚注文章
                    // とりあえずダブルクオーテーションとかを除去する
                    String tooltip = tmp;
                    tooltip = tooltip.replace("\"", "");

                    if (mode) {
                        // 本文側に参照番号を入れる
                        ret.append(" <span class=\"supText\" id=\"o" + (this.footNoteCount + 1) + "\"><a href=\"#"
                                + (this.footNoteCount + 1) + "\" title=\"" + tooltip + "\">*" + (this.footNoteCount + 1) + "</a></span> ");
                        this.footNoteCount++;
                    } else {
                        // チェック用の編集
                        ret.append(" <span class=\"supText\" id=\"o" + (this.footNoteCount) + "\"><a href=\"#"
                                + (this.footNoteCount) + "\" title=\"" + tooltip + "\">*" + (this.footNoteCount) + "</a></span> ");
                    }
                }

                i++;
            }

            // 結果を設定する
            return ret.toString();
        }
        return line;
    }

    /**
     * 行中リンクの変換.
     * @param line 1行
     * @return 変換後の1行
     */
    private String editInlineLink(final String line) {
        log("editInlineLink --- START");
        // 記法は簡略化のためこのようなものに。
        if (line.indexOf("!") != -1) {
            log(line);
            String[] tokens = line.split("!");
            log("tokens num:[" + tokens.length + "]");
            StringBuffer ret = new StringBuffer();
            int i = 0;
            for (String token : tokens) {
                if (i % 2 == 0) {
                    log("本文");
                    // 本文
                    // ! で行を分割した時に偶数番目のトークンを
                    // 本文として扱う。
                    ret.append(token);
                } else {
                    log("リンク");
                    // リンクの記述
                    // 奇数番目のトークンをリンクとして扱う。
                    String str = token;
                    StringTokenizer tmp = new StringTokenizer(str, ",");
                    if (tmp.countTokens() == 2) {
                        String linkName = tmp.nextToken();
                        String url = tmp.nextToken();
                        ret.append("<span class=\"sankou-badge\">Link</span><a href=\"" + url + "\" target=\"_blank\">" + linkName
                                + "</a>");
                    } else {
                        ret.append(str);
                    }
                }
                i++;
            }
            log("editInlineLink --- END1");
            return ret.toString();
        }
        log("editInlineLink --- END2");
        return line;
    }

    /**
     * 行の中に指定された記号をタグに変換する.
     *
     * @param line 対象の行
     * @param mode チェックモード
     * @return 編集後の文字列
     */
    private String editLine(final String line,
                            final boolean mode) {
        String work = line;
        work = this.editInline(work, "``", "<code style=\"color: #B92A2C; border: solid 1px #c0c0c0; border-radius: 3px 3px 3px 3px; background-color: #dcdcdc; padding: 2px;\">", "</code>");
        work = this.editInline(work, "##", "<span class=\"marker_yellow_hoso\">", "</span>");
        work = this.editInline(work, "@@", "<span class=\"important_sentence\">", "</span>");
        work = this.editInline(work, "&&", "<span class=\"bold_sentence\">", "</span>");
        work = this.editInline(work, "%%", "<span class=\"text_strike\">", "</span>");
        work = this.editInline(work, "__", "<span class=\"strong_impact\">", "</span>");
        // 行中の脚注の編集
        work = this.editInlineFootnote(work, mode);
        // リンクの作成
        work = this.editInlineLink(work);
        return work;
    }

    /**
     * 脚注を書き出す.
     *
     * @throws IOException
     */
    private void footNoteWrite() throws IOException {

        // 脚注がひとつもなければ処理終了
        if (this.footNoteList.size() == 0) {
            return;
        }

        // 脚注用に少しスペースを空ける
        //this.bw.write("<br><br><br>");
        this.bw.write("<hr>" + CONST_CRLF);
        this.bw.write("<div class=\"footnote_title\">脚注</div>" + CONST_CRLF);
        this.bw.write("<ul style=\"list-style:none\">" + CONST_CRLF);

        for (int i = 0; i < this.footNoteList.size(); i++) {
            String footNote = this.footNoteList.get(i);

            this.bw.write("<li><span class=\"footnote\">" + footNote + "</span></li>" + CONST_CRLF);
        }

        this.bw.write("</ul>" + CONST_CRLF);
        this.bw.write("<br>" + CONST_CRLF);
        this.footNoteList = new ArrayList<String>();
    }

    /**
     * テーブルを書き出す.
     *
     * @throws IOException
     */
    private void tableWrite() throws IOException {

        // テーブルデータがない場合は処理終了
        if (this.tableHeader.equals("")
         && this.tableDataList.size() == 0) {
            return;
        }

        this.bw.write("<table class=\"type01\">" + CONST_CRLF);
        this.bw.write("  <tr>" + CONST_CRLF);

        StringTokenizer header = new StringTokenizer(this.tableHeader, "|");
        while (header.hasMoreTokens()) {
            this.bw.write("    <th>" + header.nextToken().trim() + "</th>" + CONST_CRLF);
        }

        this.bw.write("  </tr>" + CONST_CRLF);

        for (int i = 0; i < this.tableDataList.size(); i++) {
            this.bw.write("  <tr>" + CONST_CRLF);

            String line = this.tableDataList.get(i);
            StringTokenizer data = new StringTokenizer(line, "|");

            while (data.hasMoreTokens()) {
                this.bw.write("    <td>" + data.nextToken().trim() + "</td>" + CONST_CRLF);
            }

            this.bw.write("  </tr>" + CONST_CRLF);
        }

        this.bw.write("</table>" + CONST_CRLF);

        this.tableHeader = "";
        this.tableDataList = new ArrayList<String>();
    }

    /**
     * ページの上部に目次を出力する.
     */
    private void createTOC() throws IOException {

        int tmpH1Cnt = 0;
        int tmpH2Cnt = 0;
        int tmpH3Cnt = 0;

        int nowLv = 0;
        this.bw.write("<u>Table of Contents.</u> &nbsp; <span class=\"toc\"><a href=\"" + this.tocFilePath + "\">top</a></span>");
        this.bw.write("<ul class=\"toc\">" + CONST_CRLF);
        for (int i = 0; i < this.backupList.size(); i++) {
            String line = this.backupList.get(i);
            if (line == null || line.length() == 0) {
                continue;
            }
            String prefix = line.substring(0, 1);
            if ("=".equals(prefix)) {
                if (nowLv == 0) {
                    this.bw.write("  <li><a href=\"#h1_" + tmpH1Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH1Cnt++;
                    nowLv = 1;
                } else if (nowLv == 1) {
                    this.bw.write("  <li><a href=\"#h1_" + tmpH1Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH1Cnt++;
                } else if (nowLv == 2) {
                    this.bw.write("  </ul>" + CONST_CRLF);
                    this.bw.write("  <li><a href=\"#h1_" + tmpH1Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH1Cnt++;
                    nowLv = 1;
                } else if (nowLv == 3) {
                    this.bw.write("    </ul>" + CONST_CRLF);
                    this.bw.write("  </ul>" + CONST_CRLF);
                    this.bw.write("  <li><a href=\"#h1_" + tmpH1Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH1Cnt++;
                    nowLv = 1;
                }
            } else if ("-".equals(prefix)) {
                if (nowLv == 1) {
                    this.bw.write("  <ul>" + CONST_CRLF);
                    this.bw.write("    <li><a href=\"#h2_" + tmpH2Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH2Cnt++;
                    nowLv = 2;
                } else if (nowLv == 2) {
                    this.bw.write("    <li><a href=\"#h2_" + tmpH2Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH2Cnt++;
                } else if (nowLv == 3) {
                    this.bw.write("    </ul>" + CONST_CRLF);
                    this.bw.write("    <li><a href=\"#h2_" + tmpH2Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH2Cnt++;
                    nowLv = 2;
                }
            } else if ("~".equals(prefix)) {
                if (nowLv == 2) {
                    this.bw.write("    <ul>" + CONST_CRLF);
                    this.bw.write("      <li><a href=\"#h3_" + tmpH3Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH3Cnt++;
                    nowLv = 3;
                } else if (nowLv == 3) {
                    this.bw.write("      <li><a href=\"#h3_" + tmpH3Cnt + "\">" + this.backupList.get(i - 1) + "</a></li>" + CONST_CRLF);
                    tmpH3Cnt++;
                }
            }
        }
        if (nowLv == 2) {
            this.bw.write("  </ul>" + CONST_CRLF);
        } else if (nowLv == 3) {
            this.bw.write("    </ul>" + CONST_CRLF);
            this.bw.write("  </ul>" + CONST_CRLF);
        }
        this.bw.write("</ul>" + CONST_CRLF);
    }

    /**
     * プログラム実行時のログを出力(標準出力).
     * @param msg ログに出す文字.
     */
    private void log(final String msg) {

        if (LOG_OUTPUT.equals(LOG_LEVEL_OFF)) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        String dateMsg = sdf.format(cal.getTime());
        System.out.println(dateMsg + " - " + msg);
    }
}
