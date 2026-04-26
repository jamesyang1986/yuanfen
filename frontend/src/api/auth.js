import http from './http'

export const registerByUsername = (username, password) =>
  http.post('/v1/auth/register/username', { username, password })

export const login = (username, password) =>
  http.post('/v1/auth/login', { loginType: 'USERNAME_PASSWORD', identifier: username, credential: password })

export const loginByPhone = (phone, code) =>
  http.post('/v1/auth/login', { loginType: 'PHONE_SMS', identifier: phone, credential: code })

export const sendSmsCode = (phone) =>
  http.post('/v1/sms/send', { phone })

export const logout = (refreshToken) =>
  http.post('/v1/auth/logout', { refreshToken })

export const refresh = (refreshToken) =>
  http.post('/v1/auth/refresh', { refreshToken })

export const getWechatAuthUrl = (state = '') =>
  http.get('/v1/auth/wechat/auth-url', { params: { state } })
