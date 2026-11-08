import React from 'react'

export default function ExpenseSearch({ value, onChange, count }) {
  return (
    <div className="search-row">
      <label className="search-box">
        <span className="search-icon" aria-hidden="true">⌕</span>
        <input
          type="search"
          value={value}
          onChange={(event) => onChange(event.target.value)}
          placeholder="Search descriptions or categories"
          aria-label="Search expenses by description or category"
        />
      </label>
      <span className="record-count">{count} {count === 1 ? 'record' : 'records'}</span>
    </div>
  )
}
