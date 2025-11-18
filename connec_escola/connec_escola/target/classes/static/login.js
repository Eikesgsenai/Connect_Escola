document.addEventListener('DOMContentLoaded', () => {

    // --- 1. Seleção de Elementos ---
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const showPasswordCheckbox = document.getElementById('show-password');
    const loginError = document.getElementById('login-error');

    // --- Lógica de Mostrar Senha ---
    showPasswordCheckbox.addEventListener('change', function() {
        passwordInput.type = this.checked ? 'text' : 'password';
    });

    // --- 2. Lógica de Login REAL (Fetch) ---
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        loginError.style.display = 'none'; // Esconde o erro

        const email = emailInput.value;
        const password = passwordInput.value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, senha: password }),
            });

            if (response.ok) {
                const data = await response.json();

                localStorage.setItem('authToken', data.token);
                localStorage.setItem('userRole', data.role);

                // --- CORREÇÃO DO "VOLTAR" ---
                // Voltamos a usar 'href' para que a página de login
                // permaneça no histórico do navegador.
                window.location.href = '/app.html';

            } else {
                const errorMessage = await response.text();
                loginError.textContent = errorMessage || 'Erro de conexão ou credenciais inválidas.';
                loginError.style.display = 'block';
            }
        } catch (error) {
            loginError.textContent = 'Não foi possível conectar ao servidor. Tente novamente.';
            loginError.style.display = 'block';
        }
    });
});

function handleGoogleLogin() {
    alert('Login com Google ainda não implementado!');
}