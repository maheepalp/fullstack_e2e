import React from 'react'
import { useCallback, useEffect, useRef, useState } from 'react'
import ExpenseForm from './components/ExpenseForm.jsx'
import ExpenseList from './components/ExpenseList.jsx'
import ExpenseSearch from './components/ExpenseSearch.jsx'
import { expenseService } from './services/expenseService.js'

export default function App() {
  const [expenses, setExpenses] = useState([])
  const [search, setSearch] = useState('')
  const [editingExpense, setEditingExpense] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const requestId = useRef(0)

  const refreshExpenses = useCallback(async (query = search) => {
    const currentRequestId = ++requestId.current
    setLoading(true)
    setError('')
    try {
      const results = await expenseService.list(query)
      if (currentRequestId === requestId.current) setExpenses(results)
    } catch (requestError) {
      if (currentRequestId === requestId.current) {
        setExpenses([])
        setError(requestError.message)
      }
    } finally {
      if (currentRequestId === requestId.current) setLoading(false)
    }
  }, [search])

  useEffect(() => {
    const timeout = window.setTimeout(() => refreshExpenses(search), 250)
    return () => window.clearTimeout(timeout)
  }, [search, refreshExpenses])

  async function saveExpense(expense) {
    setSaving(true)
    setError('')
    setSuccess('')
    try {
      if (editingExpense) {
        await expenseService.update(editingExpense.id, expense)
        setSuccess('Expense updated successfully.')
        setEditingExpense(null)
      } else {
        await expenseService.create(expense)
        setSuccess('Expense saved successfully.')
      }
      await refreshExpenses()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  async function deleteExpense(expense) {
    if (!window.confirm(`Delete "${expense.description}"? This cannot be undone.`)) return
    setDeletingId(expense.id)
    setError('')
    setSuccess('')
    try {
      await expenseService.remove(expense.id)
      setSuccess('Expense deleted successfully.')
      if (editingExpense?.id === expense.id) setEditingExpense(null)
      await refreshExpenses()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <main className="app-shell">
      <header className="page-header">
        <div className="brand-mark" aria-hidden="true">$</div>
        <div>
          <p className="eyebrow">YOUR MONEY, IN FOCUS</p>
          <h1>Expense tracker</h1>
          <p className="subtitle">A clear view of where your money goes.</p>
        </div>
      </header>

      <ExpenseForm
        editingExpense={editingExpense}
        onSubmit={saveExpense}
        onCancel={() => setEditingExpense(null)}
        busy={saving}
      />

      <section className="panel expenses-panel">
        <div className="section-heading list-heading">
          <div>
            <p className="eyebrow">YOUR ACTIVITY</p>
            <h2>Expenses</h2>
          </div>
          <ExpenseSearch value={search} onChange={setSearch} count={expenses.length} />
        </div>

        {success && <p className="notice success-notice" role="status">{success}</p>}
        {error && <p className="notice error-notice" role="alert">{error}</p>}
        {loading
          ? <div className="loading-state" role="status">Loading expenses…</div>
          : error && expenses.length === 0
            ? <div className="loading-state">Unable to load expenses.</div>
          : <ExpenseList expenses={expenses} onEdit={setEditingExpense} onDelete={deleteExpense} deletingId={deletingId} />}
      </section>
      <footer>Personal Expense Tracker <span>·</span> Amounts in Canadian dollars</footer>
    </main>
  )
}
