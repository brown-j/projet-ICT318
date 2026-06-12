// ── Navigation & Breadcrumb Management ────────────────────
function initNavigation() {
    const breadcrumbCurrent = document.getElementById("breadcrumbCurrent");
    const activeMenuTitle = sessionStorage.getItem("activeMenuTitle") || "Tableau de bord";
    const activeItem = document.querySelector(`.sidebar-nav .nav-item[data-title="${activeMenuTitle}"]`);

    if (activeItem) {
        activeItem.classList.add("active");
        activeItem.setAttribute("aria-current", "page");

        if (breadcrumbCurrent) {
            breadcrumbCurrent.innerText = activeMenuTitle;
        }
    }

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

    if (typeof initCharts === 'function') {
        initCharts(dashboardBarData, dashboardDoughnutData);
    }
}

// ── Modal Management ───────────────────────────────────────
function openModal(id) {
    const modal = document.getElementById('modal-' + id);
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';

        // 💡 AUTOMATISATION : Dès qu'une modale s'ouvre, on initialise ses Combobox masquées
        if (typeof initSearchableSelect === 'function') {
            modal.querySelectorAll('select[data-searchable="true"]').forEach(select => {
                initSearchableSelect(select);
            });
        }
    }
}

function closeModal(id) {
    const modal = document.getElementById('modal-' + id);
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = 'auto';
    }
}

// Variables globales pour les graphiques
let dashboardBarData = {};
let dashboardDoughnutData = [];

function initChartsServer(donneesDuServeur) {
    dashboardBarData = donneesDuServeur;
    if (dashboardBarData.repartitionTypes) {
        dashboardDoughnutData = dashboardBarData.repartitionTypes.map(item => item.percentage);
    }
    if (typeof initCharts === 'function') {
        initCharts(dashboardBarData, dashboardDoughnutData);
    }
}

function saveForm() {
    const modals = document.querySelectorAll('.modal-backdrop');
    modals.forEach(m => m.classList.remove('show'));
    document.body.style.overflow = 'auto';
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

// ── Document Ready ────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();

    // Fermeture des modales au clic extérieur
    document.querySelectorAll('.modal-backdrop').forEach(m => {
        m.addEventListener('click', e => {
            if (e.target === m) {
                m.classList.remove('show');
                document.body.style.overflow = 'auto';
            }
        });
    });

    // Fermeture des modales avec la touche Échap
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-backdrop').forEach(m => {
                m.classList.remove('show');
            });
            document.body.style.overflow = 'auto';
        }
    });

    // Gestion de la surbrillance des lignes de tableau
    document.addEventListener('click', (e) => {
        const clickedRow = e.target.closest('.table-wrapper tbody tr');
        if (!clickedRow) return;

        const parentTbody = clickedRow.closest('tbody');
        const allRows = parentTbody.querySelectorAll('tr');
        allRows.forEach(row => row.classList.remove('row-selected'));

        clickedRow.classList.add('row-selected');
    });

    // 💡 CORRECTION DU RECHERCHE RAPIDE : Approche 100% Générique
    // On ne cible plus aucun ID. On scanne la page à la recherche de n'importe quel 
    // select marqué "data-searchable" et on lui passe l'élément DOM complet.
    if (typeof initSearchableSelect === 'function') {
        document.querySelectorAll('select[data-searchable="true"]').forEach(select => {
            initSearchableSelect(select);
        });
    }
});