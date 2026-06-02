<%-- Administrative Requests List View --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Demandes administratives</h1>
    <p>12 demandes en attente de traitement</p>
  </div>
</div>

<div class="alert alert-warning mb-4">
  <i class="ti ti-alert-triangle" aria-hidden="true"></i>
  <div>
    <div class="alert-title">3 demandes urgentes</div>
    Ces demandes dépassent le délai légal de traitement de 48h.
  </div>
</div>

<div class="card">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>N° Suivi</th>
          <th>Type de demande</th>
          <th>Demandeur</th>
          <th>Date soumission</th>
          <th>Priorité</th>
          <th>Statut</th>
          <th style="text-align:right">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px">DEM-2025-0089</code></td>
          <td>Certificat de résidence</td>
          <td><strong>Amina Fouda</strong></td>
          <td>02/06/2025 15:30</td>
          <td><span class="badge badge-neutral">Normale</span></td>
          <td><span class="badge badge-warning">En cours</span></td>
          <td style="text-align:right">
            <a href="${pageContext.request.contextPath}/demande/traitement?id=89" class="btn btn-primary btn-sm btn-icon" title="Traiter"><i class="ti ti-check" aria-hidden="true"></i></a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

