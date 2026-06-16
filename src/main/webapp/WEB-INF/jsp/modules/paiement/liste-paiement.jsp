<%-- Payment History View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
    <div class="page-header-left">
        <h1>Historique des Paiements</h1>
        <p><strong id="paiement-total-count">0</strong> transaction(s) filtrée(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
    </div>
    <div class="page-header-actions">
        <button id="btn-exporter-paiements" class="btn btn-ghost btn-sm">
            <i class="ti ti-download" aria-hidden="true"></i> Exporter la table
        </button>
    </div>
</div>

<%-- Alerte Caisse du jour Dynamique --%>
<div class="alert alert-success mb-4">
    <i class="ti ti-cash" aria-hidden="true"></i>
    <div>
        <div class="alert-title">Caisse du jour : <strong>${caisseDuJour} FCFA</strong></div>
        Somme totale des encaissements enregistrés aujourd'hui.
    </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
    <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

        <div style="flex:1;min-width:200px">
            <label class="form-label" for="searchPaiement">Rechercher</label>
            <div class="input-group">
                <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
                <input class="form-control" id="searchPaiement" type="search" placeholder="N° de reçu, nom du caissier…">
            </div>
        </div>

        <%-- 💡 DYNAMISATION DU MODE DE PAIEMENT : Options injectées par JS --%>
        <div style="min-width:160px">
            <label class="form-label" for="filterModePaiement">Mode de paiement</label>
            <select class="form-control" id="filterModePaiement">
                <option value="">Tous les modes</option>
            </select>
        </div>
    </div>
</div>

<div class="card" style="padding:0">
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>N° Reçu</th>
                    <th>Demande</th>
                    <th>Montant</th>
                    <th>Mode</th>
                    <th>Caissier</th>
                    <th>Date / Heure</th>
                </tr>
            </thead>
            <tbody id="paiement-table-body">
                <%-- Géré dynamiquement par le tableau.js --%>
            </tbody>
        </table>
    </div>

    <%-- Footer de table & Pagination dynamique --%>
    <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
        <span id="paiement-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
        <div id="paiement-pagination-buttons" style="display:flex;gap:6px"></div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // 1. Sécurisation Null-Safe du JSON via JSP Expression Language
        const paiementsData = ${empty paiementsJson ? '[]' : paiementsJson};
        
        // 💡 1.B PEUPLEMENT DYNAMIQUE DU FILTRE DEPUIS LES DONNÉES DU TABLEAU
        const filterModeSelect = document.getElementById('filterModePaiement');
        
        // On regroupe par code technique unique pour éviter les doublons tout en gardant le libellé associé
        const modesMap = new Map();
        paiementsData.forEach(p => {
            if (p.modePaiement && p.modePaiementLabel) {
                modesMap.set(p.modePaiement, p.modePaiementLabel);
            }
        });
        
        // Tri par ordre alphabétique du libellé et injection
        [...modesMap.entries()]
        .sort((a, b) => a[1].localeCompare(b[1]))
        .forEach(([code, label]) => {
            const option = document.createElement('option');
            option.value = code;
            option.textContent = label;
            filterModeSelect.appendChild(option);
        });
        
        // 2. Initialisation du tableau dynamique pour les paiements
        const paiementTable = new DynamicTable({
            data: paiementsData,
            tbodyId: 'paiement-table-body',
            infoId: 'paiement-pagination-info',
            pagerId: 'paiement-pagination-buttons',
            totalCountId: 'paiement-total-count',
            pageSize: 8,
            
            // Gabarit HTML d'une ligne (Template String)
            renderRow: (p) => {
                const mode = p.modePaiement || '';
                let iconClass = 'ti-wallet'; // Par défaut
                if (mode === 'ESPECES') iconClass = 'ti-cash';
                else if (mode === 'MOBILE_MONEY') iconClass = 'ti-device-mobile';
                else if (mode === 'CARTE_BANCAIRE') iconClass = 'ti-credit-card';
                
                return `
                <tr>
                <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted);font-weight:var(--fw-medium)">\${p.referenceRecu || ''}</code></td>
                <td>\${p.typeDemandeLabel || ''}</td>
                <td><strong>\${p.montantFormate || '0'} FCFA</strong></td>
                <td>
                <span class="badge badge-primary">
                <i class="ti \${iconClass}" style="font-size:11px" aria-hidden="true"></i>
                \${p.modePaiementLabel || ''}
                </span>
                </td>
                <td>\${p.caissierNom || ''}</td>
                <td style="color:var(--text-muted)">\${p.datePaiementFormatee || ''}</td>
                </tr>
                `;
            },
            
            // Prédicat de filtrage multicritère sécurisé contre le Null/Undefined
            filterFn: (item, filters) => {
                const query = (filters.query || '').toLowerCase().trim();
                const matchQuery = !query ||
                (item.referenceRecu || '').toLowerCase().includes(query) ||
                (item.caissierNom || '').toLowerCase().includes(query) ||
                (item.typeDemandeLabel || '').toLowerCase().includes(query);
                
                const matchMode = !filters.mode || item.modePaiement === filters.mode;
                
                return matchQuery && matchMode;
            }
        });
        
        // 3. Liaison des contrôles de l'UI aux critères de filtrage
        document.getElementById('searchPaiement').addEventListener('input', (e) => {
            paiementTable.setFilter('query', e.target.value);
        });
        
        document.getElementById('filterModePaiement').addEventListener('change', (e) => {
            paiementTable.setFilter('mode', e.target.value);
        });
        
        // 4. Premier rendu du tableau au chargement de l'écran
        paiementTable.render(1);
        
        // Configuration et liaison de l'exportation globale CSV
        const mappingColonnesPaiements = {
            referenceRecu: "N° Reçu",
            typeDemandeLabel: "Demande associée",
            montantFormate: "Montant (FCFA)",
            modePaiementLabel: "Mode de Paiement",
            caissierNom: "Caissier / Agent",
            datePaiementFormatee: "Date & Heure"
        };
        
        document.getElementById('btn-exporter-paiements').addEventListener('click', (e) => {
            e.preventDefault();
            exportCSV(paiementsData, mappingColonnesPaiements, 'historique_encaissements');
        });
    });
</script>