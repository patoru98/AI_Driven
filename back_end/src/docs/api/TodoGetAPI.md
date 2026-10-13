# Todo Get API詳細設計書
## APIの概要
<table style="width:100%">
  <tr>
    <th>API名</th>
    <td>Todo Get API</td>
  </tr>
  <tr>
    <th>概要</th>
    <td>TODOを取得する</td>
  </tr>
  <tr>
    <th>Method</th>
    <td>Get</td>
  </tr>
</table>

### アクセスURI
- api/todo/getAll

### インプットパラメータ
- なし

### レスポンスコード
| 業務コード値 | HTTPステータス | 名称 | 説明 |
| -- | -- | -- | -- |
| 200 | 200 | OK | リクエスト成功 |
| 301 | 500 | NG | DB関連エラー |
| - | 200以外 | NG | 何かしらのエラーで失敗 |

### アウトプットパラメータ
| json key | 型 | 説明 |
| -- | -- | -- |
| code | Number | 業務コード(200/300/301) |
| p_key | Number | 更新時検索条件として仕様（成功時のみ） |
| done_flag | boolean | タスク完了フラグ（成功時のみ） |
| task | String | todoを格納 (成功時のみ) |
| registed_date | String | タスク開始日 (成功時のみ) |
| limit_date | String | タスク終了日 (成功時のみ) |
| memo | String | 備考 (成功時のみ) |
| messages | Array\<String\> | エラー内容 (エラー時のみ) |

## DBアクセス
- 対象テーブル：TODO_TABLE

| 論理名 | 物理名 | データ型 | 備考 |
| -- | -- | -- | -- |
| P_KEY | 主キー | INTEGER | |
| TASK | 課題 | VARCHAR | |
| REGISTED_DATE | 登録日 | DATE | |
| LIMIT_DATE | 期日 | DATE | |
| MEMO | 備考 | VARCHAR | |
| DONE_FLAG | 完了フラグ | BOOLEAN | タスク完了フラグ |

