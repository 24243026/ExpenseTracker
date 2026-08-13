const API = "/api/expenses";


// Show today's date

document.getElementById("date").innerText =
    new Date().toLocaleDateString(
        "en-IN",
        {
            day: "numeric",
            month: "short",
            year: "numeric"
        }
    );


// Add Expense

document
    .getElementById("expenseForm")
    .addEventListener("submit", async function (event) {

        event.preventDefault();

        const name =
            document.getElementById("name").value;

        const amount =
            document.getElementById("amount").value;

        const category =
            document.getElementById("category").value;


        await fetch(API, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name: name,
                amount: amount,
                category: category
            })

        });


        document
            .getElementById("expenseForm")
            .reset();


        loadExpenses();

    });


// Load Expenses

async function loadExpenses() {

    const response = await fetch(API);

    const expenses = await response.json();

    const table =
        document.getElementById("expenseTable");

    table.innerHTML = "";

    let total = 0;


    expenses.forEach(expense => {

        total += Number(expense.amount);


        const row =
            document.createElement("tr");


        row.innerHTML = `

            <td>
                <strong>${expense.name}</strong>
            </td>

            <td>
                ${expense.category}
            </td>

            <td>
                ₹${Number(expense.amount).toFixed(2)}
            </td>

            <td>

                <button
                    class="delete"
                    onclick="deleteExpense(${expense.id})">

                    🗑 Delete

                </button>

            </td>

        `;


        table.appendChild(row);

    });


    document.getElementById("total").innerText =
        total.toFixed(2);


    document.getElementById("count").innerText =
        expenses.length;


    const average =
        expenses.length > 0
            ? total / expenses.length
            : 0;


    document.getElementById("average").innerText =
        average.toFixed(2);


    document.getElementById("empty").style.display =
        expenses.length === 0
            ? "block"
            : "none";

}


// Delete Expense

async function deleteExpense(id) {

    await fetch(
        `${API}?id=${id}`,
        {
            method: "DELETE"
        }
    );

    loadExpenses();

}


// Load data when page opens

loadExpenses();