import React from 'react'

const currency = new Intl.NumberFormat('en-CA', { style: 'currency', currency: 'CAD' })
const date = new Intl.DateTimeFormat('en-CA', { year: 'numeric', month: 'short', day: 'numeric', timeZone: 'UTC' })

export default function ExpenseList({ expenses, onEdit, onDelete, deletingId }) {
  if (!expenses.length) {
    return <div className="empty-state"><span aria-hidden="true">◌</span><p>No expenses found.</p><small>Add an expense or try another search.</small></div>
  }

  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr><th>Date</th><th>Category</th><th>Description</th><th className="amount-cell">Amount</th><th><span className="sr-only">Actions</span></th></tr>
        </thead>
        <tbody>
          {expenses.map((expense) => (
            <tr key={expense.id}>
              <td className="date-cell">{date.format(new Date(`${expense.expenseDate}T00:00:00Z`))}</td>
              <td><span className="category-pill">{expense.category}</span></td>
              <td className="description-cell">{expense.description}</td>
              <td className="amount-cell">{currency.format(expense.amount)}</td>
              <td>
                <div className="row-actions">
                  <button className="text-button" type="button" onClick={() => onEdit(expense)}>Edit</button>
                  <button className="delete-button" type="button" disabled={deletingId === expense.id} onClick={() => onDelete(expense)}>
                    {deletingId === expense.id ? 'Deleting…' : 'Delete'}
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
