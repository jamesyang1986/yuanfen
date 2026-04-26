import axios from 'axios'
import router from '../router'

const http = axios.create({
  baseURL: '/api'
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('access_token')
  const expiry = Number(localStorage.getItem('access_token_expiry') || 0)
  if (token && Date.now() <= expiry) {
    config.headers.Authorization = `Bearer ${token}`
  } else if (token) {
    // 缓存已过期，清除并跳转登录
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('access_token_expiry')
    router.push('/login')
    return Promise.reject(new Error('TOKEN_EXPIRED'))
  }
  return config
})

http.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('access_token_expiry')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default http
