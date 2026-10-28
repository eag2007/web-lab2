import { points } from './points.js';

const value_table = document.getElementsByTagName('tbody')[0];
const dateHeader = document.getElementById('date-sort');
const dateSortArrow = document.getElementById('date-sort-arrow');

let dateAscending = true;

dateHeader.addEventListener('click', () => {
  points.sort((a, b) => (a[4] - b[4]) * (dateAscending ? 1 : -1));
  dateSortArrow.textContent = dateAscending ? ' ↑' : ' ↓';
  dateAscending = !dateAscending;
  value_table.innerHTML = '';
  addToTable();
});

export const addPointToTable = (point) => {
  const [x, y, r, is_range, timestamp, executionMicros] = point;
  const time = new Date(timestamp).toLocaleString('ru-RU');

  value_table.innerHTML += `<tr>
                              <td>${x}</td>
                              <td>${y}</td>
                              <td>${r}</td>
                              <td>${is_range}</td>
                              <td>${time}</td>
                              <td>${executionMicros ?? '—'}</td>
                           </tr>`;
};

export const clearTable = () => {
  value_table.innerHTML = '';
};

export const addToTable = () => {
  points.forEach(addPointToTable);
};
