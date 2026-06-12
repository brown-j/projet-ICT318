// On passe directement l'élément "realSelect" en paramètre désormais
function initSearchableSelect(realSelect) {
    if (!realSelect || realSelect.hasAttribute('data-search-initialized')) return;
    if (realSelect.disabled) return;

    realSelect.setAttribute('data-search-initialized', 'true');
    realSelect.style.display = 'none';

    const container = document.createElement('div');
    container.className = 'searchable-select-container';
    realSelect.parentNode.insertBefore(container, realSelect);
    container.appendChild(realSelect);

    const searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.className = 'form-control';
    searchInput.placeholder = '🔍 Taper pour rechercher...';
    searchInput.autocomplete = 'off';
    container.appendChild(searchInput);

    const dropdown = document.createElement('div');
    dropdown.className = 'search-dropdown-wrapper';
    container.appendChild(dropdown);

    if (realSelect.selectedIndex !== -1 && realSelect.value !== "") {
        searchInput.value = realSelect.options[realSelect.selectedIndex].text;
    }

    function filterOptions(query) {
        dropdown.innerHTML = '';
        const searchTerms = query.toLowerCase().trim().split(' ');
        let matchCount = 0;

        Array.from(realSelect.options).forEach((option) => {
            if (option.value === "") return;
            const text = option.text.toLowerCase();
            const matches = searchTerms.every(term => text.includes(term));

            if (matches) {
                matchCount++;
                if (matchCount > 30) return; // Limite de performance

                const item = document.createElement('div');
                item.className = 'search-dropdown-item';
                item.innerText = option.text;
                item.dataset.value = option.value;

                if (realSelect.value === option.value) item.classList.add('selected');

                item.addEventListener('click', (e) => {
                    e.stopPropagation();
                    realSelect.value = option.value;
                    searchInput.value = option.text;
                    dropdown.classList.remove('open');
                    realSelect.dispatchEvent(new Event('change'));
                });
                dropdown.appendChild(item);
            }
        });

        if (matchCount === 0) {
            const noResults = document.createElement('div');
            noResults.className = 'search-dropdown-no-results';
            noResults.innerText = 'Aucune correspondance';
            dropdown.appendChild(noResults);
        }
    }

    searchInput.addEventListener('input', (e) => { dropdown.classList.add('open'); filterOptions(e.target.value); });
    searchInput.addEventListener('focus', () => { dropdown.classList.add('open'); filterOptions(searchInput.value); });

    document.addEventListener('click', (e) => {
        if (!container.contains(e.target)) {
            dropdown.classList.remove('open');
            if (realSelect.selectedIndex !== -1 && realSelect.value !== "") {
                searchInput.value = realSelect.options[realSelect.selectedIndex].text;
            } else {
                searchInput.value = "";
            }
        }
    });
}