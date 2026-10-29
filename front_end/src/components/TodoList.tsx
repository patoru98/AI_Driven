import { useCallback, useEffect, useState } from 'react'
import {
  ApiError,
  completeTodo,
  deleteTodo,
  fetchTodos,
  registTodo,
  updateTodo,
} from '../api/todoApi'
import type { Todo, TodoInput } from '../types/todo'
import { TodoRegistForm } from './TodoRegistForm'
import { TodoRow } from './TodoRow'

const toMessages = (e: unknown): string[] =>
  e instanceof ApiError ? e.messages : ['処理に失敗しました']

export const TodoList = () => {
  const [todos, setTodos] = useState<Todo[]>([])
  const [loading, setLoading] = useState(true)
  const [errors, setErrors] = useState<string[]>([])

  useEffect(() => {
    let cancelled = false
    fetchTodos()
      .then((list) => {
        if (!cancelled) setTodos(list)
      })
      .catch((e: unknown) => {
        if (!cancelled) setErrors(toMessages(e))
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const withErrorHandling = useCallback(
    async (fn: () => Promise<void>) => {
      setErrors([])
      try {
        await fn()
      } catch (e) {
        setErrors(toMessages(e))
      }
    },
    [],
  )

  const handleRegist = (input: TodoInput) =>
    withErrorHandling(async () => {
      await registTodo(input)
      setTodos(await fetchTodos())
    })

  const handleUpdate = (id: number, input: TodoInput) =>
    withErrorHandling(async () => {
      await updateTodo(id, input)
      setTodos(await fetchTodos())
    })

  const handleDelete = (id: number) =>
    withErrorHandling(async () => {
      await deleteTodo(id)
      setTodos((prev) => prev.filter((t) => t.id !== id))
    })

  const handleComplete = (id: number, done: boolean) =>
    withErrorHandling(async () => {
      const updated = await completeTodo(id, done)
      setTodos((prev) => prev.map((t) => (t.id === id ? updated : t)))
    })

  return (
    <section className="todo-list">
      {errors.length > 0 && (
        <ul className="todo-errors" role="alert">
          {errors.map((m, i) => (
            <li key={i}>{m}</li>
          ))}
        </ul>
      )}
      <table>
        <thead>
          <tr>
            <th>☑</th>
            <th>タスク</th>
            <th>設定日</th>
            <th>期日</th>
            <th>備考</th>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <TodoRegistForm onRegist={handleRegist} onError={setErrors} />
          {loading ? (
            <tr>
              <td colSpan={7}>読み込み中...</td>
            </tr>
          ) : (
            todos.map((todo) => (
              <TodoRow
                key={todo.id}
                todo={todo}
                onUpdate={handleUpdate}
                onDelete={handleDelete}
                onComplete={handleComplete}
                onError={setErrors}
              />
            ))
          )}
        </tbody>
      </table>
    </section>
  )
}
