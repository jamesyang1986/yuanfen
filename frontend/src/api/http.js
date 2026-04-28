import axios from 'axios'
import router from '../router'

const http = axios.create({
  baseURL: '/api'
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
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
