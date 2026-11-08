const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const error = await response.json()
      if (error.fieldErrors) {
        message = Object.values(error.fieldErrors).join('. ')
      } else if (error.message) {
        message = error.message
      }
    } catch {
      // Keep the status-based message when the server does not return JSON.
    }
    throw new Error(message)
  }

  return response.status === 204 ? null : response.json()
}

export const expenseService = {
  list(search = '') {
    const query = search.trim() ? `?search=${encodeURIComponent(search.trim())}` : ''
    return request(`/expenses${query}`)
  },
  create(expense) {
    return request('/expenses', { method: 'POST', body: JSON.stringify(expense) })
  },
  update(id, expense) {
    return request(`/expenses/${id}`, { method: 'PUT', body: JSON.stringify(expense) })
  },
  remove(id) {
    return request(`/expenses/${id}`, { method: 'DELETE' })
  },
}
