<%-- JSP Include: Footer with Script Includes --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Modales globales -->
<div class="modal-backdrop" id="modal-citoyen" style="display:none" role="dialog" aria-modal="true" aria-labelledby="modalCitoyenTitle">
  <div class="modal">
    <div class="modal-header">
      <h2 class="modal-title" id="modalCitoyenTitle" style="font-size:var(--text-xl)">Enregistrer un citoyen</h2>
      <button class="icon-btn" onclick="closeModal('citoyen')" aria-label="Fermer">
        <i class="ti ti-x" aria-hidden="true"></i>
      </button>
    </div>
    <div class="modal-body">
      <div class="grid cols-2 gap-4 mb-4">
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fNom">Nom <span class="required">*</span></label>
          <input class="form-control" id="fNom" type="text" placeholder="ex: Mbarga">
        </div>
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fPrenom">Prénom(s) <span class="required">*</span></label>
          <input class="form-control" id="fPrenom" type="text" placeholder="ex: Jean-Paul">
        </div>
      </div>
      <div class="grid cols-2 gap-4 mb-4">
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fDdn">Date de naissance <span class="required">*</span></label>
          <input class="form-control" id="fDdn" type="date">
        </div>
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fSexe">Sexe <span class="required">*</span></label>
          <select class="form-control" id="fSexe">
            <option value="">Sélectionner…</option>
            <option value="M">Masculin</option>
            <option value="F">Féminin</option>
          </select>
        </div>
      </div>
      <div class="form-group mb-4">
        <label class="form-label" for="fNin">NIN (Numéro d'Identification Nationale)</label>
        <div class="input-group">
          <span class="input-group-icon"><i class="ti ti-id" aria-hidden="true"></i></span>
          <input class="form-control" id="fNin" type="text" placeholder="CM-AAAA-XXXXX">
        </div>
        <div class="form-hint">Généré automatiquement si laissé vide</div>
      </div>
      <div class="grid cols-2 gap-4 mb-4">
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fQuartier">Quartier</label>
          <select class="form-control" id="fQuartier">
            <option value="">Sélectionner…</option>
            <option>Bastos</option>
            <option>Biyem-Assi</option>
            <option>Melen</option>
            <option>Nlongkak</option>
          </select>
        </div>
        <div class="form-group" style="margin:0">
          <label class="form-label" for="fSituation">Situation matrimoniale</label>
          <select class="form-control" id="fSituation">
            <option value="">Sélectionner…</option>
            <option>Célibataire</option>
            <option>Marié(e)</option>
            <option>Divorcé(e)</option>
            <option>Veuf/Veuve</option>
          </select>
        </div>
      </div>
      <div class="form-group" style="margin-bottom:0">
        <label class="form-label" for="fEmail">Email</label>
        <div class="input-group">
          <span class="input-group-icon"><i class="ti ti-mail" aria-hidden="true"></i></span>
          <input class="form-control" id="fEmail" type="email" placeholder="email@exemple.cm">
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <button class="btn btn-ghost" onclick="closeModal('citoyen')">Annuler</button>
      <button class="btn btn-primary" onclick="saveForm()">
        <i class="ti ti-check" aria-hidden="true"></i> Enregistrer
      </button>
    </div>
  </div>
</div>

<!-- Toast Container -->
<div class="toast-container" id="toastContainer" role="status" aria-live="polite"></div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/resources/js/app-core.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/charts-init.js"></script>

