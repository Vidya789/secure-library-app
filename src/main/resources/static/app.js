async function parseResponseError(response, fallbackMessage) {
    const contentType = response.headers.get("content-type") || "";

    try {
        if (contentType.includes("application/json")) {
            const data = await response.json();
            if (typeof data === "string") {
                return data;
            }
            if (data.error) {
                return data.error;
            }
            const fieldErrors = Object.values(data || {}).filter(Boolean);
            if (fieldErrors.length > 0) {
                return fieldErrors.join(" ");
            }
        } else {
            const text = await response.text();
            if (text && text.trim()) {
                return text;
            }
        }
    } catch (_) {
        // ignore parse errors and fall back
    }

    return fallbackMessage;
}

function getAuthHeaders() {
    return {
        "Content-Type": "application/json"
    };
}

async function getCurrentUser() {
    const response = await fetch("/auth/me", {
        method: "GET",
        credentials: "same-origin"
    });

    if (!response.ok) {
        throw new Error("Not authenticated");
    }

    return response.json();
}

function showMessage(messageElement, text, color = "#b91c1c") {
    if (!messageElement) {
        return;
    }
    messageElement.style.color = color;
    messageElement.textContent = text;
}

async function initializeLoginPage() {
    const message = document.getElementById("message");
    if (!message) {
        return;
    }

    const params = new URLSearchParams(window.location.search);
    if (params.has("error")) {
        showMessage(message, "Invalid username or password.");
    } else if (params.has("logout")) {
        showMessage(message, "Logged out successfully.", "green");
    }
}

async function loginUser(event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    showMessage(message, "", "#111827");

    if (!username || !password) {
        showMessage(message, "Please enter username and password.");
        return;
    }

    try {
        const formBody = new URLSearchParams();
        formBody.append("username", username);
        formBody.append("password", password);

        await fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formBody.toString(),
            credentials: "same-origin"
        });

        const meResponse = await fetch("/auth/me", {
            method: "GET",
            credentials: "same-origin"
        });

        if (!meResponse.ok) {
            window.location.href = "login.html?error";
            return;
        }

        window.location.href = "index.html";
    } catch (error) {
        showMessage(message, "Login failed.");
    }
}

async function registerUser(event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    showMessage(message, "", "#111827");

    if (username.length < 3) {
        showMessage(message, "Username must be at least 3 characters.");
        return;
    }

    if (password.length < 6) {
        showMessage(message, "Password must be at least 6 characters.");
        return;
    }

    try {
        const response = await fetch("/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username,
                password,
                role: "USER"
            }),
            credentials: "same-origin"
        });

        if (!response.ok) {
            const errorMessage = await parseResponseError(response, "Registration failed.");
            throw new Error(errorMessage);
        }

        const successText = await response.text();
        showMessage(message, successText || "Registration successful.", "green");

        setTimeout(() => {
            window.location.href = "login.html";
        }, 900);
    } catch (error) {
        showMessage(message, error.message || "Registration failed.");
    }
}

async function logout() {
    try {
        await fetch("/logout", {
            method: "POST",
            credentials: "same-origin"
        });
    } finally {
        window.location.href = "login.html?logout";
    }
}

async function protectPage(requiredRole) {
    try {
        const user = await getCurrentUser();
        if (requiredRole && user.role !== requiredRole) {
            window.location.href = "index.html";
            return null;
        }
        return user;
    } catch (_) {
        window.location.href = "login.html";
        return null;
    }
}

async function renderHomeMenu() {
    const homeMenu = document.getElementById("homeMenu");
    const welcomeMessage = document.getElementById("welcomeMessage");

    if (!homeMenu) {
        return;
    }

    homeMenu.innerHTML = "";

    try {
        const user = await getCurrentUser();
        if (welcomeMessage) {
            welcomeMessage.textContent = `Welcome, ${user.username}.`;
        }

        if (user.role === "USER") {
            homeMenu.innerHTML = `
                <a class="btn" href="books.html">Books</a>
                <button class="btn" onclick="logout()">Logout</button>
            `;
        } else if (user.role === "ADMIN") {
            homeMenu.innerHTML = `
                <a class="btn" href="admin.html">Admin Dashboard</a>
                <button class="btn" onclick="logout()">Logout</button>
            `;
        } else {
            homeMenu.innerHTML = `<button class="btn" onclick="logout()">Logout</button>`;
        }
    } catch (_) {
        window.location.href = "login.html";
    }
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
