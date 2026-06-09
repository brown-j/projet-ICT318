// ── Navigation & Breadcrumb Management ────────────────────
function initNavigation() {
    const breadcrumbCurrent = document.getElementById("breadcrumbCurrent");

    // Au chargement, récupération du dernier menu cliqué (par défaut: "Tableau de bord")
    const activeMenuTitle = sessionStorage.getItem("activeMenuTitle") || "Tableau de bord";

    // Recherche de l'élément correspondant dans la sidebar
    const activeItem = document.querySelector(`.sidebar-nav .nav-item[data-title="${activeMenuTitle}"]`);

    if (activeItem) {
        activeItem.classList.add("active");
        activeItem.setAttribute("aria-current", "page");

        if (breadcrumbCurrent) {
            breadcrumbCurrent.innerText = activeMenuTitle;
        }
    }

    // Écoute des clics sur les liens de la sidebar pour mémoriser la sélection
    document.querySelectorAll(".sidebar-nav .nav-item").forEach(item => {
        item.addEventListener("click", function () {
            const title = this.getAttribute("data-title");
            if (title) {
                sessionStorage.setItem("activeMenuTitle", title);
            }
        });
    });
}

// ── Theme Toggle ──────────────────────────────────────────
function toggleTheme() {
    const html = document.documentElement;
    const isDark = html.getAttribute('data-theme') === 'dark';
    html.setAttribute('data-theme', isDark ? 'light' : 'dark');

    const icon = document.getElementById('themeIcon');
    if (icon) icon.className = isDark ? 'ti ti-moon' : 'ti ti-sun';

    // On relance avec nos variables globales rafraîchies
    if (typeof initCharts === 'function') {
        initCharts(dashboardBarData, dashboardDoughnutData);
    }
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

// Variables globales pour ne pas perdre les données au changement de thème
let dashboardBarData = {};
let dashboardDoughnutData = [];

// On attend les données en paramètre maintenant
function initChartsServer(donneesDuServeur) {
    dashboardBarData = donneesDuServeur;

    // Extraction des pourcentages
    if (dashboardBarData.repartitionTypes) {
        dashboardDoughnutData = dashboardBarData.repartitionTypes.map(item => item.percentage);
    }

    if (typeof initCharts === 'function') {
        initCharts(dashboardBarData, dashboardDoughnutData);
    }
}


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

document.addEventListener('DOMContentLoaded', () => {
    // Initialisation de la navigation
    initNavigation();

    // ── Modals Listeners ──────────────────────────────────
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