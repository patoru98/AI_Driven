import { useState } from 'react'
import type { Todo, TodoInput } from '../types/todo'
import {
  MEMO_MAX_LENGTH,
  TASK_MAX_LENGTH,
  validateTodoInput,
} from '../utils/validation'

type Props = {
  todo: Todo
  onUpdate: (id: number, input: TodoInput) => Promise<void>
  onDelete: (id: number) => Promise<void>
  onComplete: (id: number, done: boolean) => Promise<void>
  onError: (messages: string[]) => void
}

const toInput = (todo: Todo): TodoInput => ({
  task: todo.task,
  registedDate: todo.registedDate,
  limitDate: todo.limitDate,
  memo: todo.memo,
})

export const TodoRow = ({ todo, onUpdate, onDelete, onComplete, onError }: Props) => {
  const [editing, setEditing] = useState(false)
  const [draft, setDraft] = useState<TodoInput>(() => toInput(todo))
  const [busy, setBusy] = useState(false)

  const startEdit = () => {
    setDraft(toInput(todo))
    setEditing(true)
  }

  const handleUpdate = async () => {
    const errors = validateTodoInput(draft)
    if (errors.length > 0) {
      onError(errors)
      return
    }
    setBusy(true)
    try {
      await onUpdate(todo.id, draft)
      setEditing(false)
    } finally {
      setBusy(false)
    }
  }

  const run = async (fn: () => Promise<void>) => {
    setBusy(true)
    try {
      await fn()
    } finally {
      setBusy(false)
    }
  }

  const value = editing ? draft : toInput(todo)

  return (
    <tr className={todo.done ? 'todo-row done' : 'todo-row'}>
      <td>
        <input
          type="checkbox"
          aria-label="完了"
          checked={todo.done}
          disabled={editing || busy}
          onChange={(e) => run(() => onComplete(todo.id, e.target.checked))}
        />
      </td>
      <td>
        <input
          type="text"
          aria-label="タスク"
          maxLength={TASK_MAX_LENGTH}
          value={value.task}
          disabled={!editing}
          onChange={(e) => setDraft({ ...draft, task: e.target.value })}
        />
      </td>
      <td>
        <input type="date" aria-label="設定日" value={value.registedDate} disabled />
      </td>
      <td>
        <input
          type="date"
          aria-label="期日"
          min={value.registedDate}
          value={value.limitDate}
          disabled={!editing}
          onChange={(e) => setDraft({ ...draft, limitDate: e.target.value })}
        />
      </td>
      <td>
        <textarea
          aria-label="備考"
          cols={20}
          rows={1}
          maxLength={MEMO_MAX_LENGTH}
          value={value.memo}
          disabled={!editing}
          onChange={(e) => setDraft({ ...draft, memo: e.target.value })}
        />
      </td>
      <td>
        {editing ? (
          <button type="button" onClick={handleUpdate} disabled={busy}>
            更新
          </button>
        ) : (
          <button type="button" onClick={startEdit} disabled={busy}>
            編集
          </button>
        )}
      </td>
      <td>
        <button
          type="button"
          onClick={() => run(() => onDelete(todo.id))}
          disabled={editing || busy}
        >
          削除
        </button>
      </td>
    </tr>
  )
}
