import type { TodoInput } from '../types/todo'

export const TASK_MAX_LENGTH = 100
export const MEMO_MAX_LENGTH = 500

export const validateTodoInput = (input: TodoInput): string[] => {
  const errors: string[] = []

  if (input.task.trim() === '') {
    errors.push('タスクを入力してください')
  } else if (input.task.length > TASK_MAX_LENGTH) {
    errors.push(`タスクは${TASK_MAX_LENGTH}文字以内で入力してください`)
  }

  if (input.memo.length > MEMO_MAX_LENGTH) {
    errors.push(`備考は${MEMO_MAX_LENGTH}文字以内で入力してください`)
  }

  if (input.limitDate !== '' && input.limitDate < input.registedDate) {
    errors.push('期日は設定日以降の日付を指定してください')
  }

  return errors
}
