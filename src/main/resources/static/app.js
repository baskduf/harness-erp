(function () {
  "use strict";

  class ErpApiError extends Error {
    constructor(message, response, payload) {
      super(message);
      this.name = "ErpApiError";
      this.status = response.status;
      this.payload = payload;
    }
  }

  function readStoredRole() {
    try {
      return window.localStorage && window.localStorage.getItem("harnessErpRole");
    } catch (error) {
      return null;
    }
  }

  function writeStoredRole(role) {
    try {
      if (window.localStorage) {
        window.localStorage.setItem("harnessErpRole", role);
      }
    } catch (error) {
      // Storage can be unavailable in restricted browser contexts.
    }
  }

  const state = {
    role: readStoredRole() || "EMPLOYEE",
    activeView: "employees",
    selectedRows: new Map()
  };

  function setStatus(message, level) {
    const statusBar = document.getElementById("statusBar");
    const statusRole = document.getElementById("statusRole");
    if (!statusBar) {
      return;
    }

    statusBar.classList.remove("is-success", "is-warning", "is-error");
    if (level) {
      statusBar.classList.add("is-" + level);
    }
    if (statusRole) {
      statusRole.textContent = state.role;
    }
    statusBar.textContent = "User: local | Role: " + state.role
      + " | Company: HARNESS | DB: H2 | Status: " + message;
  }

  function tryParseJson(text) {
    if (!text) {
      return null;
    }
    try {
      return JSON.parse(text);
    } catch (error) {
      return { message: text };
    }
  }

  async function parseJsonResponse(response) {
    const text = await response.text();
    const payload = tryParseJson(text);
    if (!response.ok) {
      const message = payload && (payload.message || payload.error)
        ? payload.message || payload.error
        : "Request failed with status " + response.status;
      throw new ErpApiError(message, response, payload);
    }
    return payload;
  }

  async function apiRequest(path, options) {
    const requestOptions = options || {};
    const url = new URL(path, window.location.origin);
    const headers = new Headers(requestOptions.headers || {});
    headers.set("Accept", "application/json");
    headers.set("X-ERP-Role", requestOptions.role || state.role);

    if (requestOptions.query) {
      Object.entries(requestOptions.query).forEach(function (entry) {
        const key = entry[0];
        const value = entry[1];
        if (value !== undefined && value !== null && value !== "") {
          url.searchParams.set(key, value);
        }
      });
    }

    let body = requestOptions.body;
    if (body && typeof body !== "string") {
      headers.set("Content-Type", "application/json");
      body = JSON.stringify(body);
    }

    try {
      return await parseJsonResponse(await window.fetch(url, {
        method: requestOptions.method || "GET",
        headers: headers,
        body: body
      }));
    } catch (error) {
      if (error instanceof ErpApiError) {
        setStatus(error.message, "error");
      } else {
        setStatus("Network request failed.", "error");
      }
      throw error;
    }
  }

  function clearSelection(group) {
    const previousKey = state.selectedRows.get(group);
    if (previousKey) {
      const previousRow = document.querySelector("[data-row-key=\"" + previousKey + "\"]");
      if (previousRow) {
        previousRow.classList.remove("is-selected");
      }
    }
    state.selectedRows.delete(group);
  }

  function selectRow(group, rowKey) {
    clearSelection(group);
    const row = document.querySelector("[data-row-key=\"" + rowKey + "\"]");
    if (!row) {
      return;
    }
    row.classList.add("is-selected");
    state.selectedRows.set(group, rowKey);
    setStatus("Row selected.", "success");
  }

  function formatId(prefix, value) {
    if (value === undefined || value === null || value === "") {
      return "unknown";
    }
    const numberValue = Number(value);
    const formatted = Number.isNaN(numberValue)
      ? String(value)
      : String(numberValue).padStart(5, "0");
    return prefix + "-" + formatted;
  }

  function formatAmount(value) {
    if (value === undefined || value === null || value === "") {
      return "unknown";
    }
    const numberValue = Number(value);
    if (Number.isNaN(numberValue)) {
      return "unknown";
    }
    return new Intl.NumberFormat("en-US", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numberValue);
  }

  function formatDate(value) {
    if (!value) {
      return "unknown";
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return "unknown";
    }
    return date.toISOString().slice(0, 10);
  }

  function formatStatus(value) {
    if (!value) {
      return "unknown";
    }
    return String(value).replace(/_/g, " ").toUpperCase();
  }

  function showView(viewName) {
    state.activeView = viewName;
    document.querySelectorAll("[data-view]").forEach(function (tab) {
      const isActive = tab.dataset.view === viewName;
      tab.classList.toggle("is-active", isActive);
      tab.setAttribute("aria-selected", isActive ? "true" : "false");
    });
    document.querySelectorAll("[data-view-panel]").forEach(function (panel) {
      panel.classList.toggle("is-active", panel.dataset.viewPanel === viewName);
    });
    setStatus("Workspace ready.", "success");
  }

  function handleCommand(command) {
    const messages = {
      search: "No search criteria submitted.",
      reset: "Criteria reset.",
      new: "No entry form is active.",
      save: "No editable record is active.",
      delete: "No row is selected.",
      approve: "No submitted request is selected.",
      reject: "No submitted request is selected.",
      export: "Export is not available.",
      print: "Print is not available.",
      close: "Workspace ready."
    };
    setStatus(messages[command] || "Workspace ready.", command === "reset" ? "success" : "warning");
  }

  function bindTabs() {
    document.querySelectorAll("[data-view]").forEach(function (tab) {
      tab.addEventListener("click", function () {
        showView(tab.dataset.view);
      });
    });
  }

  function bindToolbar() {
    document.querySelectorAll("[data-command]").forEach(function (button) {
      button.addEventListener("click", function () {
        handleCommand(button.dataset.command);
      });
    });
  }

  function bindRoleSelector() {
    const selector = document.getElementById("roleSelector");
    if (!selector) {
      return;
    }
    selector.value = state.role;
    selector.addEventListener("change", function () {
      state.role = selector.value;
      writeStoredRole(state.role);
      setStatus("Role input changed.", "success");
    });
  }

  function bindSelectableRows() {
    document.querySelectorAll("[data-row-key]").forEach(function (row) {
      row.addEventListener("click", function () {
        const panel = row.closest("[data-view-panel]");
        const group = panel ? panel.dataset.viewPanel : "workspace";
        selectRow(group, row.dataset.rowKey);
      });
    });
  }

  function init() {
    bindTabs();
    bindToolbar();
    bindRoleSelector();
    bindSelectableRows();
    showView(state.activeView);
    setStatus("Ready.", "success");
  }

  window.HarnessERP = {
    apiRequest: apiRequest,
    clearSelection: clearSelection,
    selectRow: selectRow,
    setStatus: setStatus,
    state: state,
    format: {
      id: formatId,
      amount: formatAmount,
      date: formatDate,
      status: formatStatus
    }
  };

  document.addEventListener("DOMContentLoaded", init);
}());
