import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem('access_token') || null,
    refreshToken: localStorage.getItem('refresh_token') || null
  }),
  getters: {
    isLoggedIn: (state) => !!state.accessToken
  },
  actions: {
    login(accessToken, refreshToken) {
      this.accessToken = accessToken
      this.refreshToken = refreshToken
      localStorage.setItem('access_token', accessToken)
      localStorage.setItem('refresh_token', refreshToken)
    },
    logout() {
      this.accessToken = null
      this.refreshToken = null
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
    }
  }
})
