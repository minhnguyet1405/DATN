import { getToken } from '@/utils/auth'
import request from '@/utils/request'
import axios from 'axios'

export function login(data) {
  return request({
    url: '/vue-element-admin/user/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  token = getToken()
  return request({
    url: '/vue-element-admin/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/vue-element-admin/user/logout',
    method: 'post'
  })
}

export function checkInFromFace(image) {
  const config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }
  const formData = new FormData()
  formData.append('img', image, 'face.png')
  return axios.post(
    'http://173.249.3.91:8000/check',
    formData,
    config
  )
}

export function saveImg(image) {
  const config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }
  const formData = new FormData()
  formData.append('img', image)
  return axios.post(
    'http://localhost:8683/v1.0/upload/',
    formData,
    config
  )
}
