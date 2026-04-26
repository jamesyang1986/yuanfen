import http from './http'

export const registerByUsername = (username, password) =>
  http.post('/v1/auth/register/username', { username, password })

export const login = (username, password) =>
  http.post('/v1/auth/login', { loginType: 'USERNAME_PASSWORD', username, password })

export const logout = (refreshToken) =>
  http.post('/v1/auth/logout', { refreshToken })

export const refresh = (refreshToken) =>
  http.post('/v1/auth/refresh', { refreshToken })
