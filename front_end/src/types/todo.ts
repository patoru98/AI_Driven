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

export type TodoUpdateRequest = {
  p_key: number
  task: string
  registed_date: string
  limit_date: string
  memo: string
  done_flag?: boolean
}

export type TodoUpdateResponse = {
  code: number
  p_key?: number
  task?: string
  registed_date?: string
  limit_date?: string
  memo?: string
  done_flag?: boolean
  messages?: string[]
}

export type TodoDeleteRequest = {
  p_key: number
}

export type TodoDeleteResponse = {
  code: number
  messages?: string[]
}

export type TodoGetItem = {
  p_key: number
  done_flag: boolean
  task: string
  registed_date?: string
  limit_date?: string
  memo?: string
}

export type TodoGetResponse = {
  code: number
  todos?: TodoGetItem[]
  messages?: string[]
}
