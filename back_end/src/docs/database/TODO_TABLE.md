# テーブル定義書
## テーブル詳細
### テーブル情報
| 論理名 | 物理名 |
| -- | -- |
| TODOテーブル | TODO_TABLE |

### カラム情報
| No. | 論理名 | 物理名 | データ型 | PK | NOT NULL | デフォルト | 備考 |
| -- | -- | -- | -- | -- | -- | -- | -- |
| 1 | P_KEY | 主キー | INTEGER | ◯ | ◯ | - | |
| 2 | TASK | 課題 | VARCHAR | - | ◯ | - | |
| 3 | REGISTED_DATE | 登録日 | DATE | - | - | YYYY/MM/DD | |
| 4 | LIMIT_DATE | 期日 | DATE | - | - | YYYY/MM/DD | |
| 5 | MEMO | 備考 | VARCHAR | - | - | - | |
| 6 | CREATE_DATE | 作成年月日 | DATE | - | ◯ | YYYY/MM/DD | |
| 7 | UPDATE_DATE | 更新年月日 | DATE | - | ◯ | YYYY/MM/DD | |

