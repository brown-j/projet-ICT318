<%-- Acte Civil List View --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Actes civils</h1>
    <p>Gestion des actes d'état civil (naissances, mariages, décès)</p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter</button>
    <button class="btn btn-primary btn-sm" onclick="openModal('acte')"><i class="ti ti-plus" aria-hidden="true"></i> Nouvel acte</button>
  </div>
</div>

<div class="card">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>N° Acte</th>
          <th>Type</th>
          <th>Citoyen</th>
          <th>Date événement</th>
          <th>Lieu</th>
          <th>Statut</th>
          <th style="text-align:right">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px">ACT-2025-0203</code></td>
          <td><span class="badge badge-primary">Mariage</span></td>
          <td><strong>Kotto & Nguemo</strong></td>
          <td>01/06/2025</td>
          <td>Cathédrale de Yaoundé</td>
          <td><span class="badge badge-success"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> Délivré</span></td>
          <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
              <button class="btn btn-ghost btn-sm btn-icon" title="Voir PDF"><i class="ti ti-file-pdf" aria-hidden="true"></i></button>
              <button class="btn btn-ghost btn-sm btn-icon" title="Éditer"><i class="ti ti-edit" aria-hidden="true"></i></button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

