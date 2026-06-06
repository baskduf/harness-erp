# ERP Domain Glossary

- Employee: a person who can create purchase requests.
- Department: the required organizational group recorded on an employee.
- Purchase request: a request from an employee to purchase an item or service.
- Purchase request amount: the monetary amount requested for a purchase.
- Draft purchase request: a request that has not been submitted for approval.
- Submitted purchase request: a request that can be approved or rejected.
- Approved purchase request: a submitted request accepted by an approver.
- Rejected purchase request: a submitted request declined by an approver.
- Approval: the record of approving or rejecting a submitted purchase request.
- Approval decision: either `APPROVED` or `REJECTED`.
- Role: a documented access-policy identity supplied through `X-ERP-Role`.
- Admin role: may create and update employees.
- Employee role: may create purchase requests.
- Manager role: may approve or reject purchase requests.
- Access policy: the tested policy that maps roles to allowed ERP operations at
  both Spring Security request authorization and service mutating entrypoints.
- Role-header authentication: the local benchmark mechanism that maps
  `X-ERP-Role` to Spring Security authorities; it is not production-grade user
  identity, password login, or SSO.
