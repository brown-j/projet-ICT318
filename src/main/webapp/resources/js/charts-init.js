// ── Charts Initialization ─────────────────────────────────
let chartActes, chartTypes;

function getCSSVar(v) {
    return getComputedStyle(document.documentElement).getPropertyValue(v).trim();
}

/**
 * Ajuste un tableau pour qu'il ait exactement la taille voulue.
 * S'il est trop grand -> on coupe (slice)
 * S'il est trop petit -> on complète avec des 0 (concat)
 */
function formatDataArray(arr, expectedLength) {
    // Sécurité : si on passe null ou undefined, on crée un tableau vide
    const safeArr = Array.isArray(arr) ? arr : [];

    if (safeArr.length > expectedLength) {
        return safeArr.slice(0, expectedLength);
    } else if (safeArr.length < expectedLength) {
        const padding = Array(expectedLength - safeArr.length).fill(0);
        return safeArr.concat(padding);
    }
    return safeArr;
}

// ── Fonction principale (attend 2 objets/tableaux en paramètres) ──
// barData : { naissances: [...], mariages: [...], deces: [...] }
// doughnutData : [...]
function initCharts(barData = {}, doughnutData = []) {
    // 1. Lecture directe des variables CSS (elles gèrent déjà le thème via ton CSS)
    const primary = getCSSVar('--c-primary-500') || '#0A7E6A';
    const secondary = getCSSVar('--c-secondary-400') || '#14B8A6';
    const accent = getCSSVar('--c-accent-400') || '#F97316';
    const info = getCSSVar('--c-info-500') || '#3B82F6';


    // Variables pour la grille et le texte (adapte les noms si tes variables CSS s'appellent autrement)
    const gridColor = getCSSVar('--border-color') || 'rgba(128, 128, 128, 0.1)';
    const textColor = getCSSVar('--text-secondary') || '#6B9E97';

    // 2. Nettoyage et formatage des données (12 mois pour le premier, 4 types pour le second)
    const naissancesData = formatDataArray(barData.naissances, 12);
    const mariagesData = formatDataArray(barData.mariages, 12);
    const decesData = formatDataArray(barData.deces, 12);

    const repartitionData = formatDataArray(doughnutData, 4);

    // 3. Configuration de base
    Chart.defaults.font.family = "'DM Sans', system-ui, sans-serif";
    Chart.defaults.font.size = 12;

    if (chartActes) chartActes.destroy();
    if (chartTypes) chartTypes.destroy();

    // 4. Initialisation du Bar Chart (12 mois)
    const ctx1 = document.getElementById('chartActes');
    if (ctx1) {
        chartActes = new Chart(ctx1, {
            type: 'bar',
            data: {
                labels: ['Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév', 'Mar', 'Avr', 'Mai'],
                datasets: [
                    {
                        label: 'Naissances',
                        data: naissancesData,
                        backgroundColor: primary + 'CC', // 'CC' ajoute de la transparence (si primary est en HEX)
                        borderRadius: 4,
                        borderSkipped: false
                    },
                    {
                        label: 'Mariages',
                        data: mariagesData,
                        backgroundColor: secondary + 'CC',
                        borderRadius: 4,
                        borderSkipped: false
                    },
                    {
                        label: 'Décès',
                        data: decesData,
                        backgroundColor: accent + '99',
                        borderRadius: 4,
                        borderSkipped: false
                    }
                ]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top', labels: { color: textColor, usePointStyle: true, pointStyleWidth: 8, padding: 16, font: { size: 12 } } },
                    tooltip: { mode: 'index', intersect: false, cornerRadius: 8, padding: 10 }
                },
                scales: {
                    x: { stacked: false, grid: { display: false }, ticks: { color: textColor }, border: { color: 'transparent' } },
                    y: { stacked: false, grid: { color: gridColor }, ticks: { color: textColor }, border: { color: 'transparent' } }
                }
            }
        });
    }

    // 5. Initialisation du Doughnut Chart (4 types)
    const ctx2 = document.getElementById('chartTypes');
    if (ctx2) {
        chartTypes = new Chart(ctx2, {
            type: 'doughnut',
            data: {
                labels: ['Naissances', 'Mariages', 'Décès', 'Autres'],
                datasets: [{
                    data: repartitionData,
                    backgroundColor: [primary, secondary, accent, info],
                    borderWidth: 0,
                    hoverOffset: 6
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false, cutout: '70%',
                plugins: {
                    legend: { display: false },
                    tooltip: { cornerRadius: 8, padding: 10 }
                }
            }
        });
    }
}