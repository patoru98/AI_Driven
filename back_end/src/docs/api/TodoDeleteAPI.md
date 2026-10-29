# Todo Delete API詳細設計書
## APIの概要
| 項目 | 内容 |
| -- | -- |
| API名 | Todo Delete API |
| 概要 | TODOを削除する |
| Method | POST |

### アクセスURI
- `/api/todo/delete`

### リクエスト
- Content-Type: `application/json`
- `p_key`で削除対象のTODOを指定する。

```json
{
  "p_key": 1
}
```

| JSONキー | 型 | 必須 | 制約・説明 |
| -- | -- | -- | -- |
| `p_key` | Number | ○ | 削除対象の主キー。1以上の整数。 |

### レスポンスコード
| 業務コード (`code`) | HTTPステータス | 説明 |
| -- | -- | -- |
| 200 | 200 OK | 削除成功 |
| 300 | 400 Bad Request | JSON形式不正、または入力チェックエラー |
| 301 | 500 Internal Server Error | DBアクセスエラー |
| 302 | 404 Not Found | 指定した`p_key`のTODOが存在しない |

### アウトプットパラメータ
| JSONキー | 型 | 説明 |
| -- | -- | -- |
| `code` | Number | 業務コード。常に返却する。 |
| `messages` | Array\<String\> | エラー内容。エラー時のみ返却する。 |

### DB更新内容
- 対象テーブル：`TODO_TABLE`
- `P_KEY`がリクエストの`p_key`と一致するレコードを削除する。
- DBアクセスエラー時は削除を中断し、HTTP 500 / code 301を返す。
