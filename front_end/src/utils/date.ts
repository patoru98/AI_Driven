const pad = (n: number) => String(n).padStart(2, '0')

/** YYYY-MM-DD (input[type=date] 形式) */
export const todayIso = (): string => {
  const d = new Date()
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

/** YYYY-MM-DD -> YYYY/MM/DD (API 形式) */
export const isoToSlash = (iso: string): string => iso.replaceAll('-', '/')

/** YYYY/MM/DD -> YYYY-MM-DD */
export const slashToIso = (slash: string): string => slash.replaceAll('/', '-')
