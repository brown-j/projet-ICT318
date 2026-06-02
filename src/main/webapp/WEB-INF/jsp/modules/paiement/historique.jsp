<%-- Payment History View --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Paiements</h1>
    <p>Historique comptable des recettes</p>
  </div>
</div>

<div class="alert alert-success mb-4">
  <i class="ti ti-circle-check" aria-hidden="true"></i>
  <div>
    <div class="alert-title">Caisse du jour : 142 500 FCFA</div>
    23 transactions enregistrées aujourd'hui.
  </div>
</div>

<div class="card">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>N° Reçu</th>
          <th>Demande</th>
          <th>Montant</th>
          <th>Mode</th>
          <th>Caissier</th>
          <th>Date/Heure</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px">REC-2025-0412</code></td>
          <td>Extrait de naissance</td>
          <td><strong>2 500 FCFA</strong></td>
          <td><span class="badge badge-primary">Espèces</span></td>
          <td>Koffi N.</td>
          <td>02/06/2025 10:45</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

