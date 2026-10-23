# Todo Update API詳細設計書
## APIの概要
| 項目 | 内容 |
| -- | -- |
| API名 | Todo Update API |
| 概要 | TODOを更新する |
| Method | POST |

### アクセスURI
- `/api/todo/update`

### リクエスト
- Content-Type: `application/json`
- `p_key`で更新対象のTODOを指定する。

```json
{
  "p_key": 1,
  "task": "買い物へ行く",
  "registed_date": "2026/01/05",
  "limit_date": "2026/01/31",
  "memo": "牛乳を買う"
}
```

| JSONキー | 型 | 必須 | 制約・説明 |
| -- | -- | -- | -- |
| `p_key` | Number | ○ | 更新対象の主キー。1以上の整数。 |
| `task` | String | ○ | TODOの内容。空文字不可、100文字以内。 |
| `registed_date` | String | - | タスク開始日。`YYYY/MM/DD`形式。省略、null、空文字を許容する。 |
| `limit_date` | String | - | タスク終了日。`YYYY/MM/DD`形式。省略、null、空文字を許容する。 |
| `memo` | String | - | 備考。500文字以内。省略、null、空文字を許容する。 |

`task`、日付、`memo`の形式・文字数検証はTodo Regist APIと同じ。更新項目に省略、null、空文字が指定された任意項目はnullとして更新する。

### レスポンスコード
| 業務コード (`code`) | HTTPステータス | 説明 |
| -- | -- | -- |
| 200 | 200 OK | 更新成功 |
| 300 | 400 Bad Request | JSON形式不正、または入力チェックエラー |
| 301 | 500 Internal Server Error | DBアクセスエラー |
| 302 | 404 Not Found | 指定した`p_key`のTODOが存在しない |

### アウトプットパラメータ
| JSONキー | 型 | 説明 |
| -- | -- | -- |
| `code` | Number | 業務コード。常に返却する。 |
| `p_key` | Number | 更新したTODOの主キー。成功時のみ返却する。 |
| `task` | String | 更新後のTODOの内容。成功時のみ返却する。 |
| `registed_date` | String | 更新後の開始日（`YYYY/MM/DD`）。値がある場合のみ返却する。 |
| `limit_date` | String | 更新後の終了日（`YYYY/MM/DD`）。値がある場合のみ返却する。 |
| `memo` | String | 更新後の備考。値がある場合のみ返却する。 |
| `messages` | Array\<String\> | エラー内容。エラー時のみ返却する。 |

### DB更新内容
- 対象テーブル：`TODO_TABLE`
- `P_KEY`がリクエストの`p_key`と一致するレコードを更新する。
- `TASK`、`REGISTED_DATE`、`LIMIT_DATE`、`MEMO`をリクエストの値に置き換える。
- `UPDATE_DATE`を更新処理実行日のシステム日付にする。
- `CREATE_DATE`と`DONE_FLAG`は変更しない。
- DBアクセスエラー時は更新を中断し、HTTP 500 / code 301を返す。
