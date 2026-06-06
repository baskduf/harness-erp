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
- Role: a documented access-policy identity used by the minimal ERP policy.
- Admin role: may create employees.
- Employee role: may create purchase requests.
- Manager role: may approve or reject purchase requests.
- Access policy: the tested code-level policy that maps roles to allowed ERP
  operations; it is not HTTP runtime security.
