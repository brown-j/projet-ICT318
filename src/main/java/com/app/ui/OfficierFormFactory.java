package com.app.ui;

import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.Role;
import com.app.jpa.model.JPAEnum.StatutOfficier;
import com.app.ui.FormBuilder.*;

public class OfficierFormFactory {

	public static String genererHtml(OfficierEtatCivil officier, String actionUrl, boolean readOnly) {

		// 1. Isolation et protection contre les NullPointerExceptions
		String idValeur = (officier.getId() != null) ? String.valueOf(officier.getId()) : "";
		String dateValeur = (officier.getDatePriseFonction() != null)
				? officier.getDatePriseFonction().toString()
				: "";
		String roleValeur = (officier.getRole() != null) ? officier.getRole().name() : "";
		String statutValeur = (officier.getStatut() != null) ? officier.getStatut().name()
				: StatutOfficier.ACTIF.name();

		// 2. Construction dynamique calquée sur ton FormBuilder générique
		return new FormContainer(actionUrl)
				// Champ caché pour l'ID (Gestion des modifications)
				.addRow(FormRow.single(FormItem.hidden("id", idValeur)))

				// Ligne 1 : Nom & Prénom
				.addRow(FormRow.duo(
						FormItem.input(FormItemType.TEXT, "nom", "Nom de l'agent")
								.value(officier.getNom())
								.placeholder("ex: Kondo").required(),
						FormItem.input(FormItemType.TEXT, "prenom", "Prénom(s)")
								.value(officier.getPrenom())
								.placeholder("ex: Emilie").required()))

				// Ligne 2 : Titre professionnel & Service d'affectation
				.addRow(FormRow.duo(
						FormItem.input(FormItemType.TEXT, "titre", "Titre / Civilité")
								.value(officier.getTitre())
								.placeholder("ex: Mme l'Officier, M. l'Agent")
								.required(),
						FormItem.input(FormItemType.TEXT, "service", "Service / Bureau")
								.value(officier.getService())
								.placeholder("ex: Service État Civil, Bureau des Naissances")
								.required()))

				// Ligne 3 : Matricule & Date de prise de fonction
				.addRow(FormRow.duo(
						FormItem.input(FormItemType.TEXT, "matricule",
								"Matricule Administratif")
								.value(officier.getMatricule())
								.placeholder("ex: OEC-2026-001").required(),
						FormItem.input(FormItemType.DATE, "datePriseFonction",
								"Date de prise de fonction")
								.value(dateValeur)
								.required()))

				// Ligne 4 : Identifiants d'accès (Email & Téléphone)
				.addRow(FormRow.duo(
						FormItem.input(FormItemType.EMAIL, "email", "Adresse Email (Login)")
								.value(officier.getEmail())
								.placeholder("ex: e.kondo@mairie.cm").required(),
						FormItem.input(FormItemType.TEL, "telephone", "Numéro de Téléphone")
								.value(officier.getTelephone())
								.placeholder("ex: 6XX XX XX XX")))

				// Ligne 5 : Mot de passe (Uniquement affiché en création ou modification,
				// masqué par défaut)
				.addRow(FormRow.single(
						FormItem.input(FormItemType.PASSWORD, "motDePasse",
								"Mot de passe d'accès")
								.value(officier.getMotDePasse())
								.placeholder("Saisissez un mot de passe sécurisé (sera haché via BCrypt)")))

				// Ligne 6 : Gestion des permissions (Rôle applicatif & Statut d'accès)
				.addRow(FormRow.duo(
						FormItem.select("role", "Rôle système (RBAC)", Role.values())
								.value(roleValeur).required(),
						FormItem.select("statut", "Statut du compte", StatutOfficier.values())
								.value(statutValeur).required()))

				// Ligne 7 : Upload de la griffe/signature numérique de l'officier
				.addRow(FormRow.single(
						FormItem.input(FormItemType.FILE, "signatureFile",
								"Image de la signature numérique (.png, .jpg)")
								.placeholder("Choisir un fichier image pour l'apposer sur les futurs actes")))

				.readOnly(readOnly)
				.multipart()
				.render();
	}
}