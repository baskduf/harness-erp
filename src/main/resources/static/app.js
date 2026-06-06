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
    },
    purchaseRequests: {
      rows: [],
      selectedId: null,
      lastFilters: {
        employeeId: "",
        status: ""
      },
      employeeLookup: []
    },
    approvalQueue: {
      rows: [],
      selectedId: null,
      selectedRequest: null,
      lastStatus: "SUBMITTED"
    },
    approvalHistory: {
      rows: [],
      lastPurchaseRequestId: ""
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

  function formatDateTime(value) {
    if (!value) {
      return "unknown";
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return "unknown";
    }
    return date.toISOString().replace("T", " ").slice(0, 19);
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

  function purchaseElements() {
    return {
      rows: document.getElementById("purchaseRows"),
      recordCount: document.getElementById("purchaseRecordCount"),
      filterEmployeeId: document.getElementById("purchaseFilterEmployeeId"),
      filterStatus: document.getElementById("purchaseFilterStatus"),
      detailId: document.getElementById("purchaseDetailId"),
      employeeId: document.getElementById("purchaseEmployeeId"),
      employeeLookup: document.getElementById("purchaseEmployeeLookup"),
      employeeName: document.getElementById("purchaseEmployeeName"),
      description: document.getElementById("purchaseDescription"),
      amount: document.getElementById("purchaseAmount"),
      status: document.getElementById("purchaseStatus"),
      mode: document.getElementById("purchaseDetailMode"),
      form: document.getElementById("purchaseForm")
    };
  }

  function renderPurchasePlaceholder(message) {
    const elements = purchaseElements();
    if (!elements.rows) {
      return;
    }
    const row = document.createElement("tr");
    appendCell(row, "-", "erp-center");
    appendCell(row, "unknown", "erp-code");
    appendCell(row, message, null, 4);
    elements.rows.replaceChildren(row);
    if (elements.recordCount) {
      elements.recordCount.textContent = "0 records";
    }
  }

  function renderPurchaseRows(purchaseRequests) {
    const elements = purchaseElements();
    if (!elements.rows) {
      return;
    }
    if (!purchaseRequests.length) {
      renderPurchasePlaceholder("No rows found.");
      return;
    }

    const fragment = document.createDocumentFragment();
    purchaseRequests.forEach(function (purchaseRequest) {
      const row = document.createElement("tr");
      const rowKey = "purchase-" + purchaseRequest.id;
      row.dataset.rowKey = rowKey;
      row.dataset.purchaseRequestId = String(purchaseRequest.id);
      if (state.purchaseRequests.selectedId === purchaseRequest.id) {
        row.classList.add("is-selected");
      }
      appendCell(row, ">", "erp-center");
      appendCell(row, formatId("PR", purchaseRequest.id), "erp-code");
      appendCell(row, formatId("EMP", purchaseRequest.employeeId), "erp-code");
      appendCell(row, purchaseRequest.description || "unknown");
      appendCell(row, formatAmount(purchaseRequest.amount), "erp-amount");
      appendCell(row, formatStatus(purchaseRequest.status), "erp-code");
      row.addEventListener("click", function () {
        loadPurchaseRequestDetail(purchaseRequest.id);
      });
      fragment.appendChild(row);
    });
    elements.rows.replaceChildren(fragment);
    if (elements.recordCount) {
      elements.recordCount.textContent = purchaseRequests.length
        + (purchaseRequests.length === 1 ? " record" : " records");
    }
  }

  function purchaseFiltersFromInputs() {
    const elements = purchaseElements();
    const employeeId = elements.filterEmployeeId ? elements.filterEmployeeId.value.trim() : "";
    const status = elements.filterStatus ? elements.filterStatus.value : "";
    return {
      employeeId: employeeId,
      status: status
    };
  }

  function purchaseQueryFromFilters(filters) {
    const query = {};
    if (filters.employeeId) {
      query.employeeId = Number(filters.employeeId);
    }
    if (filters.status) {
      query.status = filters.status;
    }
    return query;
  }

  async function loadPurchaseRequests(options) {
    const requestOptions = options || {};
    const filters = requestOptions.filters || purchaseFiltersFromInputs();
    state.purchaseRequests.lastFilters = {
      employeeId: filters.employeeId || "",
      status: filters.status || ""
    };
    const query = purchaseQueryFromFilters(state.purchaseRequests.lastFilters);
    const purchaseRequests = await apiRequest("/purchase-requests", {
      query: query
    });
    state.purchaseRequests.rows = Array.isArray(purchaseRequests) ? purchaseRequests : [];
    renderPurchaseRows(state.purchaseRequests.rows);
    if (!requestOptions.silent) {
      setStatus("Search complete. " + state.purchaseRequests.rows.length + " records found.", "success");
    }
    return state.purchaseRequests.rows;
  }

  function setPurchaseDetail(purchaseRequest) {
    const elements = purchaseElements();
    state.purchaseRequests.selectedId = purchaseRequest ? purchaseRequest.id : null;
    if (elements.detailId) {
      elements.detailId.value = purchaseRequest ? formatId("PR", purchaseRequest.id) : "new";
    }
    if (elements.employeeId) {
      elements.employeeId.value = purchaseRequest ? purchaseRequest.employeeId : "";
    }
    if (elements.employeeLookup) {
      elements.employeeLookup.value = purchaseRequest ? String(purchaseRequest.employeeId) : "";
    }
    if (elements.employeeName) {
      elements.employeeName.value = purchaseRequest ? purchaseRequest.employeeName || "unknown" : "unknown";
    }
    if (elements.description) {
      elements.description.value = purchaseRequest ? purchaseRequest.description || "" : "";
    }
    if (elements.amount) {
      elements.amount.value = purchaseRequest ? Number(purchaseRequest.amount).toFixed(2) : "";
    }
    if (elements.status) {
      elements.status.value = purchaseRequest
        && (purchaseRequest.status === "DRAFT" || purchaseRequest.status === "SUBMITTED")
        ? purchaseRequest.status
        : "";
    }
    if (elements.mode) {
      elements.mode.value = purchaseRequest ? "Detail" : "Create";
    }
    document.querySelectorAll("#purchaseRows [data-row-key]").forEach(function (row) {
      row.classList.toggle(
        "is-selected",
        purchaseRequest && row.dataset.purchaseRequestId === String(purchaseRequest.id)
      );
    });
  }

  async function loadPurchaseRequestDetail(purchaseRequestId, options) {
    const detailOptions = options || {};
    const purchaseRequest = await apiRequest(
      "/purchase-requests/" + encodeURIComponent(purchaseRequestId)
    );
    setPurchaseDetail(purchaseRequest);
    selectRow("purchaseRequests", "purchase-" + purchaseRequest.id);
    if (!detailOptions.silent) {
      setStatus("Detail loaded.", "success");
    }
    return purchaseRequest;
  }

  async function loadPurchaseEmployeeLookup() {
    const elements = purchaseElements();
    if (!elements.employeeLookup) {
      return [];
    }
    const employees = await apiRequest("/employees");
    state.purchaseRequests.employeeLookup = Array.isArray(employees) ? employees : [];

    const currentValue = elements.employeeLookup.value;
    const options = [new Option("Select", "")];
    state.purchaseRequests.employeeLookup.forEach(function (employee) {
      options.push(new Option(
        formatId("EMP", employee.id) + " | " + employee.name,
        String(employee.id)
      ));
    });
    elements.employeeLookup.replaceChildren.apply(elements.employeeLookup, options);
    if (currentValue) {
      elements.employeeLookup.value = currentValue;
    }
    return state.purchaseRequests.employeeLookup;
  }

  function purchasePayload() {
    const elements = purchaseElements();
    const employeeId = elements.employeeId ? elements.employeeId.value.trim() : "";
    const description = elements.description ? elements.description.value.trim() : "";
    const amountText = elements.amount ? elements.amount.value.trim() : "";
    const amount = Number(amountText);
    if (!employeeId) {
      setStatus("Required value is missing. [Employee ID]", "error");
      return null;
    }
    if (!description) {
      setStatus("Required value is missing. [Description]", "error");
      return null;
    }
    if (!amountText || Number.isNaN(amount) || amount <= 0) {
      setStatus("Required value is missing. [Amount]", "error");
      return null;
    }
    const payload = {
      employeeId: Number(employeeId),
      description: description,
      amount: amount
    };
    if (elements.status && elements.status.value) {
      payload.status = elements.status.value;
    }
    return payload;
  }

  async function createPurchaseRequest() {
    const payload = purchasePayload();
    if (!payload) {
      return;
    }
    try {
      const purchaseRequest = await apiRequest("/purchase-requests", {
        method: "POST",
        body: payload
      });
      await loadPurchaseRequests({ filters: state.purchaseRequests.lastFilters, silent: true });
      await loadPurchaseRequestDetail(purchaseRequest.id, { silent: true });
      setStatus("Saved.", "success");
    } catch (error) {
      setStatus(messageFromError(error), "error");
    }
  }

  function clearPurchaseForm() {
    setPurchaseDetail(null);
    setStatus("Ready.", "success");
  }

  function resetPurchaseFilters() {
    const elements = purchaseElements();
    if (elements.filterEmployeeId) {
      elements.filterEmployeeId.value = "";
    }
    if (elements.filterStatus) {
      elements.filterStatus.value = "";
    }
    loadPurchaseRequests({
      filters: {
        employeeId: "",
        status: ""
      }
    }).catch(function (error) {
      setStatus(messageFromError(error), "error");
    });
  }

  async function handlePurchaseAction(action) {
    if (action === "list") {
      const elements = purchaseElements();
      if (elements.filterEmployeeId) {
        elements.filterEmployeeId.value = "";
      }
      if (elements.filterStatus) {
        elements.filterStatus.value = "";
      }
      await loadPurchaseRequests({
        filters: {
          employeeId: "",
          status: ""
        }
      });
    } else if (action === "search") {
      await loadPurchaseRequests();
    } else if (action === "reset") {
      resetPurchaseFilters();
    } else if (action === "new") {
      clearPurchaseForm();
    } else if (action === "create") {
      await createPurchaseRequest();
    } else if (action === "reload") {
      if (!state.purchaseRequests.selectedId) {
        setStatus("No row is selected.", "error");
        return;
      }
      await loadPurchaseRequestDetail(state.purchaseRequests.selectedId);
    }
  }

  function approvalQueueElements() {
    return {
      rows: document.getElementById("approvalQueueRows"),
      recordCount: document.getElementById("approvalQueueRecordCount"),
      status: document.getElementById("approvalQueueStatus"),
      detailRequestId: document.getElementById("approvalRequestId"),
      detailEmployeeId: document.getElementById("approvalEmployeeId"),
      detailAmount: document.getElementById("approvalAmount"),
      detailStatus: document.getElementById("approvalStatus"),
      comment: document.getElementById("approvalComment")
    };
  }

  function approvalHistoryElements() {
    return {
      purchaseRequestId: document.getElementById("historyPurchaseRequestId"),
      rows: document.getElementById("historyRows"),
      recordCount: document.getElementById("historyRecordCount")
    };
  }

  function renderApprovalQueuePlaceholder(message) {
    const elements = approvalQueueElements();
    if (!elements.rows) {
      return;
    }
    const row = document.createElement("tr");
    appendCell(row, "-", "erp-center");
    appendCell(row, "unknown", "erp-code");
    appendCell(row, message, null, 4);
    elements.rows.replaceChildren(row);
    if (elements.recordCount) {
      elements.recordCount.textContent = "0 records";
    }
  }

  function renderApprovalQueueRows(purchaseRequests) {
    const elements = approvalQueueElements();
    if (!elements.rows) {
      return;
    }
    if (!purchaseRequests.length) {
      renderApprovalQueuePlaceholder("No submitted requests found.");
      return;
    }

    const fragment = document.createDocumentFragment();
    purchaseRequests.forEach(function (purchaseRequest) {
      const row = document.createElement("tr");
      const rowKey = "approval-" + purchaseRequest.id;
      row.dataset.rowKey = rowKey;
      row.dataset.approvalRequestId = String(purchaseRequest.id);
      if (state.approvalQueue.selectedId === purchaseRequest.id) {
        row.classList.add("is-selected");
      }
      appendCell(row, ">", "erp-center");
      appendCell(row, formatId("PR", purchaseRequest.id), "erp-code");
      appendCell(row, formatId("EMP", purchaseRequest.employeeId), "erp-code");
      appendCell(row, purchaseRequest.description || "unknown");
      appendCell(row, formatAmount(purchaseRequest.amount), "erp-amount");
      appendCell(row, formatStatus(purchaseRequest.status), "erp-code");
      row.addEventListener("click", function () {
        loadApprovalQueueDetail(purchaseRequest.id);
      });
      fragment.appendChild(row);
    });
    elements.rows.replaceChildren(fragment);
    if (elements.recordCount) {
      elements.recordCount.textContent = purchaseRequests.length
        + (purchaseRequests.length === 1 ? " record" : " records");
    }
  }

  function setApprovalQueueDetail(purchaseRequest) {
    const elements = approvalQueueElements();
    state.approvalQueue.selectedId = purchaseRequest ? purchaseRequest.id : null;
    state.approvalQueue.selectedRequest = purchaseRequest || null;
    if (elements.detailRequestId) {
      elements.detailRequestId.value = purchaseRequest ? formatId("PR", purchaseRequest.id) : "none";
    }
    if (elements.detailEmployeeId) {
      elements.detailEmployeeId.value = purchaseRequest ? formatId("EMP", purchaseRequest.employeeId) : "unknown";
    }
    if (elements.detailAmount) {
      elements.detailAmount.value = purchaseRequest ? formatAmount(purchaseRequest.amount) : "unknown";
    }
    if (elements.detailStatus) {
      elements.detailStatus.value = purchaseRequest ? formatStatus(purchaseRequest.status) : "unknown";
    }
    if (elements.comment && !purchaseRequest) {
      elements.comment.value = "";
    }
    document.querySelectorAll("#approvalQueueRows [data-row-key]").forEach(function (row) {
      row.classList.toggle(
        "is-selected",
        purchaseRequest && row.dataset.approvalRequestId === String(purchaseRequest.id)
      );
    });
  }

  async function loadApprovalQueue(options) {
    const requestOptions = options || {};
    const elements = approvalQueueElements();
    const status = elements.status && elements.status.value ? elements.status.value : "SUBMITTED";
    state.approvalQueue.lastStatus = status;
    const purchaseRequests = await apiRequest("/purchase-requests", {
      query: { status: status }
    });
    state.approvalQueue.rows = Array.isArray(purchaseRequests) ? purchaseRequests : [];
    renderApprovalQueueRows(state.approvalQueue.rows);
    if (!state.approvalQueue.rows.some(function (request) {
      return request.id === state.approvalQueue.selectedId;
    })) {
      setApprovalQueueDetail(null);
    }
    if (!requestOptions.silent) {
      setStatus("Approval queue loaded. " + state.approvalQueue.rows.length + " submitted records found.", "success");
    }
    return state.approvalQueue.rows;
  }

  async function loadApprovalQueueDetail(purchaseRequestId, options) {
    const detailOptions = options || {};
    const purchaseRequest = await apiRequest(
      "/purchase-requests/" + encodeURIComponent(purchaseRequestId)
    );
    setApprovalQueueDetail(purchaseRequest);
    selectRow("approvalQueue", "approval-" + purchaseRequest.id);
    if (!detailOptions.silent) {
      setStatus("Approval detail loaded.", "success");
    }
    return purchaseRequest;
  }

  function approvalCommentPayload() {
    const elements = approvalQueueElements();
    const comment = elements.comment ? elements.comment.value.trim() : "";
    return comment ? { comment: comment } : null;
  }

  async function submitApprovalDecision(decision) {
    const purchaseRequestId = state.approvalQueue.selectedId;
    if (!purchaseRequestId) {
      setStatus("No submitted request is selected.", "error");
      return;
    }
    const options = {
      method: "POST"
    };
    const payload = approvalCommentPayload();
    if (payload) {
      options.body = payload;
    }
    const action = decision === "APPROVED" ? "approve" : "reject";
    try {
      const approval = await apiRequest(
        "/purchase-requests/" + encodeURIComponent(purchaseRequestId) + "/" + action,
        options
      );
      const historyElements = approvalHistoryElements();
      if (historyElements.purchaseRequestId) {
        historyElements.purchaseRequestId.value = String(purchaseRequestId);
      }
      state.approvalHistory.lastPurchaseRequestId = String(purchaseRequestId);
      await loadApprovalHistory({ purchaseRequestId: purchaseRequestId, silent: true });
      await loadApprovalQueue({ silent: true });
      setApprovalQueueDetail(null);
      setStatus(formatStatus(approval.decision) + ".", "success");
      return approval;
    } catch (error) {
      setStatus(messageFromError(error), "error");
      return null;
    }
  }

  function renderApprovalHistoryPlaceholder(message) {
    const elements = approvalHistoryElements();
    if (!elements.rows) {
      return;
    }
    const row = document.createElement("tr");
    appendCell(row, "-", "erp-center");
    appendCell(row, "unknown", "erp-code");
    appendCell(row, message, null, 4);
    elements.rows.replaceChildren(row);
    if (elements.recordCount) {
      elements.recordCount.textContent = "0 records";
    }
  }

  function renderApprovalHistoryRows(approvals) {
    const elements = approvalHistoryElements();
    if (!elements.rows) {
      return;
    }
    if (!approvals.length) {
      renderApprovalHistoryPlaceholder("No approval history found.");
      return;
    }

    const fragment = document.createDocumentFragment();
    approvals.forEach(function (approval) {
      const row = document.createElement("tr");
      row.dataset.rowKey = "history-" + approval.id;
      row.dataset.approvalId = String(approval.id);
      appendCell(row, ">", "erp-center");
      appendCell(row, formatId("APR", approval.id), "erp-code");
      appendCell(row, formatId("PR", approval.purchaseRequestId), "erp-code");
      appendCell(row, formatStatus(approval.decision), "erp-code");
      appendCell(row, approval.comment || "unknown");
      appendCell(row, formatDateTime(approval.createdAt), "erp-code");
      fragment.appendChild(row);
    });
    elements.rows.replaceChildren(fragment);
    if (elements.recordCount) {
      elements.recordCount.textContent = approvals.length + (approvals.length === 1 ? " record" : " records");
    }
  }

  function currentHistoryPurchaseRequestId() {
    const elements = approvalHistoryElements();
    return elements.purchaseRequestId ? elements.purchaseRequestId.value.trim() : "";
  }

  async function loadApprovalHistory(options) {
    const historyOptions = options || {};
    const purchaseRequestId = historyOptions.purchaseRequestId !== undefined
      ? String(historyOptions.purchaseRequestId).trim()
      : currentHistoryPurchaseRequestId();
    if (!purchaseRequestId) {
      renderApprovalHistoryPlaceholder("Enter a purchase request id.");
      if (!historyOptions.silent) {
        setStatus("Required value is missing. [Request ID]", "error");
      }
      return [];
    }
    state.approvalHistory.lastPurchaseRequestId = purchaseRequestId;
    const approvals = await apiRequest(
      "/purchase-requests/" + encodeURIComponent(purchaseRequestId) + "/approvals"
    );
    state.approvalHistory.rows = Array.isArray(approvals) ? approvals : [];
    renderApprovalHistoryRows(state.approvalHistory.rows);
    if (!historyOptions.silent) {
      setStatus("Approval history loaded. " + state.approvalHistory.rows.length + " records found.", "success");
    }
    return state.approvalHistory.rows;
  }

  function resetApprovalHistory() {
    const elements = approvalHistoryElements();
    if (elements.purchaseRequestId) {
      elements.purchaseRequestId.value = "";
    }
    state.approvalHistory.lastPurchaseRequestId = "";
    renderApprovalHistoryPlaceholder("Enter a purchase request id.");
    setStatus("Criteria reset.", "success");
  }

  async function handleApprovalAction(action) {
    if (action === "queue-search") {
      await loadApprovalQueue();
    } else if (action === "queue-reset") {
      const elements = approvalQueueElements();
      if (elements.status) {
        elements.status.value = "SUBMITTED";
      }
      setApprovalQueueDetail(null);
      await loadApprovalQueue();
    } else if (action === "approve") {
      await submitApprovalDecision("APPROVED");
    } else if (action === "reject") {
      await submitApprovalDecision("REJECTED");
    } else if (action === "reload") {
      if (!state.approvalQueue.selectedId) {
        setStatus("No submitted request is selected.", "error");
        return;
      }
      await loadApprovalQueueDetail(state.approvalQueue.selectedId);
    }
  }

  async function handleHistoryAction(action) {
    if (action === "search") {
      await loadApprovalHistory();
    } else if (action === "reset") {
      resetApprovalHistory();
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
    if (viewName === "purchaseRequests") {
      loadPurchaseEmployeeLookup().catch(function (error) {
        setStatus(messageFromError(error), "error");
      });
      loadPurchaseRequests({ filters: state.purchaseRequests.lastFilters, silent: true }).catch(function (error) {
        setStatus(messageFromError(error), "error");
      });
    }
    if (viewName === "approvalQueue") {
      loadApprovalQueue({ silent: true }).catch(function (error) {
        setStatus(messageFromError(error), "error");
      });
    }
    if (viewName === "approvalHistory") {
      const elements = approvalHistoryElements();
      if (elements.purchaseRequestId && state.approvalHistory.lastPurchaseRequestId) {
        elements.purchaseRequestId.value = state.approvalHistory.lastPurchaseRequestId;
        loadApprovalHistory({ purchaseRequestId: state.approvalHistory.lastPurchaseRequestId, silent: true })
          .catch(function (error) {
            setStatus(messageFromError(error), "error");
          });
      } else {
        renderApprovalHistoryPlaceholder("Enter a purchase request id.");
      }
    }
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
    if (state.activeView === "purchaseRequests") {
      if (command === "search") {
        await handlePurchaseAction("search");
        return;
      }
      if (command === "new") {
        await handlePurchaseAction("new");
        return;
      }
      if (command === "save") {
        await handlePurchaseAction("create");
        return;
      }
      if (command === "close") {
        setStatus("Workspace ready.", "success");
        return;
      }
    }
    if (state.activeView === "approvalQueue") {
      if (command === "search") {
        await handleApprovalAction("queue-search");
        return;
      }
      if (command === "approve") {
        await handleApprovalAction("approve");
        return;
      }
      if (command === "reject") {
        await handleApprovalAction("reject");
        return;
      }
      if (command === "close") {
        setStatus("Workspace ready.", "success");
        return;
      }
    }
    if (state.activeView === "approvalHistory") {
      if (command === "search") {
        await handleHistoryAction("search");
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

  function bindPurchaseScreen() {
    const elements = purchaseElements();
    if (elements.form) {
      elements.form.addEventListener("submit", function (event) {
        event.preventDefault();
      });
    }
    if (elements.employeeLookup) {
      elements.employeeLookup.addEventListener("change", function () {
        if (elements.employeeId) {
          elements.employeeId.value = elements.employeeLookup.value;
        }
      });
    }
    document.querySelectorAll("[data-purchase-action]").forEach(function (button) {
      button.addEventListener("click", function () {
        handlePurchaseAction(button.dataset.purchaseAction).catch(function (error) {
          setStatus(messageFromError(error), "error");
        });
      });
    });
  }

  function bindApprovalQueueScreen() {
    document.querySelectorAll("[data-approval-action]").forEach(function (button) {
      button.addEventListener("click", function () {
        handleApprovalAction(button.dataset.approvalAction).catch(function (error) {
          setStatus(messageFromError(error), "error");
        });
      });
    });
  }

  function bindApprovalHistoryScreen() {
    document.querySelectorAll("[data-history-action]").forEach(function (button) {
      button.addEventListener("click", function () {
        handleHistoryAction(button.dataset.historyAction).catch(function (error) {
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
    bindPurchaseScreen();
    bindApprovalQueueScreen();
    bindApprovalHistoryScreen();
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
      dateTime: formatDateTime,
      status: formatStatus
    }
  };

  document.addEventListener("DOMContentLoaded", init);
}());
