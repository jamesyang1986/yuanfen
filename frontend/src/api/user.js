import http from './http'

export const getProfile = () =>
  http.get('/v1/users/profile')

export const updateProfile = (fields) =>
  http.put('/v1/users/profile', fields)

export const uploadAvatar = (file) => {
  const form = new FormData()
  form.append('file', file)
  return http.post('/v1/users/avatar', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const listUsers = (page = 0, size = 20) =>
  http.get('/v1/users', { params: { page, size } })

export const getUserById = (id) =>
  http.get(`/v1/users/${id}`)
