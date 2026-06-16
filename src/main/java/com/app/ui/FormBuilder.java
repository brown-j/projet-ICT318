package com.app.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Moteur de génération de formulaires dynamiques en Java.
 */
public class FormBuilder {

    public enum FormItemType {
        TEXT,
        EMAIL,
        PASSWORD,
        NUMBER,
        DATE,
        TEL,
        HIDDEN,
        SELECT,
        FILE;

        public String getHtmlType() {
            return name().toLowerCase();
        }
    }

    public static class FormContainer {
        private String action;
        private List<FormRow> rows = new ArrayList<>();
        private boolean isReadOnly = false;
        private boolean isMultipart = false;

        public FormContainer(String action) {
            this.action = action;
        }

        public FormContainer addRow(FormRow row) {
            this.rows.add(row);
            return this;
        }

        public FormContainer readOnly(boolean readOnly) {
            this.isReadOnly = readOnly;
            return this;
        }

        public FormContainer multipart() {
            this.isMultipart = true;
            return this;
        }

        public String render() {
            StringBuilder html = new StringBuilder();
            html.append("<form action='").append(action).append("' method='post'");
            if (this.isMultipart) {
                html.append(" enctype='multipart/form-data'");
            }
            html.append(">");
            html.append("<div class=\"modal-body\">\n");

            for (FormRow row : rows) {
                html.append(row.render(this.isReadOnly));
            }

            html.append("</div>\n");
            html.append("<div class=\"modal-footer\">\n");

            if (this.isReadOnly) {
                html.append(
                        "  <button type=\"button\" class=\"btn btn-secondary\" onclick=\"closeModal('global')\">Fermer</button>\n");
            } else {
                html.append(
                        "  <button type=\"button\" class=\"btn btn-ghost\" onclick=\"closeModal('global')\">Annuler</button>\n");
                html.append(
                        "  <button type=\"submit\" class=\"btn btn-primary\"><i class=\"ti ti-check\"></i> Enregistrer</button>\n");
            }

            html.append("</div>\n");
            html.append("</form>\n");

            return html.toString();
        }
    }

    public static class FormRow {
        private List<FormItem> items;

        private FormRow(FormItem... items) {
            this.items = Arrays.asList(items);
        }

        public static FormRow single(FormItem item) {
            return new FormRow(item);
        }

        public static FormRow duo(FormItem left, FormItem right) {
            return new FormRow(left, right);
        }

        public String render(boolean formReadOnly) {
            StringBuilder html = new StringBuilder();
            if (items.size() == 1 && items.get(0).type == FormItemType.HIDDEN) {
                return items.get(0).render(formReadOnly);
            }

            String gridClass = items.size() == 2 ? "grid cols-2 gap-4 mb-4" : "form-group mb-4";
            html.append("<div class=\"").append(gridClass).append("\">\n");
            for (FormItem item : items) {
                html.append(item.render(formReadOnly));
            }
            html.append("</div>\n");
            return html.toString();
        }
    }

    public static class FormItem {
        private FormItemType type;
        private String name;
        private String label;
        private String value = "";
        private String placeholder = "";
        private boolean required = false;
        private boolean isReadOnly = false;
        private boolean isSearchable = false;
        private Enum<?>[] selectOptions;
        private Map<String, String> dynamicOptions;

        private FormItem(FormItemType type, String name, String label) {
            this.type = type;
            this.name = name;
            this.label = label;
        }

        public static FormItem input(FormItemType type, String name, String label) {
            return new FormItem(type, name, label);
        }

        public static FormItem hidden(String name, String value) {
            FormItem item = new FormItem(FormItemType.HIDDEN, name, "");
            return item.value(value);
        }

        public static FormItem select(String name, String label, Enum<?>[] options) {
            FormItem item = new FormItem(FormItemType.SELECT, name, label);
            item.selectOptions = options;
            return item;
        }

        public static FormItem select(String name, String label, Map<String, String> options) {
            FormItem item = new FormItem(FormItemType.SELECT, name, label);
            item.dynamicOptions = options;
            return item;
        }

        public FormItem required() {
            this.required = true;
            return this;
        }

        public FormItem readonly(boolean b) {
            this.isReadOnly = b;
            return this;
        }

        public FormItem value(String value) {
            this.value = (value != null) ? value : "";
            return this;
        }

        public FormItem placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public FormItem searchable() {
            this.isSearchable = true;
            return this;
        }

        public String render(boolean formReadOnly) {
            if (type == FormItemType.HIDDEN) {
                return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">\n";
            }

            boolean effectiveReadOnly = this.isReadOnly || formReadOnly;

            StringBuilder html = new StringBuilder();
            html.append("  <div class=\"form-group\" style=\"margin:0\">\n");
            html.append("    <label class=\"form-label\" for=\"").append(name).append("\">").append(label);

            if (required && !effectiveReadOnly)
                html.append(" <span class=\"required\" style=\"color:var(--c-danger-500)\">*</span>");
            html.append("</label>\n");

            if (type == FormItemType.FILE && value != null && !value.trim().isEmpty()) {
                html.append(
                        "    <div class=\"file-exist-indicator\" style=\"margin-bottom: var(--space-1, 6px); font-size: var(--text-xs, 12px); color: var(--c-success-600, #16a34a); display: flex; align-items: center; gap: 4px;\">\n");
                html.append("        <i class=\"ti ti-file-check\" style=\"font-size: 15px;\"></i>\n");
                html.append(
                        "        <span>Fichier actuel chargé : <code style=\"font-family: var(--font-mono); font-size: 11px;\">")
                        .append(value).append("</code></span>\n");
                html.append("    </div>\n");
            }

            if (type == FormItemType.SELECT) {
                html.append("    <select class=\"form-control\" id=\"").append(name).append("\" name=\"").append(name)
                        .append("\" ").append(required && !effectiveReadOnly ? "required" : "")
                        .append(effectiveReadOnly ? " disabled" : "")
                        .append(this.isSearchable && !effectiveReadOnly ? " data-searchable=\"true\"" : "")
                        .append(">\n");

                html.append("      <option value=\"\">Sélectionner…</option>\n");

                if (selectOptions != null) {
                    for (Enum<?> opt : selectOptions) {
                        String selected = opt.name().equals(value) ? "selected" : "";
                        html.append("      <option value=\"").append(opt.name()).append("\" ").append(selected)
                                .append(">").append(opt.name()).append("</option>\n");
                    }
                } else if (dynamicOptions != null) {
                    for (Map.Entry<String, String> entry : dynamicOptions.entrySet()) {
                        String selected = entry.getKey().equals(value) ? "selected" : "";
                        html.append("      <option value=\"").append(entry.getKey()).append("\" ").append(selected)
                                .append(">").append(entry.getValue()).append("</option>\n");
                    }
                }
                html.append("    </select>\n");

                // 💡 CORRECTION AUTOMATIQUE : Injection du champ caché si le SELECT est
                // désactivé
                if (effectiveReadOnly) {
                    html.append("    <input type=\"hidden\" name=\"").append(name).append("\" value=\"").append(value)
                            .append("\">\n");
                }
            } else {
                html.append("    <input class=\"form-control\" type=\"").append(type.getHtmlType()).append("\" id=\"")
                        .append(name).append("\" name=\"").append(name)
                        .append("\" value=\"").append(value).append("\" placeholder=\"").append(placeholder)
                        .append("\" ").append(required && !effectiveReadOnly ? "required" : "")
                        .append(effectiveReadOnly ? " readonly" : "").append(">\n");
            }

            html.append("  </div>\n");
            return html.toString();
        }
    }
}