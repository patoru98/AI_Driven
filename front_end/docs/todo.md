# main画面設計書
## 機能概要
TODOの管理を行う画面

## 画面項目定義
<html>
    <table align="center">
        <tbody>
            <tr>
			    <th rowspan="2">No</th>
			    <th colspan="5">表示情報</th>
		    </tr>
		    <tr>
			    <th>画面項目名</th>
			    <th>画面項目種別</th>
			    <th>情報取得元</th>
                <th>編集仕様</th>
                <th>初期値</th>
		    </tr>
		    <tr>
    			<th colspan="6">領域名：TODO一覧-登録フォーム</th>
	    	</tr>
		    <tr>
			    <td>1</td>
			    <td>タスク</td>
			    <td>text</td>
			    <td>ユーザーの入力</td>
                <td>100文字以内</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>2</td>
			    <td>設定日</td>
			    <td>date</td>
			    <td>入力当日のシステム日</td>
                <td>YYYY/MM/DD</td>
                <td>入力当日のシステム日</td>
		    </tr>
            <tr>
			    <td>3</td>
			    <td>期日</td>
			    <td>date</td>
			    <td>カレンダーからの入力</td>
                <td>YYYY/MM/DD、設定日<=期日であること</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>4</td>
			    <td>備考</td>
			    <td>text</td>
			    <td>ユーザーの入力</td>
                <td>500文字以内</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>5</td>
			    <td>登録ボタン</td>
			    <td>button</td>
			    <td></td>
                <td>-</td>
                <td>TODO登録APIを実行</td>
		    </tr>
            <tr>
    			<th colspan="6">領域名：TODO一覧</th>
	    	</tr>
            <tr>
			    <td>1</td>
			    <td>タスク</td>
			    <td>text</td>
			    <td>ユーザーの入力</td>
                <td>100文字以内</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>2</td>
			    <td>設定日</td>
			    <td>date</td>
			    <td>TODO取得API</td>
                <td>YYYY/MM/DD</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>3</td>
			    <td>期日</td>
			    <td>date</td>
			    <td>カレンダーからの入力</td>
                <td>YYYY/MM/DD、設定日<=期日であること</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>4</td>
			    <td>備考</td>
			    <td>text</td>
			    <td>TODO取得API</td>
                <td>500文字以内</td>
                <td>-</td>
		    </tr>
            <tr>
			    <td>5</td>
			    <td>編集ボタン</td>
			    <td>button</td>
			    <td></td>
                <td>-</td>
                <td>編集モードへ切り替え</td>
		    </tr>
            <tr>
			    <td>6</td>
			    <td>更新ボタン</td>
			    <td>button</td>
			    <td></td>
                <td>-</td>
                <td>TODO更新APIを実行</td>
		    </tr>
            <tr>
			    <td>6</td>
			    <td>削除ボタン</td>
			    <td>button</td>
			    <td></td>
                <td>-</td>
                <td>TODO削除APIを実行</td>
		    </tr>
        </tbody>
    </table>
</html>

## 画面イベント一覧
API設計書へのリンクが存在しない場合はモックで動作したこととする
| イベント名 | タイミング | イベント概要 |
| -- | -- | -- |
| 初回読み込み | 画面アクセス時 | todo取得API実行 |
| todo登録 | 登録ボタン押下時 | [TODO登録API](/back_end/src/docs/api/TodoRegistAPI.md)実行 |
| todo編集 | 編集ボタン押下時 | タスク、期日、備考を編集可能にする |
| todo更新 | 更新ボタン押下時 | todo更新API実行 |
| todo削除 | 削除ボタン押下時 | todo削除API実行 |
| todo完了 | チェックボックス入力時 | todo完了API実行 |

## 入出力一覧
<html>
    <table align="center">
        <tbody>
            <tr>
                <th rowspan="2">No.</th>
                <th rowspan="2">入出力名</th>
                <th rowspan="2">種別</th>
                <th rowspan="2">I/O</th>
                <th colspan="5">DBアクセス識別</th>
                <th colspan="2" rowspan="2">備考</th>
            </tr>
            <tr>
                <th>C</th>
                <th>R</th>
                <th>U</th>
                <th>D</th>
                <th>ロック対象</th>
            </tr>
            <tr>
                <td>1</td>
                <td>TODO</td>
                <td>テーブル</td>
                <td>I/O</td>
                <td>◯</td>
                <td>◯</td>
                <td>◯</td>
                <td>◯</td>
                <td>-</td>
                <td>-</td>
            </tr>
	    </tbody>
    </table>
</html>

## 画面イメージモック
<html>
    <table align="center">
        <tbody>
            <tr>
                <th>☑</th>
                <th>タスク</th>
                <th>設定日</th>
                <th>期日</th>
                <th>備考</th>
                <th></th>
                <th></th>
            </tr>
            <tr>
                <td></td>
                <td><input type="text" value="登録するtodoを記載"></td>
                <td><input type="date" value="2026-09-11" disabled></td>
                <td><input type="date"></td>
                <td><textarea cols="20" rows="1"></textarea></td>
                <td><button type="button">登録</button></td>
                <td></td>
            </tr>
            <tr>
                <td><input type="checkbox"></td>
                <td><input type="text" value="登録されているtodoを表示" disabled></td>
                <td><input type="date" value="2026-09-11" disabled></td>
                <td><input type="date" value="2026-10-01" disabled></td>
                <td><textarea cols="20" rows="1" disabled>備考</textarea></td>
                <td><button type="button">編集</button></td>
                <td><button type="button">削除</button></td>
            </tr>
            <tr>
                <td><input type="checkbox"></td>
                <td><input type="text" value="登録されているtodoを表示-2" disabled></td>
                <td><input type="date" value="2026-09-11" disabled></td>
                <td><input type="date" value="2026-10-01" disabled></td>
                <td><textarea cols="20" rows="1" disabled>備考-2</textarea></td>
                <td><button type="button">編集</button></td>
                <td><button type="button">削除</button></td>
            </tr>
            <tr>
                <td><input type="checkbox" disabled></td>
                <td><input type="text" value="todo編集中"></td>
                <td><input type="date" value="2026-09-11" disabled></td>
                <td><input type="date" value="2026-10-01" dibled></td>
                <td><textarea cols="20" rows="1"></textarea></td>
                <td><button type="button">更新</button></td>
                <td><button type="button" disabled>削除</button></td>
            </tr>
        </tbody>
    </table>
</html>