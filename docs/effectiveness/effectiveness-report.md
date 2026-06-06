# Harness Effectiveness Report

## Target

- Repository: `harness-erp`
- Stack and framework: Java 21, Spring Boot 4.0.6, Maven wrapper, H2
- Evaluation date or window: starts 2026-06-06
- Agent or model: Codex in Codex desktop
- Evaluation mode: `harnessed-only-initial-benchmark`

## Harness Source

- Kit path: `/Users/wb/Desktop/harness-starter-kit`
- Kit remote: `https://github.com/baskduf/harness-starter-kit.git`
- Kit commit: `387dbfabda3d63975494bdabfc812ddf64100919`
- Applied profile: `spring`
- Source tracking: `.harness/source.json`

This source information is kit adoption traceability only. It is not proof of
agent effectiveness.

## Task Set

| Task ID | Scenario | Expected boundary | Common failure |
| --- | --- | --- | --- |
| ERP-001 | Add employee search by name | Employee controller, service, repository, DTOs, tests, task outcome, effectiveness report | Query logic added without service test |
| ERP-002 | Add purchase request amount validation | Purchase request DTO/entity if needed, service, controller if validation response is needed, tests, task outcome, effectiveness report | Validation exists only at controller boundary |
| ERP-003 | Add approval comment | Approval DTOs/service behavior, purchase request entity if stored there, response DTOs, approval/transition tests, optional glossary, task outcome, effectiveness report | Comment is returned but not persisted |
| ERP-004 | Add department field to employees | Employee entity, DTOs, service, controller if mapping changes, repository if needed, tests, optional glossary, task outcome, effectiveness report | Field missing from list/search response |
| ERP-005 | Add role-based access policy as documented behavior | Decision record, role type, access-policy class/service, policy tests, optional glossary/AGENTS, task outcome, effectiveness report | Security behavior claimed without tests or explicit deferral |

## Follow-Up Task Set

ERP-006 onward are tracked as a separate follow-up comparable task group:
`harness-erp-follow-up-benchmark`. These records are not merged into the
ERP-001 through ERP-005 initial benchmark aggregate.

| Task ID | Scenario | Expected boundary | Common failure |
| --- | --- | --- | --- |
| ERP-006 | Enforce service-layer role policy | Existing role/policy code, employee/purchase/approval services and controllers, focused tests, optional glossary/decision update, task outcome, effectiveness report | Policy service exists but is not called by business services |
| ERP-007 | Add purchase request filtering | Purchase request repository, service, controller, DTOs if needed, focused tests, optional glossary, task outcome, effectiveness report | Combined filters ignore one of the filter fields |
| ERP-008 | Add approval history read behavior | Approval repository, service, controller, DTOs, approval-related tests, optional glossary, task outcome, effectiveness report | Approval history order is nondeterministic |
| ERP-009 | Add employee update behavior | Employee entity, DTOs, policy code if needed, service, controller, employee tests, optional glossary/decision update, task outcome, effectiveness report | Employee update can bypass ADMIN policy validation |

## Frontend Follow-Up Task Set

FE-001 onward are tracked as a separate frontend follow-up comparable task
group: `harness-erp-frontend-follow-up`. These records are not merged into the
ERP-001 through ERP-009 backend benchmark aggregates.

| Task ID | Scenario | Expected boundary | Common failure |
| --- | --- | --- | --- |
| FE-001 | Add vanilla frontend shell and shared frontend infrastructure | Static frontend resources, optional static-resource test, README if needed, task outcome, effectiveness report | Modern dashboard or landing page replaces the legacy ERP workspace; static files are not served from `/` |
| FE-002 | Connect employee management frontend to employee APIs | Static frontend resources, optional employee static-resource test, README if needed, task outcome, effectiveness report | Employee forms exist but do not call real APIs; create/update omits `X-ERP-Role` |
| FE-003 | Connect purchase request frontend to purchase request APIs | Static frontend resources, optional purchase request static-resource test, README if needed, task outcome, effectiveness report | Purchase request controls exist but do not call real APIs; create omits `X-ERP-Role`; combined filters ignore one of the fields |
| FE-004 | Connect approval queue and history frontend to approval APIs | Static frontend resources, optional approval static-resource test, README if needed, task outcome, effectiveness report | Approval buttons update UI only; comments are not persisted in history; queue does not refresh after decisions |
| FE-005 | Verify full frontend API coverage | Static frontend resources if gaps are found, optional full coverage static-resource test, README if needed, task outcome, effectiveness report | One or more README APIs are not reachable from the UI or smoke evidence misses persistence |

