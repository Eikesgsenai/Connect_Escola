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
    // O evento agora é assíncrono (async) para usar o fetch/await
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
                // Envia os dados para o AuthController do Spring Boot
                body: JSON.stringify({ email, senha: password }),
            });

            if (response.ok) {
                const data = await response.json(); // Recebe o { token: "...", role: "..." }

                // --- LOGIN DE SUCESSO ---
                localStorage.setItem('authToken', data.token); // Salva o TOKEN JWT REAL
                localStorage.setItem('userRole', data.role); // Salva a ROLE REAL

                // Redireciona para a página principal do app
                window.location.href = '/app.html';

            } else {
                // --- LOGIN FALHOU (Ex: 401 Unauthorized) ---
                const errorMessage = await response.text();
                loginError.textContent = errorMessage || 'Erro de conexão ou credenciais inválidas.';
                loginError.style.display = 'block';
            }
        } catch (error) {
            // Erro de rede (servidor fora do ar)
            loginError.textContent = 'Não foi possível conectar ao servidor. Tente novamente.';
            loginError.style.display = 'block';
        }
    });
});

// TODO: Implementar o login com Google
function handleGoogleLogin() {
    alert('Login com Google ainda não implementado!');
    // No futuro, isso vai redirecionar para:
    // window.location.href = '/oauth2/authorization/google';
}