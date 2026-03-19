async function loadAdminBooks() {
    const message = document.getElementById("message");
    const tableBody = document.getElementById("adminBooksTableBody");
    tableBody.innerHTML = "";
    message.textContent = "";

    try {
        const response = await fetch("/books", {
            method: "GET",
            headers: getAuthHeaders(),
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error("Could not load books.");
        }

        const books = await response.json();

        if (books.length === 0) {
            tableBody.innerHTML = "<tr><td colspan='5'>No books found.</td></tr>";
            return;
        }

        books.forEach(book => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${book.id ?? ""}</td>
                <td>${escapeHtml(book.title ?? "")}</td>
                <td>${escapeHtml(book.author ?? "")}</td>
                <td>${escapeHtml(book.category ?? "")}</td>
                <td>
                    <button type="button" class="edit-btn">Edit</button>
                    <button type="button" class="delete-btn">Delete</button>
                </td>
            `;

            row.querySelector(".edit-btn").addEventListener("click", () => {
                editBook(book.id, book.title, book.author, book.category);
            });

            row.querySelector(".delete-btn").addEventListener("click", () => {
                deleteBook(book.id);
            });

            tableBody.appendChild(row);
        });
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

async function loadBorrowRequests() {
    const tableBody = document.getElementById("borrowRequestsTableBody");
    tableBody.innerHTML = "";

    try {
        const response = await fetch("/borrow/all", {
            method: "GET",
            headers: getAuthHeaders(),
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error("Could not load borrow requests.");
        }

        const requests = await response.json();

        if (requests.length === 0) {
            tableBody.innerHTML = "<tr><td colspan='5'>No borrow requests found.</td></tr>";
            return;
        }

        requests.forEach(request => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${request.id ?? ""}</td>
                <td>${request.userId ?? ""}</td>
                <td>${request.bookId ?? ""}</td>
                <td>${escapeHtml(request.status ?? "")}</td>
                <td>
                    <button type="button" class="approve-btn">Approve</button>
                    <button type="button" class="reject-btn">Reject</button>
                </td>
            `;

            row.querySelector(".approve-btn").addEventListener("click", () => {
                approveRequest(request.id);
            });

            row.querySelector(".reject-btn").addEventListener("click", () => {
                rejectRequest(request.id);
            });

            tableBody.appendChild(row);
        });
    } catch (error) {
        tableBody.innerHTML = "<tr><td colspan='5'>Could not load requests.</td></tr>";
    }
}

function editBook(id, title, author, category) {
    document.getElementById("bookId").value = id;
    document.getElementById("title").value = title;
    document.getElementById("author").value = author;
    document.getElementById("category").value = category;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function saveBook(event) {
    event.preventDefault();

    const id = document.getElementById("bookId").value;
    const title = document.getElementById("title").value.trim();
    const author = document.getElementById("author").value.trim();
    const category = document.getElementById("category").value.trim();
    const message = document.getElementById("message");

    message.textContent = "";

    if (title.length < 2 || author.length < 2 || category.length < 2) {
        message.style.color = "#b91c1c";
        message.textContent = "All fields must be at least 2 characters.";
        return;
    }

    const url = id ? `/books/${id}` : "/books";
    const method = id ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method,
            headers: getAuthHeaders(),
            credentials: "same-origin",
            body: JSON.stringify({ title, author, category })
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(text || "Could not save book.");
        }

        message.style.color = "green";
        message.textContent = id ? "Book updated successfully." : "Book added successfully.";

        document.getElementById("bookForm").reset();
        document.getElementById("bookId").value = "";

        loadAdminBooks();
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

async function deleteBook(id) {
    const message = document.getElementById("message");

    if (!confirm("Are you sure you want to delete this book?")) {
        return;
    }

    try {
        const response = await fetch(`/books/${id}`, {
            method: "DELETE",
            headers: getAuthHeaders(),
            credentials: "same-origin"
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(text || "Could not delete book.");
        }

        message.style.color = "green";
        message.textContent = text || "Book deleted successfully.";

        loadAdminBooks();
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

async function approveRequest(id) {
    await updateRequestStatus(id, "approve");
}

async function rejectRequest(id) {
    await updateRequestStatus(id, "reject");
}

async function updateRequestStatus(id, action) {
    const message = document.getElementById("message");

    try {
        const response = await fetch(`/borrow/${id}/${action}`, {
            method: "PUT",
            headers: getAuthHeaders(),
            credentials: "same-origin"
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(text || `Could not ${action} request.`);
        }

        message.style.color = "green";
        message.textContent = text || `Request ${action}d successfully.`;

        loadBorrowRequests();
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

window.addEventListener("load", async () => {
    document.getElementById("bookForm").addEventListener("submit", saveBook);
    document.getElementById("logoutBtn").addEventListener("click", logout);

    const user = await protectPage("ADMIN");
    if (!user) {
        return;
    }

    loadAdminBooks();
    loadBorrowRequests();
});