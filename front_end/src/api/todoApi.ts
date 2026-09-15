import type {
  Todo,
  TodoInput,
  TodoRegistRequest,
  TodoRegistResponse,
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

// ---- TODO登録API (back_end/src/docs/api/TodoRegistAPI.md) ----

export const registTodo = async (input: TodoInput): Promise<Todo> => {
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

  return mockStore.add({
    task: json.task ?? input.task,
    registedDate: slashToIso(json.registed_date ?? body.registed_date),
    limitDate: slashToIso(json.limit_date ?? body.limit_date),
    memo: json.memo ?? input.memo,
  })
}

// ---- 以下、API設計書が未作成のためモック実装 ----

const MOCK_DELAY_MS = 200
const delay = () => new Promise((r) => setTimeout(r, MOCK_DELAY_MS))

const createMockStore = () => {
  let seq = 0
  let todos: Todo[] = []

  return {
    all: (): Todo[] => todos.map((t) => ({ ...t })),
    add: (input: TodoInput): Todo => {
      const todo: Todo = { id: ++seq, done: false, ...input }
      todos = [...todos, todo]
      return { ...todo }
    },
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

/** TODO取得API (モック) */
export const fetchTodos = async (): Promise<Todo[]> => {
  await delay()
  return mockStore.all()
}

/** TODO更新API (モック) */
export const updateTodo = async (id: number, input: TodoInput): Promise<Todo> => {
  await delay()
  return mockStore.update(id, input)
}

/** TODO削除API (モック) */
export const deleteTodo = async (id: number): Promise<void> => {
  await delay()
  mockStore.remove(id)
}

/** TODO完了API (モック) */
export const completeTodo = async (id: number, done: boolean): Promise<Todo> => {
  await delay()
  return mockStore.update(id, { done })
}