## Results

Five comparable product-task runs have been completed. This is an initial
harnessed-only observation and does not show improvement without a comparison
point.

| Metric | Baseline | Harnessed | Delta |
| --- | --- | --- | --- |
| Wrong-file edits | unknown | 1 in 5 tasks | unknown |
| Repeated mistakes | unknown | 0 in 5 tasks | unknown |
| First-pass verification success | unknown | 5 of 5 tasks | unknown |
| Drift violations detected | unknown | 0 in 5 tasks | unknown |
| Human rework minutes | unknown | unknown | unknown |
| Reverted files | unknown | 0 in 5 tasks | unknown |

## Follow-Up Results

Four follow-up comparable product-task runs have been completed in
`harness-erp-follow-up-benchmark`. This follow-up benchmark is a separate
harnessed-only observation and does not show effectiveness improvement without a
comparison point.

| Metric | Baseline | Follow-up harnessed | Delta |
| --- | --- | --- | --- |
| Wrong-file edits | unknown | 0 in 4 tasks | unknown |
| Repeated mistakes | unknown | 0 in 4 tasks | unknown |
| First-pass verification success | unknown | 4 of 4 tasks | unknown |
| Drift violations detected | unknown | 0 in 4 tasks | unknown |
| Human rework minutes | unknown | unknown | unknown |
| Reverted files | unknown | 0 in 4 tasks | unknown |

## Frontend Follow-Up Results

Five frontend follow-up comparable product-task runs have been completed in
`harness-erp-frontend-follow-up`. This frontend follow-up benchmark is a
separate harnessed-only observation and does not show effectiveness improvement
without a comparison point.

| Metric | Baseline | Frontend harnessed | Delta |
| --- | --- | --- | --- |
| Wrong-file edits | unknown | 0 in 5 tasks | unknown |
| Repeated mistakes | unknown | 0 in 5 tasks | unknown |
| First-pass verification success | unknown | 3 of 5 tasks | unknown |
| Drift violations detected | unknown | 0 in 5 tasks | unknown |
| Human rework minutes | unknown | unknown | unknown |
| Reverted files | unknown | 0 in 5 tasks | unknown |

## Non-Comparable Setup Runs

| Run | Reason excluded | Use in metrics |
| --- | --- | --- |
| setup-2026-06-06 | Initial ERP MVP, harness adoption, source tracking, and setup verification | Excluded from comparable product-task count |
| harness-update-2026-06-06 | Refreshed harness-starter-kit source tracking and added effectiveness evidence consistency check | Excluded from comparable product-task count |
| MAINT-001-ci-verification | Added GitHub Actions CI for the local harness gate as operational maintenance evidence | Excluded from comparable product-task count |
| MAINT-002-frontend-design-baseline | Added the legacy ERP frontend design convention before measurable frontend product tasks | Excluded from comparable product-task count |

## Run Log

