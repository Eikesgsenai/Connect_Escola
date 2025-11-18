// --- 0. VERIFICAÇÃO DE AUTENTICAÇÃO ---
// Isso pode rodar primeiro, pois só checa o localStorage.
(function() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        alert('Você precisa estar logado para ver esta página.');
        window.location.href = '/index.html';
    }
})();

// --- 1. DECLARAÇÃO DE VARIÁVEIS GLOBAIS ---
// Apenas declaramos as variáveis aqui. Elas serão preenchidas
// quando o DOM carregar (no final do arquivo).
let publicView, adminEventView, adminUserView, detailsView,
    publicEventList, adminEventList, eventForm, userList,
    adminButtonsContainer, adminEventBtn, adminUserBtn, createUserForm;

// --- 2. Armazenamento de Dados ---
let events = [];
let rsvpEvents = [];
function saveRsvp() {
    localStorage.setItem('rsvpEvents', JSON.stringify(rsvpEvents));
}


// --- 3. Funções de Controle de Visualização (COM HISTORY API) ---
// (Estas funções agora funcionam porque as variáveis acima são globais)
function showPublicView(pushState = true) {
    publicView.style.display = 'block';
    adminEventView.style.display = 'none';
    adminUserView.style.display = 'none';
    detailsView.style.display = 'none';
    renderPublicEvents();
    if (pushState) {
        history.pushState({ view: 'public' }, 'Eventos', '#eventos');
    }
}

function showAdminEventView(pushState = true) {
    publicView.style.display = 'none';
    adminEventView.style.display = 'block';
    adminUserView.style.display = 'none';
    detailsView.style.display = 'none';
    renderAdminEvents();
    if (pushState) {
        history.pushState({ view: 'admin-eventos' }, 'Gestão de Eventos', '#admin-eventos');
    }
}

function showAdminUserView(pushState = true) {
    publicView.style.display = 'none';
    adminEventView.style.display = 'none';
    adminUserView.style.display = 'block';
    detailsView.style.display = 'none';
    fetchAndRenderUsers();
    if (pushState) {
        history.pushState({ view: 'admin-usuarios' }, 'Gestão de Usuários', '#admin-usuarios');
    }
}

function showDetailsView(eventId, pushState = true) {
    publicView.style.display = 'none';
    adminEventView.style.display = 'none';
    adminUserView.style.display = 'none';
    detailsView.style.display = 'block';
    const event = events.find(e => e.id === eventId);
    if (!event) return;
    document.getElementById('details-title').textContent = event.title;
    let mapButtonHTML = event.mapLink ? `<p><a href="${event.mapLink}" target="_blank" class="button button-primary">Ver Localiza\u00E7\u00E3o no Mapa</a></p>` : '';
    document.getElementById('details-content').innerHTML = `<p><strong>Data:</strong> ${new Date(event.date).toLocaleDateString('pt-BR', {timeZone: 'UTC'})}</p><p><strong>Hora:</strong> ${event.time}</p><p><strong>Local:</strong> ${event.local}</p><p>${event.description}</p>${mapButtonHTML}`;
    document.getElementById('attendee-count').textContent = `Confirmados: ${event.attendeeCount}`;
    document.getElementById('rsvp-button').onclick = () => handleRsvp(eventId);
    if (pushState) {
        history.pushState({ view: 'details', eventId: eventId }, event.title, `#evento/${eventId}`);
    }
}

// --- 4. Funções de Renderização ---

async function fetchAndRenderUsers() {
    const token = localStorage.getItem('authToken');
    try {
        const response = await fetch('/api/admin/users', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.status === 403) {
            alert('Você não tem permissão de Gerente (ADMIN) para ver esta lista.');
            userList.innerHTML = '<p>Acesso negado.</p>';
            return;
        }
        if (!response.ok) { throw new Error('Falha ao buscar usuários.'); }
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Erro ao buscar usuários:', error);
        userList.innerHTML = '<p>Erro ao carregar usuários.</p>';
    }
}

