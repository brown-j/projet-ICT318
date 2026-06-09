package com.app.model.viewmodel;

import java.util.ArrayList;
import java.util.List;
import com.app.model.theme.ThemeColor;

public class EvolutionActesData {
    // Les listes de données brutes (les 12 mois)
    private List<Integer> naissances;
    private List<Integer> mariages;
    private List<Integer> deces;

    // Configuration des labels (figée dans la classe)
    private final String labelNaissances = "Naissances";
    private final String labelMariages = "Mariages";
    private final String labelDeces = "Décès";

    // Configuration des couleurs via ton Enum
    private final String colorNaissances = ThemeColor.PRIMARY.getCssClass();
    private final String colorMariages = ThemeColor.SECONDARY.getCssClass();
    private final String colorDeces = ThemeColor.ACCENT.getCssClass();

    // NOUVEAU : La liste générée pour le graphique circulaire
    private List<StatItem> repartitionTypes;

    public EvolutionActesData() {
    }

    public EvolutionActesData(List<Integer> naissances, List<Integer> mariages, List<Integer> deces) {
        this.naissances = naissances;
        this.mariages = mariages;
        this.deces = deces;
        // On génère automatiquement la répartition à la création
        this.repartitionTypes = genererRepartition();
    }

    /**
     * Méthode privée qui calcule la somme des actes et génère les StatItems
     * (pourcentages)
     */
    private List<StatItem> genererRepartition() {
        List<StatItem> stats = new ArrayList<>();

        // Sécurité en cas de listes vides/nulles
        if (naissances == null || mariages == null || deces == null)
            return stats;

        // 1. Calcul des totaux par catégorie
        int totalN = naissances.stream().mapToInt(Integer::intValue).sum();
        int totalM = mariages.stream().mapToInt(Integer::intValue).sum();
        int totalD = deces.stream().mapToInt(Integer::intValue).sum();

        int totalGlobal = totalN + totalM + totalD;

        // Sécurité pour éviter la division par zéro
        if (totalGlobal == 0) {
            stats.add(new StatItem(labelNaissances, 0, ThemeColor.PRIMARY));
            stats.add(new StatItem(labelMariages, 0, ThemeColor.SECONDARY));
            stats.add(new StatItem(labelDeces, 0, ThemeColor.ACCENT));
            return stats;
        }

        // 2. Calcul des pourcentages
        int pctNaissances = Math.round((float) totalN * 100 / totalGlobal);
        int pctMariages = Math.round((float) totalM * 100 / totalGlobal);
        int pctDeces = Math.round((float) totalD * 100 / totalGlobal);

        // 3. Création de la liste
        stats.add(new StatItem(labelNaissances, pctNaissances, ThemeColor.PRIMARY));
        stats.add(new StatItem(labelMariages, pctMariages, ThemeColor.SECONDARY));
        stats.add(new StatItem(labelDeces, pctDeces, ThemeColor.ACCENT));

        return stats;
    }

    // --- Getters & Setters ---

    public List<Integer> getNaissances() {
        return naissances;
    }

    public void setNaissances(List<Integer> naissances) {
        this.naissances = naissances;
    }

    public List<Integer> getMariages() {
        return mariages;
    }

    public void setMariages(List<Integer> mariages) {
        this.mariages = mariages;
    }

    public List<Integer> getDeces() {
        return deces;
    }

    public void setDeces(List<Integer> deces) {
        this.deces = deces;
    }

    public String getLabelNaissances() {
        return labelNaissances;
    }

    public String getLabelMariages() {
        return labelMariages;
    }

    public String getLabelDeces() {
        return labelDeces;
    }

    public String getColorNaissances() {
        return colorNaissances;
    }

    public String getColorMariages() {
        return colorMariages;
    }

    public String getColorDeces() {
        return colorDeces;
    }

    // Getter pour la répartition générée
    public List<StatItem> getRepartitionTypes() {
        // Recalcul de sécurité au cas où on a utilisé les setters après la création
        if (repartitionTypes == null) {
            return genererRepartition();
        }
        return repartitionTypes;
    }
}