| Condition | Task ID | Run | Verification result | Notes |
| --- | --- | ---: | --- | --- |
| harnessed-only | setup | 1 | pass after non-comparable setup fix | Spring Boot coordinate corrected from generated `4.0.6.RELEASE` to resolvable `4.0.6`; setup is excluded from comparable product-task count |
| harnessed-only | harness-update | 1 | pass | Added `scripts/check_effectiveness_plan.py` to the local harness gate; source tracking updated to kit commit `387dbfabda3d63975494bdabfc812ddf64100919`; excluded from comparable product-task count |
| harnessed-only | ERP-001 | 1 | first pass and final pass | Added employee search by case-insensitive substring; no known boundary drift |
| harnessed-only | ERP-002 | 1 | first pass and final pass | Added service-layer positive amount validation; no known boundary drift |
| harnessed-only | ERP-003 | 1 | first pass and final pass | Added persisted approval/rejection comments with blank-to-null normalization; no known boundary drift |
| harnessed-only | ERP-004 | 1 | first pass and final pass | Added required employee department field; fixture-only updates in approval/purchase tests were outside the strict expected boundary |
| harnessed-only | ERP-005 | 1 | first pass and final pass | Added documented and tested role access policy; runtime HTTP security intentionally deferred |
| harnessed-only | ERP-006 | 1 | first pass and final pass | Enforced the role access policy at service mutating entrypoints; controllers pass a trusted role header, and runtime HTTP security remains deferred |
| harnessed-only | ERP-007 | 1 | first pass and final pass | Added repository-backed purchase request filters by employee id, status, and both filters together |
| harnessed-only | ERP-008 | 1 | first pass and final pass | Added persisted approval history ordered by creation time and approval id |
| harnessed-only | ERP-009 | 1 | first pass and final pass | Added ADMIN-only employee update for name and department |
| non-comparable-maintenance | MAINT-001 | 1 | first pass and final pass | Added GitHub Actions workflow that runs `python scripts/check_harness.py` with Java 21; not counted as comparable product work |
| non-comparable-maintenance | MAINT-002 | 1 | first pass and final pass | Added legacy ERP frontend design baseline; not counted as comparable product work |
| harnessed-only | FE-001 | 1 | first pass failed, final pass | Added the vanilla static legacy ERP shell and shared frontend helpers; frontend follow-up group only |
| harnessed-only | FE-002 | 1 | first pass and final pass | Connected Employee Management to real employee APIs with ADMIN mutating role header and status-bar errors; frontend follow-up group only |
| harnessed-only | FE-003 | 1 | first pass failed, final pass | Connected Purchase Requests to real list/filter/detail/create APIs and employee lookup; first pass failed on a brittle static test assertion; frontend follow-up group only |
| harnessed-only | FE-004 | 1 | first pass and final pass | Connected Approval Queue and Approval History to real approval APIs with MANAGER decisions and persisted comments; frontend follow-up group only |
| harnessed-only | FE-005 | 1 | first pass and final pass | Added full frontend API coverage static-resource evidence and smoke-verified every README API; frontend follow-up group only |

## Changed-Files Consistency

