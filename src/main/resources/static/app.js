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
    selectedRows: new Map(),
    employees: {
      rows: [],
      selectedId: null,
      lastSearchName: ""
    }
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

  function employeeElements() {
    return {
      rows: document.getElementById("employeeRows"),
      recordCount: document.getElementById("employeeRecordCount"),
      searchName: document.getElementById("employeeSearchName"),
      detailId: document.getElementById("employeeDetailId"),
      name: document.getElementById("employeeName"),
      department: document.getElementById("employeeDepartment"),
      mode: document.getElementById("employeeDetailMode"),
      form: document.getElementById("employeeForm")
    };
  }

  function messageFromError(error) {
    if (error instanceof ErpApiError) {
      return error.message;
    }
    return "Request failed.";
  }

  function appendCell(row, text, className, colSpan) {
    const cell = document.createElement("td");
    cell.textContent = text;
    if (className) {
      cell.className = className;
    }
    if (colSpan) {
      cell.colSpan = colSpan;
    }
    row.appendChild(cell);
    return cell;
  }

  function renderEmployeePlaceholder(message) {
    const elements = employeeElements();
    if (!elements.rows) {
      return;
    }
    const row = document.createElement("tr");
    appendCell(row, "-", "erp-center");
    appendCell(row, "unknown", "erp-code");
    appendCell(row, message, null, 3);
    elements.rows.replaceChildren(row);
    if (elements.recordCount) {
      elements.recordCount.textContent = "0 records";
    }
  }

  function renderEmployeeRows(employees) {
    const elements = employeeElements();
    if (!elements.rows) {
      return;
    }
    if (!employees.length) {
      renderEmployeePlaceholder("No rows found.");
      return;
    }

    const fragment = document.createDocumentFragment();
    employees.forEach(function (employee) {
      const row = document.createElement("tr");
      const rowKey = "employee-" + employee.id;
      row.dataset.rowKey = rowKey;
      row.dataset.employeeId = String(employee.id);
      if (state.employees.selectedId === employee.id) {
        row.classList.add("is-selected");
      }
      appendCell(row, ">", "erp-center");
      appendCell(row, formatId("EMP", employee.id), "erp-code");
      appendCell(row, employee.name || "unknown");
      appendCell(row, employee.department || "unknown");
      appendCell(row, "ACTIVE", "erp-code");
      row.addEventListener("click", function () {
        loadEmployeeDetail(employee.id);
      });
      fragment.appendChild(row);
    });
    elements.rows.replaceChildren(fragment);
    if (elements.recordCount) {
      elements.recordCount.textContent = employees.length + (employees.length === 1 ? " record" : " records");
    }
  }

  function setEmployeeDetail(employee) {
    const elements = employeeElements();
    state.employees.selectedId = employee ? employee.id : null;
    if (elements.detailId) {
      elements.detailId.value = employee ? formatId("EMP", employee.id) : "new";
    }
    if (elements.name) {
      elements.name.value = employee ? employee.name || "" : "";
    }
    if (elements.department) {
      elements.department.value = employee ? employee.department || "" : "";
    }
    if (elements.mode) {
      elements.mode.value = employee ? "Update" : "Create";
    }
    document.querySelectorAll("#employeeRows [data-row-key]").forEach(function (row) {
      row.classList.toggle("is-selected", employee && row.dataset.employeeId === String(employee.id));
    });
  }

  function employeePayload() {
    const elements = employeeElements();
    const name = elements.name ? elements.name.value.trim() : "";
    const department = elements.department ? elements.department.value.trim() : "";
    if (!name) {
      setStatus("Required value is missing. [Name]", "error");
      return null;
    }
    if (!department) {
      setStatus("Required value is missing. [Department]", "error");
      return null;
    }
    return { name: name, department: department };
  }

  function currentEmployeeSearchName() {
    const elements = employeeElements();
    return elements.searchName ? elements.searchName.value.trim() : "";
  }

  async function loadEmployees(options) {
    const searchOptions = options || {};
    const requestedName = searchOptions.name !== undefined
      ? searchOptions.name.trim()
      : currentEmployeeSearchName();
    state.employees.lastSearchName = requestedName;
    const employees = await apiRequest("/employees", {
      query: requestedName ? { name: requestedName } : undefined
    });
    state.employees.rows = Array.isArray(employees) ? employees : [];
    renderEmployeeRows(state.employees.rows);
    if (!searchOptions.silent) {
      setStatus("Search complete. " + state.employees.rows.length + " records found.", "success");
    }
    return state.employees.rows;
  }

  async function loadEmployeeDetail(employeeId, options) {
    const detailOptions = options || {};
    const employee = await apiRequest("/employees/" + encodeURIComponent(employeeId));
    setEmployeeDetail(employee);
    selectRow("employees", "employee-" + employee.id);
    if (!detailOptions.silent) {
      setStatus("Detail loaded.", "success");
    }
    return employee;
  }

  async function createEmployee() {
    const payload = employeePayload();
    if (!payload) {
      return;
    }
    try {
      const employee = await apiRequest("/employees", {
        method: "POST",
        body: payload
      });
      await loadEmployees({ name: state.employees.lastSearchName, silent: true });
      await loadEmployeeDetail(employee.id, { silent: true });
      setStatus("Saved.", "success");
    } catch (error) {
      setStatus(messageFromError(error), "error");
    }
  }

  async function updateEmployee() {
    const selectedId = state.employees.selectedId;
    if (!selectedId) {
      setStatus("No row is selected.", "error");
      return;
    }
    const payload = employeePayload();
    if (!payload) {
      return;
    }
    try {
      const employee = await apiRequest("/employees/" + encodeURIComponent(selectedId), {
        method: "PUT",
        body: payload
      });
      await loadEmployees({ name: state.employees.lastSearchName, silent: true });
      setEmployeeDetail(employee);
      selectRow("employees", "employee-" + employee.id);
      setStatus("Saved.", "success");
    } catch (error) {
      try {
        await loadEmployeeDetail(selectedId, { silent: true });
      } catch (restoreError) {
        // Keep the original service error visible if detail restore fails.
      }
      setStatus(messageFromError(error), "error");
    }
  }

  function resetEmployeeSearch() {
    const elements = employeeElements();
    if (elements.searchName) {
      elements.searchName.value = "";
    }
    state.employees.lastSearchName = "";
    loadEmployees({ name: "" }).catch(function (error) {
      setStatus(messageFromError(error), "error");
    });
  }

  function clearEmployeeForm() {
    setEmployeeDetail(null);
    setStatus("Ready.", "success");
  }

  async function handleEmployeeAction(action) {
    if (action === "list") {
      const elements = employeeElements();
      if (elements.searchName) {
        elements.searchName.value = "";
      }
      await loadEmployees({ name: "" });
    } else if (action === "search") {
      await loadEmployees();
    } else if (action === "reset") {
      resetEmployeeSearch();
    } else if (action === "new") {
      clearEmployeeForm();
    } else if (action === "create") {
      await createEmployee();
    } else if (action === "update") {
      await updateEmployee();
    } else if (action === "reload") {
      if (!state.employees.selectedId) {
        setStatus("No row is selected.", "error");
        return;
      }
      await loadEmployeeDetail(state.employees.selectedId);
    }
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

  async function handleCommand(command) {
    if (state.activeView === "employees") {
      if (command === "search") {
        await handleEmployeeAction("search");
        return;
      }
      if (command === "new") {
        await handleEmployeeAction("new");
        return;
      }
      if (command === "save") {
        await (state.employees.selectedId ? updateEmployee() : createEmployee());
        return;
      }
      if (command === "close") {
        setStatus("Workspace ready.", "success");
        return;
      }
    }

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
        handleCommand(button.dataset.command).catch(function (error) {
          setStatus(messageFromError(error), "error");
        });
      });
    });
  }

  function bindEmployeeScreen() {
    const elements = employeeElements();
    if (elements.form) {
      elements.form.addEventListener("submit", function (event) {
        event.preventDefault();
      });
    }
    document.querySelectorAll("[data-employee-action]").forEach(function (button) {
      button.addEventListener("click", function () {
        handleEmployeeAction(button.dataset.employeeAction).catch(function (error) {
          setStatus(messageFromError(error), "error");
        });
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
    bindEmployeeScreen();
    bindRoleSelector();
    bindSelectableRows();
    showView(state.activeView);
    loadEmployees({ name: "", silent: true }).then(function () {
      setStatus("Ready.", "success");
    }).catch(function (error) {
      setStatus(messageFromError(error), "error");
    });
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
