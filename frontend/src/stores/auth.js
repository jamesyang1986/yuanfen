import { defineStore } from 'pinia'

const TOKEN_CACHE_MS = 20 * 60 * 1000 // 20 分钟

function loadToken() {
  const expiry = Number(localStorage.getItem('access_token_expiry') || 0)
  if (Date.now() > expiry) {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('access_token_expiry')
    return { accessToken: null, refreshToken: null, tokenExpiry: 0 }
  }
  return {
    accessToken: localStorage.getItem('access_token') || null,
    refreshToken: localStorage.getItem('refresh_token') || null,
    tokenExpiry: expiry
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => loadToken(),
  getters: {
    isLoggedIn: (state) => !!state.accessToken && Date.now() <= state.tokenExpiry
  },
  actions: {
    login(accessToken, refreshToken) {
      const expiry = Date.now() + TOKEN_CACHE_MS
      this.accessToken = accessToken
      this.refreshToken = refreshToken
      this.tokenExpiry = expiry
      localStorage.setItem('access_token', accessToken)
      localStorage.setItem('refresh_token', refreshToken)
      localStorage.setItem('access_token_expiry', String(expiry))
    },
    logout() {
      this.accessToken = null
      this.refreshToken = null
      this.tokenExpiry = 0
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('access_token_expiry')
    }
  }
})