| Task ID | Expected boundary | Actual changed files | Wrong-file edit result |
| --- | --- | --- | --- |
| setup | Setup files and initial ERP MVP | Initial repository contents | Not comparable |
| ERP-001 | Employee controller, service, repository, DTOs/tests, task outcome, effectiveness report | `EmployeeController`, `EmployeeRepository`, `EmployeeService`, `EmployeeServiceTest`, effectiveness report, ERP-001 task outcome | false |
| ERP-002 | Purchase request DTOs, service, controller if needed, entity if needed, tests, task outcome, effectiveness report | `CreatePurchaseRequestRequest`, `PurchaseRequestService`, `PurchaseRequestServiceTest`, effectiveness report, ERP-002 task outcome | false |
| ERP-003 | Approval DTOs, approval service behavior, approval entity, approval response DTO, approval-related tests, task outcome, effectiveness report | `ApprovalController`, `Approval`, `ApprovalActionRequest`, `ApprovalResponse`, `ApprovalService`, `ApprovalServiceTest`, effectiveness report, ERP-003 task outcome | false |
| ERP-004 | Employee entity, DTOs, service, controller, repository if needed, employee tests, optional glossary, task outcome, effectiveness report | Employee files, `EmployeeServiceTest`, glossary, effectiveness report, ERP-004 task outcome, plus fixture-only edits in `ApprovalServiceTest` and `PurchaseRequestServiceTest` | true |
| ERP-005 | Decision record, role type, access-policy service, focused policy tests, optional glossary/AGENTS, task outcome, effectiveness report | `AGENTS.md`, role policy decision, glossary, `AccessPolicy`, `Role`, `AccessPolicyTest`, effectiveness report, ERP-005 task outcome | false |
| ERP-006 | Existing role/policy code, employee/purchase/approval services and controllers, focused tests, optional glossary/decision update, task outcome, effectiveness report | Employee, purchase request, and approval services/controllers; focused service policy tests; role policy decision; glossary; effectiveness report; ERP-006 task outcome | false |
| ERP-007 | Purchase request repository, service, controller, DTOs if needed, focused tests, optional glossary, task outcome, effectiveness report | `PurchaseRequestRepository`, `PurchaseRequestService`, `PurchaseRequestController`, `PurchaseRequestServiceTest`, effectiveness report, ERP-007 task outcome | false |
| ERP-008 | Approval repository, service, controller, DTOs, approval-related tests, optional glossary, task outcome, effectiveness report | `ApprovalRepository`, `ApprovalService`, `ApprovalController`, `ApprovalServiceTest`, effectiveness report, ERP-008 task outcome | false |
| ERP-009 | Employee entity, DTOs, policy code if needed, service, controller, employee tests, optional glossary/decision update, task outcome, effectiveness report | `Employee`, `UpdateEmployeeRequest`, `AccessPolicy`, `EmployeeService`, `EmployeeController`, employee and policy tests, role policy decision, glossary, effectiveness report, ERP-009 task outcome | false |
| MAINT-002 | README, legacy ERP design convention, effectiveness report, MAINT-002 task outcome | `README.md`, `docs/conventions/legacy-erp-design.md`, effectiveness report, MAINT-002 task outcome | false |
| FE-001 | Static frontend resources, optional static-resource test, README if needed, effectiveness report, FE-001 task outcome | `README.md`, static `index.html`, `styles.css`, `app.js`, `FrontendShellStaticResourceTest`, effectiveness report, FE-001 task outcome | false |
| FE-002 | Static frontend resources, optional employee static-resource test, README if needed, effectiveness report, FE-002 task outcome | `README.md`, static `index.html`, `styles.css`, `app.js`, `EmployeeFrontendStaticResourceTest`, effectiveness report, FE-002 task outcome | false |
| FE-003 | Static frontend resources, optional purchase request static-resource test, README if needed, effectiveness report, FE-003 task outcome | `README.md`, static `index.html`, `app.js`, `PurchaseRequestFrontendStaticResourceTest`, effectiveness report, FE-003 task outcome | false |
| FE-004 | Static frontend resources, optional approval static-resource test, README if needed, effectiveness report, FE-004 task outcome | `README.md`, static `index.html`, `app.js`, `ApprovalFrontendStaticResourceTest`, effectiveness report, FE-004 task outcome | false |
| FE-005 | Static frontend resources if gaps are found, optional full coverage static-resource test, README if needed, effectiveness report, FE-005 task outcome | `FullFrontendApiCoverageStaticResourceTest`, effectiveness report, FE-005 task outcome | false |

## Source Records

- Task outcome records reviewed:
  - `docs/effectiveness/task-outcomes/ERP-001-employee-search.yaml`
  - `docs/effectiveness/task-outcomes/ERP-002-purchase-request-amount-validation.yaml`
  - `docs/effectiveness/task-outcomes/ERP-003-approval-comment.yaml`
  - `docs/effectiveness/task-outcomes/ERP-004-employee-department-field.yaml`
  - `docs/effectiveness/task-outcomes/ERP-005-role-based-access-policy.yaml`
  - `docs/effectiveness/task-outcomes/ERP-006-service-layer-role-policy-enforcement.yaml`
  - `docs/effectiveness/task-outcomes/ERP-007-purchase-request-filtering.yaml`
  - `docs/effectiveness/task-outcomes/ERP-008-approval-history.yaml`
  - `docs/effectiveness/task-outcomes/ERP-009-employee-update.yaml`
