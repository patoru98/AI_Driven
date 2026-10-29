import type {
  Todo,
  TodoInput,
  TodoGetResponse,
  TodoRegistRequest,
  TodoRegistResponse,
  TodoUpdateRequest,
  TodoUpdateResponse,
} from '../types/todo'
import { isoToSlash, slashToIso } from '../utils/date'

export class ApiError extends Error {
  readonly messages: string[]

  constructor(messages: string[]) {
    super(messages.join('\n'))
    this.name = 'ApiError'
    this.messages = messages
  }
}

const toTodoInput = (todo: Partial<Todo>): TodoInput => ({
  task: todo.task ?? '',
  registedDate: todo.registedDate ?? '',
  limitDate: todo.limitDate ?? '',
  memo: todo.memo ?? '',
})

const createUpdateRequest = (id: number, input: TodoInput, doneFlag: boolean): TodoUpdateRequest => ({
  p_key: id,
  task: input.task,
  registed_date: isoToSlash(input.registedDate),
  limit_date: isoToSlash(input.limitDate),
  memo: input.memo,
  done_flag: doneFlag,
})

// ---- TODO登録API (back_end/src/docs/api/TodoRegistAPI.md) ----

export const registTodo = async (input: TodoInput): Promise<void> => {
  const body: TodoRegistRequest = {
    task: input.task,
    registed_date: isoToSlash(input.registedDate),
    limit_date: isoToSlash(input.limitDate),
    memo: input.memo,
  }

  let res: Response
  try {
    res = await fetch('/api/todo/regist', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
  } catch {
    throw new ApiError(['サーバーに接続できませんでした'])
  }

  const json = (await res.json().catch(() => null)) as TodoRegistResponse | null
  if (!res.ok || json === null || json.code !== 200) {
    throw new ApiError(json?.messages ?? ['TODOの登録に失敗しました'])
  }
}

// ---- 更新・削除・完了APIは未実装のためモック ----

const MOCK_DELAY_MS = 200
const delay = () => new Promise((r) => setTimeout(r, MOCK_DELAY_MS))

const createMockStore = () => {
  let todos: Todo[] = []

  return {
    replaceAll: (items: Todo[]): void => {
      todos = items.map((todo) => ({ ...todo }))
    },
    get: (id: number): Todo | undefined => todos.find((t) => t.id === id),
    update: (id: number, patch: Partial<Todo>): Todo => {
      const index = todos.findIndex((t) => t.id === id)
      if (index < 0) throw new ApiError(['対象のTODOが見つかりません'])
      const updated = { ...todos[index], ...patch, id }
      todos = todos.map((t) => (t.id === id ? updated : t))
      return { ...updated }
    },
    remove: (id: number): void => {
      todos = todos.filter((t) => t.id !== id)
    },
  }
}

const mockStore = createMockStore()

/** TODO取得API */
export const fetchTodos = async (): Promise<Todo[]> => {
  let res: Response
  try {
    res = await fetch('/api/todo/getAll')
  } catch {
    throw new ApiError(['サーバーに接続できませんでした'])
  }

  const json = (await res.json().catch(() => null)) as TodoGetResponse | null
  if (!res.ok || json === null || json.code !== 200 || !Array.isArray(json.todos)) {
    throw new ApiError(json?.messages ?? ['TODOの取得に失敗しました'])
  }

  const todos = json.todos.map((item) => ({
    id: item.p_key,
    done: item.done_flag,
    task: item.task,
    registedDate: item.registed_date ? slashToIso(item.registed_date) : '',
    limitDate: item.limit_date ? slashToIso(item.limit_date) : '',
    memo: item.memo ?? '',
  }))
  mockStore.replaceAll(todos)
  return todos
}

/** TODO更新API */
export const updateTodo = async (id: number, input: TodoInput): Promise<Todo> => {
  const current = mockStore.get(id)
  const body: TodoUpdateRequest = createUpdateRequest(id, input, current?.done ?? false)

  let res: Response
  try {
    res = await fetch('/api/todo/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
  } catch {
    throw new ApiError(['サーバーに接続できませんでした'])
  }

  const json = (await res.json().catch(() => null)) as TodoUpdateResponse | null
  if (!res.ok || json === null || json.code !== 200) {
    throw new ApiError(json?.messages ?? ['TODOの更新に失敗しました'])
  }

  return {
    id: json.p_key ?? id,
    task: json.task ?? input.task,
    registedDate: json.registed_date ? slashToIso(json.registed_date) : input.registedDate,
    limitDate: json.limit_date ? slashToIso(json.limit_date) : input.limitDate,
    memo: json.memo ?? input.memo,
    done: json.done_flag ?? current?.done ?? false,
  }
}

/** TODO削除API (モック) */
export const deleteTodo = async (id: number): Promise<void> => {
  await delay()
  mockStore.remove(id)
}

/** TODO完了API */
export const completeTodo = async (id: number, done: boolean): Promise<Todo> => {
  const current = mockStore.get(id)
  if (!current) {
    throw new ApiError(['対象のTODOが見つかりません'])
  }

  const body = createUpdateRequest(id, toTodoInput(current), done)

  let res: Response
  try {
    res = await fetch('/api/todo/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
  } catch {
    throw new ApiError(['サーバーに接続できませんでした'])
  }

  const json = (await res.json().catch(() => null)) as TodoUpdateResponse | null
  if (!res.ok || json === null || json.code !== 200) {
    throw new ApiError(json?.messages ?? ['TODOの完了処理に失敗しました'])
  }

  const updated: Todo = {
    id,
    task: current.task,
    registedDate: current.registedDate,
    limitDate: current.limitDate,
    memo: current.memo,
    done,
  }

  mockStore.update(id, { done })
  return updated
}
