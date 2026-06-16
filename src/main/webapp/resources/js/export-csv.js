/**
 * Exporte un tableau d'objets au format CSV et déclenche le téléchargement
 * @param {Array} data - La liste complète des données à exporter (ex: actesData)
 * @param {Object} headersMapping - Clés des objets et leurs libellés d'affichage
 * @param {String} filename - Nom du fichier de sortie (sans extension)
 */
function exportCSV(data, headersMapping, filename = 'export') {
    if (!data || data.length === 0) {
        alert("Aucune donnée à exporter.");
        return;
    }

    const keys = Object.keys(headersMapping);
    const labels = Object.values(headersMapping);

    // 1. Création de la ligne d'en-tête (délimitée par des points-virgules pour Excel FR)
    let csvContent = "\uFEFF"; // BOM UTF-8 pour que Excel lise correctement les accents
    csvContent += labels.join(";") + "\n";

    // 2. Transformation des lignes de données
    data.forEach(item => {
        const row = keys.map(key => {
            let valeur = item[key] !== undefined && item[key] !== null ? item[key] : '';

            // Nettoyage de la valeur (suppression des sauts de ligne et échappement des guillemets)
            valeur = String(valeur).replace(/\s+/g, ' ').replace(/"/g, '""');

            // Si la valeur contient un point-virgule, on l'entoure de guillemets
            if (valeur.includes(';')) {
                valeur = `"${valeur}"`;
            }
            return valeur;
        });
        csvContent += row.join(";") + "\n";
    });

    // 3. Déclenchement du téléchargement dans le navigateur
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.setAttribute("href", url);
    link.setAttribute("download", `${filename}_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}