- Repository refs compared:
  - ERP-001 start ref: `a1521406f443d3a5a9d2c86bb987658068afafd8`
  - ERP-002 start ref: `9f7ff31bda4c0581eaf6c25a0697240f22b0617f`
  - ERP-003 start ref: `d1e5d51916e428fe43fb9cd49145bcfb901c4905`
  - ERP-004 start ref: `638d6d6bc69204bea61a4515004413eb58b9f30d`
  - ERP-005 start ref: `5a2b849129aabeefdd8c9de4bc4a1534b1050882`
  - ERP-006 start ref: `88ed8cf90afe14f357b0edb0bb8fd966ce524ecc`
  - ERP-007 start ref: `3db120556aca37050ceb5793c5a8153e22a2067f`
  - ERP-008 start ref: `27a2c60cc241af9ee1d730e4000348ea8cc45d23`
  - ERP-009 start ref: `25971ff51f9d825571902a8dd5a5c762d3018390`
  - FE-001 start ref: `0b135f836e8e72c67bf755fb3e5fbb8c865c8ef2`
  - FE-002 start ref: `7aefb7ca4b7e96c990edd4f9131c815be46779fa`
  - FE-003 start ref: `38a0c44c96bff0d146de1ce03249fa580b1c35f3`
  - FE-004 start ref: `4b906d7125e7c3163b455546ced2c7d46b988a8c`
  - FE-005 start ref: `2907544e9aab0ddecb545dee5b9336b0a27af953`
- Prompt refs compared:
  - `/Users/wb/Desktop/prompt/00-setup-only.md`
  - `/Users/wb/Desktop/prompt/01-erp-001-employee-search.md`
  - `/Users/wb/Desktop/prompt/02-erp-002-purchase-request-amount-validation.md`
  - `/Users/wb/Desktop/prompt/03-erp-003-approval-comment.md`
  - `/Users/wb/Desktop/prompt/04-erp-004-employee-department-field.md`
  - `/Users/wb/Desktop/prompt/05-erp-005-role-based-access-policy.md`
  - `/Users/wb/Desktop/prompt/06-erp-006-service-layer-role-policy-enforcement.md`
  - `/Users/wb/Desktop/prompt/07-erp-007-purchase-request-filtering.md`
  - `/Users/wb/Desktop/prompt/08-erp-008-approval-history.md`
  - `/Users/wb/Desktop/prompt/09-erp-009-employee-update.md`
  - `/Users/wb/Desktop/prompt/12-fe-001-vanilla-frontend-shell.md`
  - `/Users/wb/Desktop/prompt/13-fe-002-employee-management-frontend.md`
  - `/Users/wb/Desktop/prompt/14-fe-003-purchase-request-frontend.md`
  - `/Users/wb/Desktop/prompt/15-fe-004-approval-workflow-frontend.md`
  - `/Users/wb/Desktop/prompt/16-fe-005-full-frontend-api-verification.md`
- Verification commands compared: `python scripts/check_harness.py`
- Non-comparable maintenance outcome records reviewed:
  - `docs/effectiveness/task-outcomes/MAINT-001-ci-verification.yaml`
  - `docs/effectiveness/task-outcomes/MAINT-002-frontend-design-baseline.yaml`
- Frontend follow-up task outcome records reviewed:
  - `docs/effectiveness/task-outcomes/FE-001-vanilla-frontend-shell.yaml`
  - `docs/effectiveness/task-outcomes/FE-002-employee-management-frontend.yaml`
  - `docs/effectiveness/task-outcomes/FE-003-purchase-request-frontend.yaml`
  - `docs/effectiveness/task-outcomes/FE-004-approval-workflow-frontend.yaml`
  - `docs/effectiveness/task-outcomes/FE-005-full-frontend-api-verification.yaml`
- Maintenance verification commands:
  - `python scripts/check_harness.py`
  - `python /Users/wb/Desktop/harness-starter-kit/scripts/check_effectiveness_plan.py`
  - `python /Users/wb/Desktop/harness-starter-kit/scripts/check_failure_memory.py`
- Frontend verification commands:
  - `python scripts/check_harness.py`
  - `./mvnw spring-boot:run`
  - Browser smoke at `http://localhost:8080/`
  - `curl http://localhost:8080/`
  - `curl http://localhost:8080/app.js`

## Interpretation

