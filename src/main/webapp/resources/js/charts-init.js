// ── Charts Initialization ─────────────────────────────────
let chartActes, chartTypes;

function getCSSVar(v) {
    return getComputedStyle(document.documentElement).getPropertyValue(v).trim();
}

function initCharts() {
    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
    const primary = isDark ? '#2DD4BF' : '#0A7E6A';
    const secondary = isDark ? '#5EEAD4' : '#14B8A6';
    const accent = isDark ? '#FB923C' : '#F97316';
    const info = isDark ? '#60A5FA' : '#3B82F6';
    const gridColor = isDark ? 'rgba(45,212,191,0.08)' : 'rgba(10,126,106,0.07)';
    const textColor = isDark ? '#7ECEC6' : '#6B9E97';

    Chart.defaults.font.family = "'DM Sans', system-ui, sans-serif";
    Chart.defaults.font.size = 12;

    if (chartActes) chartActes.destroy();
    if (chartTypes) chartTypes.destroy();

    const ctx1 = document.getElementById('chartActes');
    if (!ctx1) return;

    chartActes = new Chart(ctx1, {
        type: 'bar',
        data: {
            labels: ['Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév', 'Mar', 'Avr', 'Mai'],
            datasets: [
                {
                    label: 'Naissances', data: [14, 18, 12, 16, 20, 15, 17, 13, 19, 22, 16, 18],
                    backgroundColor: primary + 'CC', borderRadius: 4, borderSkipped: false
                },
                {
                    label: 'Mariages', data: [8, 10, 6, 9, 12, 8, 11, 7, 10, 14, 9, 11],
                    backgroundColor: secondary + 'CC', borderRadius: 4, borderSkipped: false
                },
                {
                    label: 'Décès', data: [5, 4, 7, 5, 6, 8, 5, 6, 4, 7, 5, 4],
                    backgroundColor: accent + '99', borderRadius: 4, borderSkipped: false
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

    const ctx2 = document.getElementById('chartTypes');
    if (!ctx2) return;
    chartTypes = new Chart(ctx2, {
        type: 'doughnut',
        data: {
            labels: ['Naissances', 'Mariages', 'Décès', 'Autres'],
            datasets: [{ data: [38, 27, 20, 15], backgroundColor: [primary, secondary, accent, info], borderWidth: 0, hoverOffset: 6 }]
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
