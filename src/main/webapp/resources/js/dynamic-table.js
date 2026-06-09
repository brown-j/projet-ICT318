/**
 * DynamicTable - Gestionnaire générique de tableaux avec pagination et filtrage côté client.
 * Évite les rechargements serveur intempestifs et optimise le rendu du DOM.
 */
class DynamicTable {
    /**
     * @param {Object} config - Configuration du tableau
     * @param {Array} config.data - Données brutes (JSON sérialisé par Gson)
     * @param {string} config.tbodyId - ID de l'élément <tbody>
     * @param {string} config.infoId - ID du conteneur de texte de pagination (ex: "Affichage 1-8...")
     * @param {string} config.pagerId - ID du conteneur des boutons de navigation
     * @param {string} config.totalCountId - ID de l'élément affichant le total filtré
     * @param {number} [config.pageSize=8] - Nombre maximum de lignes par page
     * @param {Function} config.renderRow - Fonction (item) => string renvoyant le HTML d'une ligne <tr>
     * @param {Function} config.filterFn - Fonction (item, filters) => boolean pour valider les filtres
     */
    constructor(config) {
        this.data = config.data || [];
        this.tbody = document.getElementById(config.tbodyId);
        this.infoContainer = document.getElementById(config.infoId);
        this.pagerContainer = document.getElementById(config.pagerId);
        this.totalCountContainer = document.getElementById(config.totalCountId);
        this.pageSize = config.pageSize || 8;
        this.renderRow = config.renderRow;
        this.filterFn = config.filterFn || (() => true);

        this.state = {
            currentPage: 1,
            filters: {}
        };
    }

    /**
     * Met à jour un filtre spécifique et déclenche le rafraîchissement
     * @param {string} key - Clé du filtre (ex: 'query', 'statut')
     * @param {any} value - Valeur appliquée
     */
    setFilter(key, value) {
        this.state.filters[key] = value;
        this.render(1); // Retour automatique à la première page lors d'un filtrage
    }

    /**
     * Calcule et affiche la page demandée
     * @param {number} page - Index de la page à afficher
     */
    render(page = this.state.currentPage) {
        this.state.currentPage = page;

        // 1. Filtrage des données selon la logique métier passée en config
        const filteredData = this.data.filter(item => this.filterFn(item, this.state.filters));
        const totalItems = filteredData.length;
        const totalPages = Math.ceil(totalItems / this.pageSize) || 1;

        // Sécurité : évite de rester bloqué sur une page inexistante après filtrage agressif
        if (this.state.currentPage > totalPages) {
            this.state.currentPage = totalPages;
        }

        const startIndex = (this.state.currentPage - 1) * this.pageSize;
        const endIndex = Math.min(startIndex + this.pageSize, totalItems);
        const pageItems = filteredData.slice(startIndex, endIndex);

        // 2. Gestion de l'état vide
        if (totalItems === 0) {
            this.tbody.innerHTML = `<tr><td colspan="100%" style="text-align:center; padding:var(--space-6); color:var(--text-muted)">Aucune donnée disponible.</td></tr>`;
            if (this.infoContainer) this.infoContainer.innerText = "Affichage 0-0 sur 0 entrées";
            if (this.pagerContainer) this.pagerContainer.innerHTML = '';
            if (this.totalCountContainer) this.totalCountContainer.innerText = '0';
            return;
        }

        // 3. Rendu HTML des lignes
        this.tbody.innerHTML = pageItems.map(item => this.renderRow(item)).join('');

        // 4. Mise à jour des compteurs textuels UI
        if (this.totalCountContainer) this.totalCountContainer.innerText = totalItems;
        if (this.infoContainer) {
            this.infoContainer.innerText = `Affichage ${startIndex + 1}-${endIndex} sur ${totalItems} entrées`;
        }

        // 5. Génération dynamique de la pagination (sans injection de chaînes onclick vulnérables)
        if (this.pagerContainer) {
            this.buildPagination(totalPages);
        }
    }

    /**
     * Construit proprement les boutons de pagination avec des event listeners natifs
     */
    buildPagination(totalPages) {
        this.pagerContainer.innerHTML = '';

        // Bouton Précédent
        const prevBtn = this.createPageButton('<i class="ti ti-chevron-left" aria-hidden="true"></i>', this.state.currentPage - 1, this.state.currentPage === 1);
        this.pagerContainer.appendChild(prevBtn);

        // Boutons de pages numériques
        for (let i = 1; i <= totalPages; i++) {
            const isCurrent = this.state.currentPage === i;
            const pageBtn = this.createPageButton(i.toString(), i, false, isCurrent);
            this.pagerContainer.appendChild(pageBtn);
        }

        // Bouton Suivant
        const nextBtn = this.createPageButton('<i class="ti ti-chevron-right" aria-hidden="true"></i>', this.state.currentPage + 1, this.state.currentPage === totalPages);
        this.pagerContainer.appendChild(nextBtn);
    }

    /**
     * Fabrique un élément bouton HTMLButtonElement configuré
     */
    createPageButton(content, targetPage, isDisabled, isCurrent = false) {
        const btn = document.createElement('button');
        btn.className = `btn ${isCurrent ? 'btn-primary' : 'btn-ghost'} btn-sm`;
        btn.innerHTML = content;
        btn.disabled = isDisabled;

        // Style standardisé pour les numéros de page carrés
        if (!isNaN(content)) {
            btn.style.width = '32px';
            btn.style.padding = '0';
        }

        if (!isDisabled && !isCurrent) {
            btn.addEventListener('click', () => this.render(targetPage));
        }

        return btn;
    }
}