function renderUserList(users) {
    userList.innerHTML = '';
    if (users.length === 0) {
        userList.innerHTML = '<p>Nenhum usuário cadastrado.</p>';
        return;
    }
    const admins = users.filter(u => u.role === 'ADMIN' || u.role === 'PROFESSOR');
    const alunos = users.filter(u => u.role === 'ALUNO');
    userList.innerHTML += '<h3>Administradores e Professores</h3>';
    if (admins.length > 0) {
        admins.forEach(user => {
            const listItem = document.createElement('li');
            listItem.className = 'user-card';
            listItem.innerHTML = `
                <div class="user-info">
                    <strong>${user.nome}</strong> <span>(${user.role})</span>
                    <p>${user.email}</p>
                </div>
                <div class="user-buttons">
                    <button class="button button-primary" onclick="handleEditUserRole(${user.id})">Editar Role</button>
                </div>
            `;
            userList.appendChild(listItem);
        });
    } else { userList.innerHTML += '<p>Nenhum admin ou professor encontrado.</p>'; }
    userList.innerHTML += '<hr style="margin: 20px 0;"><h3>Alunos</h3>';
    if (alunos.length > 0) {
        alunos.forEach(user => {
            const listItem = document.createElement('li');
            listItem.className = 'user-card';
            listItem.innerHTML = `
                <div class="user-info">
                    <strong>${user.nome}</strong> <span>(${user.role})</span>
                    <p>${user.email}</p>
                </div>
                <div class="user-buttons">
                    <button class="button button-primary" onclick="handleEditUserRole(${user.id})">Editar Role</button>
                </div>
            `;
            userList.appendChild(listItem);
        });
     } else { userList.innerHTML += '<p>Nenhum aluno encontrado.</p>'; }
}


function renderPublicEvents() {
    publicEventList.innerHTML = '';
    const sortedEvents = [...events].sort((a, b) => new Date(a.date) - new Date(b.date));
    if (sortedEvents.length === 0) { publicEventList.innerHTML = "<p>Nenhum evento agendado no momento.</p>"; return; }
    const template = document.getElementById('public-event-template');
    sortedEvents.forEach(event => {
        const clone = template.content.cloneNode(true);
        const listItem = clone.querySelector('li');
        listItem.onclick = () => showDetailsView(event.id);
        const shareButton = clone.querySelector('.event-share');
        shareButton.onclick = (e) => { e.stopPropagation(); handleShare(event.id); };
        const eventDate = new Date(event.date);
        const month = eventDate.toLocaleDateString('pt-BR', { month: 'short', timeZone: 'UTC' }).replace('.', '').toUpperCase();
        const day = eventDate.toLocaleDateString('pt-BR', { day: '2-digit', timeZone: 'UTC' });
        clone.querySelector('h3').textContent = event.title;
        clone.querySelector('.event-date-month').textContent = month;
        clone.querySelector('.event-date-day').textContent = day;
        clone.querySelector('.event-time').textContent = event.time;
        clone.querySelector('.event-local').textContent = event.local;
        publicEventList.appendChild(clone);
    });
}
function renderAdminEvents() {
    adminEventList.innerHTML = '';
    const sortedEvents = [...events].sort((a, b) => new Date(a.date) - new Date(b.date));
    if (sortedEvents.length === 0) { adminEventList.innerHTML = '<p>Nenhum evento cadastrado.</p>'; return; }
    sortedEvents.forEach(event => {
        const listItem = document.createElement('li');
        listItem.className = 'event-card';
        listItem.innerHTML = `<div class="event-info"><h3>${event.title}</h3><p><strong>Data:</strong> ${new Date(event.date).toLocaleDateString('pt-BR', {timeZone: 'UTC'})} | <strong>Local:</strong> ${event.local}</p></div><div class="event-buttons"><button class="button button-success" onclick="handleEdit(${event.id})">Editar</button><button class="button button-danger" onclick="handleDelete(${event.id})">Excluir</button></div>`;
        adminEventList.appendChild(listItem);
    });
}

// --- 5. Lógica de Autenticação ---
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    window.location.href = '/index.html';
}

// --- 6. Funções CRUD e RSVP (ATUALIZADAS) ---

const getAuthHeaders = () => {
    const token = localStorage.getItem('authToken');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
};

async function handleEventFormSubmit(e) {
    e.preventDefault();
    const eventId = document.getElementById('event-id').value;
    const data = document.getElementById('event-date').value;
    const horaInicio = document.getElementById('event-time').value;
    const horaFim = document.getElementById('event-time-fim').value;
    const dataInicio = `${data}T${horaInicio}`;
    const dataFim = `${data}T${horaFim}`;

    const eventData = {
        title: document.getElementById('event-title').value,
        dataInicio: dataInicio,
        dataFim: dataFim,
        localNome: document.getElementById('event-local').value,
        mapLink: document.getElementById('event-map-link').value,
        description: document.getElementById('event-description').value
    };

    const isUpdating = eventId !== '';
    const url = isUpdating ? `/api/eventos/${eventId}` : '/api/eventos';
    const method = isUpdating ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            body: JSON.stringify(eventData)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(`Erro ao salvar evento: ${error}`);
        }

        alert(`Evento ${isUpdating ? 'atualizado' : 'criado'} com sucesso!`);
        resetForm();
        await loadInitialEvents();
        renderAdminEvents();

    } catch (error) {
        console.error("Erro ao salvar evento:", error);
        alert(error.message);
    }
}

