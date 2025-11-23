let logLines = [];
let currentLine = 0;
let n = 0;
const tableBody = document.querySelector('#logTable tbody');
const nextBtn = document.getElementById('nextBtn');

// Define all the variable names (columns)
const columns = ['ANTin','ANTout','earliest','AVin','AVout','latest','POSTin','POSTout','Usedin','Usedout'];
// const columns = ['ANTin','ANTout','AVin','AVout','earliest','POSTin','POSTout','latest','Usedin','Usedout'];

// Keep a 2D array for the table data
let tableData = [];

async function loadProcessingOrders() {
    const response = await fetch("lcm_worklist_order_log.txt");
    const text = await response.text();

    const lines = text.split("\n").filter(line => line.trim().length > 0);

    const container = document.getElementById("pass-orders");
    container.innerHTML = ""; // clear any previous content

    lines.forEach((line, index) => {
        const div = document.createElement("div");
        div.innerHTML = `<strong>Pass ${index + 1}:</strong> ${line}`;
        container.appendChild(div);
    });
}

window.addEventListener("DOMContentLoaded", loadProcessingOrders);


// Initialize table rows after reading 'n'
function initializeTable(n) {
  tableBody.innerHTML = '';
  tableData = Array.from({length: n}, () => {
    const row = {};
    columns.forEach(col => row[col] = []);
    return row;
  });

  for (let i = 0; i < n; i++) {
    const tr = document.createElement('tr');
    tr.setAttribute('data-index', i);
    tr.innerHTML = `<td>${i}</td>` + columns.map(col => `<td id="${col}-${i}"></td>`).join('');
    tableBody.appendChild(tr);
  }
}

// Load the log file
fetch('lcm_worklist_log.txt')
  .then(res => res.text())
  .then(text => {
    logLines = text.split('\n').filter(line => line.trim() !== '');
    n = parseInt(logLines[0]); // first line is n
    initializeTable(n);
    // Remove the first line (n) from logLines
    logLines = logLines.slice(1);
  })
  .catch(err => console.error('Error loading log:', err));

// Update a cell in the table
function updateCell(variable, index, values) {
  tableData[index][variable] = values;

  const cell = document.getElementById(`${variable}-${index}`);
  if (!cell) return;

  cell.textContent = values.join(', ');
  cell.classList.add('highlight');
  setTimeout(() => cell.classList.remove('highlight'), 500);
}

// Process the next line
function processNextLine() {
  if (currentLine >= logLines.length) return;

  const line = logLines[currentLine].trim();
  currentLine++;

  // Skip empty lines
  if (!line) return;

  // Format: VAR,index,[values]
  const match = line.match(/^(\w+),(\d+),\[(.*)\]$/);
  if (match) {
    const [_, variable, idx, vals] = match;
    const index = parseInt(idx);
    const values = vals.split(',').map(v => v.trim()).filter(v => v.length > 0);
    updateCell(variable, index, values);
  }
}

nextBtn.addEventListener('click', processNextLine);
