import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(value) {
  return Cookies.set(TokenKey, value)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