async function handleCreateUserSubmit(e) {
    e.preventDefault();

    const userData = {
        nome: document.getElementById('user-nome').value,
        email: document.getElementById('user-email').value,
        senha: document.getElementById('user-senha').value,
        role: document.getElementById('user-role').value
    };

    try {
        const response = await fetch('/api/admin/users', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(`Erro ao criar usuário: ${error}`);
        }

        alert("Usuário criado com sucesso!");
        createUserForm.reset();
        fetchAndRenderUsers();

    } catch (error) {
        console.error("Erro ao criar usuário:", error);
        alert(error.message);
    }
}


function handleEdit(id) {
    const eventToEdit = events.find(event => event.id === id);
    if (!eventToEdit) return;
    document.getElementById('event-id').value = eventToEdit.id;
    document.getElementById('event-title').value = eventToEdit.title;
    document.getElementById('event-date').value = eventToEdit.date;
    document.getElementById('event-time').value = eventToEdit.time;
    document.getElementById('event-time-fim').value = eventToEdit.timeFim;
    document.getElementById('event-local').value = eventToEdit.local;
    document.getElementById('event-map-link').value = eventToEdit.mapLink || '';
    document.getElementById('event-description').value = eventToEdit.description;
    window.scrollTo(0, 0);
}

async function handleDelete(id) {
    if (confirm('Tem certeza que deseja excluir este evento?')) {
        try {
            const response = await fetch(`/api/eventos/${id}`, {
                method: 'DELETE',
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                const error = await response.text();
                throw new Error(`Erro ao deletar evento: ${error}`);
            }
            alert("Evento deletado com sucesso!");
            await loadInitialEvents();
            renderAdminEvents();

        } catch (error) {
            console.error("Erro ao deletar evento:", error);
            alert(error.message);
        }
    }
}

function resetForm() {
    eventForm.reset();
    document.getElementById('event-id').value = '';
}

async function handleRsvp(eventId) {
    if (rsvpEvents.includes(eventId)) {
        alert('Você já confirmou presença neste evento.');
        return;
    }

    const token = localStorage.getItem('authToken');
    try {
        const response = await fetch(`/api/eventos/${eventId}/rsvp`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) { throw new Error('Falha ao confirmar presença.'); }

        const updatedEvent = await response.json();

        const eventIndex = events.findIndex(e => e.id === eventId);
        if (eventIndex !== -1) {
            events[eventIndex].attendeeCount = updatedEvent.attendeeCount;
        }

        document.getElementById('attendee-count').textContent = `Confirmados: ${updatedEvent.attendeeCount}`;

        rsvpEvents.push(eventId);
        saveRsvp();

        alert('Presença confirmada com sucesso!');

    } catch (error) {
        console.error("Erro ao confirmar presença:", error);
        alert("Erro ao confirmar presença. Tente novamente.");
    }
}

function handleShare(eventId) {
    const event = events.find(e => e.id === eventId);
    if (!event) return;
    const shareText = `Ol\u00E1, voc\u00EA est\u00E1 convidado(a) para o evento ${event.title} \u00E0s ${event.time} no ${event.local}`;
    if (navigator.share) {
        navigator.share({ title: event.title, text: shareText }).catch(console.error);
    } else {
        navigator.clipboard.writeText(shareText).then(() => {
            alert('Mensagem do evento copiada para a área de transferência!');
        });
    }
}

async function handleEditUserRole(userId) {
    const novaRoleInput = prompt("Digite a nova ROLE (ALUNO, PROFESSOR, ADMIN):");
    if (!novaRoleInput) { alert("Operação cancelada."); return; }
    const novaRole = novaRoleInput.toUpperCase();
    if (novaRole !== 'ALUNO' && novaRole !== 'PROFESSOR' && novaRole !== 'ADMIN') {
        alert("Role inválida."); return;
    }
    const token = localStorage.getItem('authToken');
    try {
        const response = await fetch(`/api/admin/users/${userId}/role`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ role: novaRole })
        });
        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || "Falha ao atualizar a role.");
        }
        alert("Role atualizada com sucesso!");
        fetchAndRenderUsers();
    } catch (error) {
        console.error('Erro ao atualizar a role:', error);
        alert(`Erro: ${error.message}`);
    }
}


