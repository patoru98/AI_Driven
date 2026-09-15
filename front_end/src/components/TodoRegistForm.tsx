import { useState } from 'react'
import type { TodoInput } from '../types/todo'
import { todayIso } from '../utils/date'
import {
  MEMO_MAX_LENGTH,
  TASK_MAX_LENGTH,
  validateTodoInput,
} from '../utils/validation'

type Props = {
  onRegist: (input: TodoInput) => Promise<void>
  onError: (messages: string[]) => void
}

const emptyInput = (): TodoInput => ({
  task: '',
  registedDate: todayIso(),
  limitDate: '',
  memo: '',
})

export const TodoRegistForm = ({ onRegist, onError }: Props) => {
  const [input, setInput] = useState<TodoInput>(emptyInput)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async () => {
    const errors = validateTodoInput(input)
    if (errors.length > 0) {
      onError(errors)
      return
    }
    setSubmitting(true)
    try {
      await onRegist(input)
      setInput(emptyInput())
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <tr className="todo-regist-form">
      <td></td>
      <td>
        <input
          type="text"
          aria-label="タスク"
          placeholder="登録するtodoを記載"
          maxLength={TASK_MAX_LENGTH}
          value={input.task}
          onChange={(e) => setInput({ ...input, task: e.target.value })}
        />
      </td>
      <td>
        <input type="date" aria-label="設定日" value={input.registedDate} disabled />
      </td>
      <td>
        <input
          type="date"
          aria-label="期日"
          min={input.registedDate}
          value={input.limitDate}
          onChange={(e) => setInput({ ...input, limitDate: e.target.value })}
        />
      </td>
      <td>
        <textarea
          aria-label="備考"
          cols={20}
          rows={1}
          maxLength={MEMO_MAX_LENGTH}
          value={input.memo}
          onChange={(e) => setInput({ ...input, memo: e.target.value })}
        />
      </td>
      <td>
        <button type="button" onClick={handleSubmit} disabled={submitting}>
          登録
        </button>
      </td>
      <td></td>
    </tr>
  )
}
