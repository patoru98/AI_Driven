export type Todo = {
  id: number
  task: string
  registedDate: string
  limitDate: string
  memo: string
  done: boolean
}

export type TodoInput = {
  task: string
  registedDate: string
  limitDate: string
  memo: string
}

export type TodoRegistRequest = {
  task: string
  registed_date: string
  limit_date: string
  memo: string
}

export type TodoRegistResponse = {
  code: number
  task?: string
  registed_date?: string
  limit_date?: string
  memo?: string
  messages?: string[]
}
