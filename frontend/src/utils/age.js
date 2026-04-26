/**
 * Calculate age from a "YYYY-MM" birth date string.
 * Returns null if birthDate is falsy.
 */
export function calcAge(birthDate) {
  if (!birthDate) return null
  return new Date().getFullYear() - parseInt(birthDate.substring(0, 4))
}
