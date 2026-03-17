function getStoredUsername() {
    return sessionStorage.getItem("username");
}

function getStoredPassword() {
    return sessionStorage.getItem("password");
}

function getAuthHeaders() {
    const username = getStoredUsername();
    const password = getStoredPassword();

    const headers = {
        "Content-Type": "application/json"
    };

    if (username && password) {
        headers["Authorization"] = "Basic " + btoa(username + ":" + password);
    }

    return headers;
}

async function loginUser(event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    message.textContent = "";

    try {
        const response = await fetch("/books", {
            method: "GET",
            headers: {
                "Authorization": "Basic " + btoa(username + ":" + password)
            }
        });

        if (!response.ok) {
            throw new Error("Invalid username or password.");
        }

        sessionStorage.setItem("username", username);
        sessionStorage.setItem("password", password);

        message.style.color = "green";
        message.textContent = "Login successful.";

        setTimeout(() => {
            window.location.href = "books.html";
        }, 700);
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

async function registerUser(event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    message.textContent = "";

    if (username.length < 3) {
        message.style.color = "#b91c1c";
        message.textContent = "Username must be at least 3 characters.";
        return;
    }

    if (password.length < 6) {
        message.style.color = "#b91c1c";
        message.textContent = "Password must be at least 6 characters.";
        return;
    }

    try {
        const response = await fetch("/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password,
                role: "USER"
            })
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(text || "Registration failed.");
        }

        message.style.color = "green";
        message.textContent = text || "Registration successful.";

        setTimeout(() => {
            window.location.href = "login.html";
        }, 900);
    } catch (error) {
        message.style.color = "#b91c1c";
        message.textContent = error.message;
    }
}

function logout() {
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("password");
    window.location.href = "login.html";
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function jsEscape(value) {
    return String(value ?? "")
        .replaceAll("\\", "\\\\")
        .replaceAll("'", "\\'")
        .replaceAll('"', '\\"');
}