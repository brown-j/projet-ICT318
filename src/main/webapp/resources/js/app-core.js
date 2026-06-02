// ── Theme Toggle ──────────────────────────────────────────
function toggleTheme() {
    const html = document.documentElement;
    const isDark = html.getAttribute('data-theme') === 'dark';
    html.setAttribute('data-theme', isDark ? 'light' : 'dark');
    document.getElementById('themeIcon').className = isDark ? 'ti ti-moon' : 'ti ti-sun';
    if (typeof initCharts === 'function') initCharts();
}

// ── Modal Management ───────────────────────────────────────
function openModal(name) {
    const m = document.getElementById('modal-' + name);
    if (m) {
        m.style.display = 'flex';
        m.querySelector('input,select') && m.querySelector('input,select').focus();
    }
}

function closeModal(name) {
    const m = document.getElementById('modal-' + name);
    if (m) m.style.display = 'none';
}

document.addEventListener('DOMContentLoaded', () => {
    // Close modal when clicking outside
    document.querySelectorAll('.modal-backdrop').forEach(m => {
        m.addEventListener('click', e => {
            if (e.target === m) m.style.display = 'none';
        });
    });

    // Close modal on ESC key
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-backdrop').forEach(m => m.style.display = 'none');
        }
    });
});

function saveForm() {
    const modals = document.querySelectorAll('.modal-backdrop');
    modals.forEach(m => m.style.display = 'none');
    showToast('Enregistrement réussi !', 'success');
}

// ── Toast Notifications ───────────────────────────────────
const toastIcons = {
    success: 'ti-circle-check',
    error: 'ti-circle-x',
    warning: 'ti-alert-triangle',
    info: 'ti-info-circle'
};

function showToast(msg, type = 'info') {
    const c = document.getElementById('toastContainer');
    if (!c) return;

    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `<i class="ti ${toastIcons[type]}" aria-hidden="true"></i>
    <span class="toast-msg">${msg}</span>
    <button class="icon-btn" style="width:24px;height:24px;flex-shrink:0" onclick="this.parentElement.remove()" aria-label="Fermer">
      <i class="ti ti-x" style="font-size:13px" aria-hidden="true"></i>
    </button>`;

    c.appendChild(t);
    setTimeout(() => t.style.opacity = '0', 3500);
    setTimeout(() => t.remove(), 3800);
}