// --- 7. Inicialização da Aplicação ---

async function loadInitialEvents() {
    const token = localStorage.getItem('authToken');
    try {
        const response = await fetch('/api/eventos', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            throw new Error('Falha ao buscar eventos do servidor.');
        }
        events = await response.json();
    } catch (error) {
        console.error("Erro ao carregar eventos:", error);
        publicEventList.innerHTML = "<p>Erro ao carregar eventos. Tente novamente mais tarde.</p>";
        events = [];
    }
}

// Roteador: Decide qual view mostrar
// (Declarado globalmente para que o popstate possa vê-lo)
function route(view, eventId, pushState) {
    if (view === 'admin-eventos') {
        showAdminEventView(pushState);
    } else if (view === 'admin-usuarios') {
        showAdminUserView(pushState);
    } else if (view === 'details' && eventId) {
        showDetailsView(eventId, pushState);
    } else {
        showPublicView(pushState);
    }
}

// Ouve o botão "Voltar" do navegador
window.addEventListener('popstate', (event) => {
    if (event.state) {
        route(event.state.view, event.state.eventId, false);
    } else {
        showPublicView(false);
    }
});


// Roda TUDO quando o HTML estiver pronto
document.addEventListener('DOMContentLoaded', async () => {

    // --- 1. ATRIBUIÇÃO DOS ELEMENTOS ---
    // (Agora sim, os elementos existem)
    publicView = document.getElementById('public-view');
    adminEventView = document.getElementById('admin-event-view');
    adminUserView = document.getElementById('admin-user-view');
    detailsView = document.getElementById('details-view');
    publicEventList = document.getElementById('public-event-list');
    adminEventList = document.getElementById('admin-event-list');
    eventForm = document.getElementById('event-form');
    userList = document.getElementById('user-list');
    adminButtonsContainer = document.getElementById('admin-buttons-container');
    adminEventBtn = document.getElementById('admin-event-btn');
    adminUserBtn = document.getElementById('admin-user-btn');
    createUserForm = document.getElementById('create-user-form');

    // --- 6. ATRIBUIÇÃO DOS EVENTOS (LISTENERS) ---
    // Agora podemos adicionar os listeners com segurança
    eventForm.addEventListener('submit', handleEventFormSubmit);
    createUserForm.addEventListener('submit', handleCreateUserSubmit);
    adminEventBtn.onclick = () => showAdminEventView(true);
    adminUserBtn.onclick = () => showAdminUserView(true);


    // --- 7. ROTEAMENTO E CARGA INICIAL ---

    // Carrega a lista de quem já demos RSVP
    const savedRsvp = localStorage.getItem('rsvpEvents');
    if (savedRsvp) {
        rsvpEvents = JSON.parse(savedRsvp);
    }

    await loadInitialEvents(); // Carrega eventos da API

    const userRole = localStorage.getItem('userRole');

    let isAdmin = false;
    let isProfessor = false;
    if (userRole === 'ADMIN') {
        isAdmin = true;
        isProfessor = true;
    } else if (userRole === 'PROFESSOR') {
        isProfessor = true;
    }

    if (isProfessor) {
        adminEventBtn.style.display = 'block';
    } else {
        adminEventBtn.style.display = 'none';
    }

    if (isAdmin) {
        adminUserBtn.style.display = 'block';
    } else {
        adminUserBtn.style.display = 'none';
    }

    if (!isAdmin && !isProfessor) {
        adminButtonsContainer.style.display = 'none';
    } else {
        adminButtonsContainer.style.display = 'flex';
    }

    // Define o estado inicial da página
    let initialView = 'public';
    let initialEventId = null;
    if (window.location.hash.startsWith('#evento/')) {
        initialView = 'details';
        initialEventId = parseInt(window.location.hash.split('/')[1]);
    } else if (window.location.hash === '#admin-eventos') {
        initialView = 'admin-eventos';
    } else if (window.location.hash === '#admin-usuarios') {
        initialView = 'admin-usuarios';
    }

    route(initialView, initialEventId, false);

    if (!window.location.hash) {
         history.replaceState({ view: 'public' }, 'Eventos', '#eventos');
    }
});