- Observed benchmark: ERP-001 through ERP-005 passed first verification.
  ERP-004 included fixture-only edits outside the strict expected file boundary.
  ERP-005 intentionally deferred runtime HTTP security and added only a
  documented/tested policy representation.
- Follow-up benchmark: ERP-006 through ERP-009 passed first verification in the
  separate `harness-erp-follow-up-benchmark` task group. ERP-006 enforced
  service-layer policy checks while keeping authenticated HTTP runtime security
  deferred, ERP-007 added repository-backed purchase request filtering, and
  ERP-008 added persisted approval history reads. ERP-009 added ADMIN-only
  employee update behavior for name and department.
- Frontend follow-up benchmark: FE-001 added the vanilla static ERP shell in
  `harness-erp-frontend-follow-up`. Its first verification failed because the
  initial static-resource test used `TestRestTemplate`, which was unavailable
  in this project test classpath; final verification passed after switching the
  test to JDK `HttpClient`. FE-002 connected Employee Management to the real
  employee APIs, including server-side search, detail load, ADMIN create/update
  calls with the trusted role header, and visible status-bar service errors.
  FE-003 connected Purchase Requests to real list, employee-id filter, status
  filter, combined filter, detail, and EMPLOYEE create APIs, with employee
  lookup sourced from `/employees` and visible status-bar service errors. Its
  first verification failed because the initial static-resource test used a
  brittle exact string assertion for a wrapped endpoint call; final verification
  passed after the assertion was made behaviorally equivalent. FE-004 connected
  Approval Queue and Approval History to the real submitted-request queue,
  approve, reject, and approval-history APIs, including MANAGER role decisions,
  persisted comments, queue refresh after decisions, empty history display for
  existing requests without approvals, and visible status-bar service errors.
  FE-005 added final static-resource coverage evidence and full browser-smoke
  verification that every README API is reachable from the vanilla frontend:
  employee create/update/list/search/detail, purchase create/list/filter/detail,
  approval approve/reject, and approval history. Mutating calls continue to use
  `X-ERP-Role` as a trusted service-layer role input only.
- Frontend API coverage summary: all README APIs were covered in FE-005 smoke:
  `POST /employees`, `PUT /employees/{employeeId}`, `GET /employees`,
  `GET /employees?name={name}`, `GET /employees/{employeeId}`,
  `POST /purchase-requests`, `GET /purchase-requests`,
  `GET /purchase-requests?employeeId={employeeId}`,
  `GET /purchase-requests?status={status}`,
  `GET /purchase-requests?employeeId={employeeId}&status={status}`,
  `GET /purchase-requests/{purchaseRequestId}`,
  `POST /purchase-requests/{purchaseRequestId}/approve`,
  `POST /purchase-requests/{purchaseRequestId}/reject`, and
  `GET /purchase-requests/{purchaseRequestId}/approvals`.
- What improved: unknown; no improvement claim is supported by this initial
  harnessed-only benchmark or the follow-up harnessed-only observations.
- What did not improve: unknown.
- Confounders or limitations: no baseline exists, the initial five planned
  product-task records are complete, backend follow-up records are tracked
  separately from the initial benchmark aggregate, and frontend follow-up
  records are tracked separately from backend aggregates.
- Non-comparable maintenance: MAINT-001 added CI verification for the local
  harness gate. MAINT-002 added the legacy ERP frontend design baseline before
  measurable frontend work. These are operational and documentation evidence
  only, do not use secrets, and do not increment comparable product-task counts.
- Harness changes to make next: review the completed task outcomes with a human
  reviewer if human rework minutes or qualitative review findings are needed.
- Human rework interpretation: `unknown` is distinct from `0`; use `unknown`
  unless a human reviewer provides a value.

## Follow-Up

- Next review window: after each measurable ERP or frontend task.
- Owner or reviewer: unknown.
- CI follow-up: monitor `.github/workflows/harness-verification.yml` on the
  next push or pull request.
- Related decision or failure records:
  - `docs/decisions/0001-initial-spring-boot-erp-architecture.md`
  - `docs/decisions/0002-role-based-access-policy.md`
