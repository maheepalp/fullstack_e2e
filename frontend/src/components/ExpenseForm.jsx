import React from 'react'
import { useEffect, useState } from 'react'

export const categories = [
  'Groceries',
  'Utilities',
  'Transportation',
  'Dining',
  'Shopping',
  'Healthcare',
  'Other',
]

const today = () => new Date().toLocaleDateString('en-CA')
const emptyForm = () => ({ expenseDate: today(), category: '', description: '', amount: '' })

export default function ExpenseForm({ editingExpense, onSubmit, onCancel, busy }) {
  const [form, setForm] = useState(emptyForm)
  const [validationError, setValidationError] = useState('')

  useEffect(() => {
    setForm(editingExpense
      ? {
          expenseDate: editingExpense.expenseDate,
          category: editingExpense.category,
          description: editingExpense.description,
          amount: String(editingExpense.amount),
        }
      : emptyForm())
    setValidationError('')
  }, [editingExpense])

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function submit(event) {
    event.preventDefault()
    const amount = Number(form.amount)
    if (!form.expenseDate || !form.category || !form.description.trim()) {
      setValidationError('Date, category, and description are required.')
      return
    }
    if (form.description.trim().length > 200) {
      setValidationError('Description must be at most 200 characters.')
      return
    }
    if (!Number.isFinite(amount) || amount <= 0 || !/^\d+(\.\d{1,2})?$/.test(form.amount)) {
      setValidationError('Amount must be greater than zero and have at most 2 decimal places.')
      return
    }
    setValidationError('')
    await onSubmit({ ...form, description: form.description.trim(), amount })
  }

  return (
    <section className="panel">
      <div className="section-heading">
        <div>
          <p className="eyebrow">{editingExpense ? 'UPDATE RECORD' : 'NEW RECORD'}</p>
          <h2>{editingExpense ? 'Edit expense' : 'Add an expense'}</h2>
        </div>
        {editingExpense && <button className="text-button" type="button" onClick={onCancel}>Cancel edit</button>}
      </div>
      <form className="expense-form" onSubmit={submit} noValidate>
        <label>
          Date
          <input name="expenseDate" type="date" value={form.expenseDate} onChange={updateField} required />
        </label>
        <label>
          Category
          <select name="category" value={form.category} onChange={updateField} required>
            <option value="">Select a category</option>
            {categories.map((category) => <option key={category}>{category}</option>)}
          </select>
        </label>
        <label className="description-field">
          Description
          <input name="description" type="text" maxLength="200" value={form.description} onChange={updateField} placeholder="What was it for?" required />
        </label>
        <label>
          Amount (CAD)
          <input name="amount" type="number" min="0.01" step="0.01" value={form.amount} onChange={updateField} placeholder="0.00" required />
        </label>
        {validationError && <p className="form-error" role="alert">{validationError}</p>}
        <div className="form-actions">
          <button className="primary-button" disabled={busy} type="submit">
            {busy ? 'Saving…' : editingExpense ? 'Update expense' : 'Save expense'}
          </button>
        </div>
      </form>
    </section>
  )
}
