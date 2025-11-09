// --- 0. VERIFICAÇÃO DE AUTENTICAÇÃO ---
// Esta é a primeira coisa que roda.
// Protege a página contra acesso não autorizado.
(function() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        // Se não tem token, chuta o usuário de volta para o login
        alert('Você precisa estar logado para ver esta página.');
        window.location.href = '/index.html'; // Ou '/'
    }
})();

// --- 1. Seleção dos Elementos da Página ---
const publicView = document.getElementById('public-view');
// loginView NÃO EXISTE MAIS AQUI
const adminView = document.getElementById('admin-view');
const detailsView = document.getElementById('details-view');
const publicEventList = document.getElementById('public-event-list');
const adminEventList = document.getElementById('admin-event-list');
// loginForm NÃO EXISTE MAIS AQUI
const eventForm = document.getElementById('event-form');
const adminButton = document.getElementById('admin-button');


// --- 2. Simulação de Banco de Dados ---
let events = [];
let nextEventId = 1;
let rsvpEvents = [];

function saveEvents() {
    localStorage.setItem('schoolEvents', JSON.stringify(events));
}

function saveRsvp() {
    localStorage.setItem('rsvpEvents', JSON.stringify(rsvpEvents));
}

// --- 3. Funções de Controle de Visualização ---
function showPublicView() {
    publicView.style.display = 'block'; adminView.style.display = 'none'; detailsView.style.display = 'none';
    renderPublicEvents();
}

function handleAdminButton() {
    // A lógica de login foi removida.
    // O usuário já está logado, apenas mostramos a view admin.
    showAdminView();
}

function handleBackFromAdmin() {
    showPublicView();
}
function showAdminView() {
    publicView.style.display = 'none'; adminView.style.display = 'block'; detailsView.style.display = 'none';
    renderAdminEvents();
}
function showDetailsView(eventId) {
    publicView.style.display = 'none'; adminView.style.display = 'none'; detailsView.style.display = 'block';
    const event = events.find(e => e.id === eventId);
    if (!event) return;
    document.getElementById('details-title').textContent = event.title;
    let mapButtonHTML = event.mapLink ? `<p><a href="${event.mapLink}" target="_blank" class="button button-primary">Ver Localização no Mapa</a></p>` : '';
    document.getElementById('details-content').innerHTML = `<p><strong>Data:</strong> ${new Date(event.date).toLocaleDateString('pt-BR', {timeZone: 'UTC'})}</p><p><strong>Hora:</strong> ${event.time}</p><p><strong>Local:</strong> ${event.local}</p><p>${event.description}</p>${mapButtonHTML}`;
    document.getElementById('attendee-count').textContent = `Confirmados: ${event.attendees}`;
    document.getElementById('rsvp-button').onclick = () => handleRsvp(eventId);
}

// --- 4. Funções de Renderização ---
function renderPublicEvents() {
    publicEventList.innerHTML = '';
    const sortedEvents = [...events].sort((a, b) => new Date(a.date) - new Date(b.date));
    if (sortedEvents.length === 0) { publicEventList.innerHTML = "<p>Nenhum evento agendado.</p>"; return; }
    const template = document.getElementById('public-event-template');

    sortedEvents.forEach(event => {
        const clone = template.content.cloneNode(true);
        const listItem = clone.querySelector('li');
        listItem.onclick = () => showDetailsView(event.id);

        const shareButton = clone.querySelector('.event-share');
        shareButton.onclick = (e) => {
            e.stopPropagation();
            handleShare(event.id);
        };

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
// A LÓGICA DE LOGIN FOI MOVIDA PARA login.js

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    window.location.href = '/index.html'; // Volta para o login
}

// --- 6. Funções CRUD e RSVP ---
eventForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const eventId = document.getElementById('event-id').value;
    const eventData = {
        title: document.getElementById('event-title').value,
        date: document.getElementById('event-date').value,
        time: document.getElementById('event-time').value,
        local: document.getElementById('event-local').value,
        mapLink: document.getElementById('event-map-link').value,
        description: document.getElementById('event-description').value
    };

    if (eventId) {
        const index = events.findIndex(event => event.id == eventId);
        eventData.attendees = events[index].attendees;
        events[index] = { id: Number(eventId), ...eventData };
    } else {
        eventData.id = nextEventId++;
        eventData.attendees = 0;
        events.push(eventData);
    }
    saveEvents(); resetForm(); renderAdminEvents();
});

function handleEdit(id) {
    const eventToEdit = events.find(event => event.id === id);
    document.getElementById('event-id').value = eventToEdit.id;
    document.getElementById('event-title').value = eventToEdit.title;
    document.getElementById('event-date').value = eventToEdit.date;
    document.getElementById('event-time').value = eventToEdit.time;
    document.getElementById('event-local').value = eventToEdit.local;
    document.getElementById('event-map-link').value = eventToEdit.mapLink;
    document.getElementById('event-description').value = eventToEdit.description;
    window.scrollTo(0, 0);
}

function handleDelete(id) {
    if (confirm('Tem certeza?')) {
        events = events.filter(event => event.id !== id);
        saveEvents();
        renderAdminEvents();
    }
}

function resetForm() {
    eventForm.reset();
    document.getElementById('event-id').value = '';
}

function handleRsvp(eventId) {
    if (rsvpEvents.includes(eventId)) {
        alert('Você já confirmou presença neste evento.');
        return;
    }
    const event = events.find(e => e.id === eventId);
    if (event) {
        event.attendees++;
        rsvpEvents.push(eventId);
        saveEvents();
        saveRsvp();
        showDetailsView(eventId);
    }
}

function handleShare(eventId) {
    // ... (função de compartilhar sem mudanças)
}


// --- 7. Inicialização da Aplicação ---
function initializeApp() {
    const savedEvents = localStorage.getItem('schoolEvents');
    if (savedEvents) {
        try {
            events = JSON.parse(savedEvents);
        } catch (e) { events = []; }

        if (events.length === 0) {
            // (Dados padrão, sem mudanças)
        }
        events.forEach(event => {
            if (event.attendees === undefined) event.attendees = 0;
            if (event.mapLink === undefined) event.mapLink = '';
        });
    } else {
        // (Dados padrão, sem mudanças)
    }

    const savedRsvp = localStorage.getItem('rsvpEvents');
    if (savedRsvp) {
        rsvpEvents = JSON.parse(savedRsvp);
    }

    if (events.length > 0) {
        nextEventId = Math.max(...events.map(e => e.id)) + 1;
    } else {
        nextEventId = 1;
    }

    // --- LÓGICA DE PERMISSÃO ---
    // A verificação de login já foi feita no topo.
    // Agora, verificamos a ROLE para mostrar/esconder o botão Admin.
    const userRole = localStorage.getItem('userRole');
    if (userRole === 'ADMIN' || userRole === 'PROFESSOR') {
        adminButton.style.display = 'block';
    } else {
        adminButton.style.display = 'none';
    }

    // Mostrar a tela inicial
    showPublicView();
}
document.addEventListener('DOMContentLoaded', initializeApp);