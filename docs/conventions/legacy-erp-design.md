# Legacy ERP UI Design Specification

## Purpose

Harness ERP should use a deliberately old internal-business-system style if a
web UI is added. The target feel is a dense Windows 2000-era ERP client, closer
to SAP GUI, Oracle E-Business Suite Forms, JD Edwards, and AS/400 terminal
workflows than to a modern SaaS dashboard.

The UI should look dated, but the task flow should be clear, fast, and
operator-friendly. Prioritize search, data grids, keyboard-friendly forms, and
status feedback over marketing polish.

This specification covers presentation only. Runtime authorization for mutating
endpoints is handled by Spring Security as of ERP-010 using the `X-ERP-Role`
role header, with service-layer policy checks retained as defense in depth. Do
not claim production-grade user identity, password login, SSO, or per-user
authorization exists unless later work implements and tests it.

## Design Principles

- Build for trained internal operators, not anonymous prospects.
- Prefer dense information, compact controls, and predictable layouts.
- Use form-and-grid workflows as the default page shape.
- Make codes, dates, amounts, statuses, and audit state easy to scan.
- Keep visual hierarchy functional: window title, menu, toolbar, criteria,
  results, details, status.
- Avoid marketing patterns such as hero sections, oversized cards, large
  illustrations, decorative gradients, and animated onboarding.

## Visual Direction

Use a classic desktop application look:

- Flat gray application background.
- Beveled panels and inset fields.
- Square corners; maximum radius is `2px`.
- Thin hard borders instead of soft shadows.
- Small controls with short labels.
- Tahoma-style typography.
- Minimal animation; state changes should be immediate.

## Color Tokens

```css
:root {
  --erp-bg: #d4d0c8;
  --erp-panel: #ece9d8;
  --erp-panel-dark: #c0c0c0;
  --erp-border-light: #ffffff;
  --erp-border-dark: #808080;
  --erp-border-deep: #404040;

  --erp-titlebar: #000080;
  --erp-titlebar-text: #ffffff;

  --erp-input-bg: #ffffff;
  --erp-input-required: #fff7a8;
  --erp-input-readonly: #e5e5e5;

  --erp-table-header: #dcdcdc;
  --erp-table-row: #ffffff;
  --erp-table-row-alt: #f5f5f5;
  --erp-table-selected: #0a246a;

  --erp-success: #008000;
  --erp-warning: #b36b00;
  --erp-error: #b00000;
  --erp-link: #0000ee;
}
```

## Typography

- Default font: `Tahoma`, `Arial`, `MS Sans Serif`, sans-serif.
- Code, document number, item code, and account fields: `Consolas`,
  `Courier New`, monospace.
- Default text size: `12px`.
- Section title size: `13px`, bold.
- Table text size: `12px`.
- Button text size: `12px`.
- Letter spacing: `0`.
- Do not use viewport-scaled type.

## Application Shell

Every full UI screen should use a fixed internal app shell:

1. Title bar
   - Example: `HARNESS ERP - Inventory Management [Local]`
   - Background: `--erp-titlebar`.
   - Text: white, compact, left aligned.

2. Menu bar
   - Example groups: `File`, `Master Data`, `Purchasing`, `Inventory`,
     `Approvals`, `Accounting`, `System`, `Help`.
   - Menus should be compact text entries, not large navigation cards.

3. Toolbar
   - One row of small, high-frequency commands.
   - Recommended commands: `Search`, `New`, `Save`, `Delete`, `Copy`,
     `Approve`, `Reject`, `Post`, `Export`, `Print`, `Close`.
   - Icons may be used at 16px, but text labels are acceptable for legacy tone.

4. Work area
   - Default structure: search criteria, result grid, detail form.
   - Use tabs inside detail areas when a record has multiple related datasets.

5. Status bar
   - Example: `User: admin | Company: HARNESS | DB: H2 | Status: Search complete`
   - Use it for non-blocking success messages and current context.

## Primary Page Pattern

Most ERP pages should follow this shape:

```text
[Page title / menu path]

+ Search Criteria --------------------------------------------------+
| Item Code [________]  Item Name [____________]  Warehouse [v]    |
| Status    [v]         Date [YYYY-MM-DD] ~ [YYYY-MM-DD] [Search] |
+------------------------------------------------------------------+

+ Results ----------------------------------------------------------+
| Select | Status | Item Code | Item Name | Warehouse | On Hand ... |
|------------------------------------------------------------------|
| ...                                                              |
+------------------------------------------------------------------+

+ Detail -----------------------------------------------------------+
| Code [________]  Name [________________]  Status [v]             |
| Qty  [____.__]   Unit Price [____.__]    Memo [____________]     |
+------------------------------------------------------------------+
```

Do not replace this pattern with a card-based analytics dashboard unless the
screen is explicitly a dashboard.

## Forms

Field behavior:

- Optional editable fields use white backgrounds.
- Required editable fields use `--erp-input-required`.
- Read-only fields use `--erp-input-readonly`.
- Numeric fields are right aligned.
- Code and ID fields use monospace and short fixed widths.
- Dates use `YYYY-MM-DD`.
- Fields with lookup support should include a small `...` button.
- Long labels are left aligned and should not wrap inside compact forms.

Example field set:

```text
Vendor Code [V00041____] [...]
Vendor Name [Daehan Parts Industrial____] read-only
Document Date [2026-06-06]
Amount [______________123,450.00]
```

## Buttons

Button styling:

- Background: `--erp-panel-dark`.
- Height: `24px`.
- Padding: `4px 10px`.
- Border: light top/left and dark bottom/right.
- Active state: inverted inset border.
- Disabled state: gray text and muted border.

Button hierarchy should be restrained. Legacy ERP screens should not have large
modern primary buttons.

Recommended labels:

```text
Search | Reset | New | Save | Delete | Copy | Approve | Reject | Post |
Export | Print | Select | Close
```

## Data Grid

The data grid is the core component.

Grid requirements:

- Header height: `28px`.
- Row height: `26px`.
- Cell borders on every row and column.
- Header background: `--erp-table-header`.
- Alternating rows may use `--erp-table-row-alt`.
- Selected row: `--erp-table-selected` with white text.
- Hover state: pale yellow or pale blue.
- Numeric and amount columns right aligned.
- Code, status, and checkbox columns centered.
- Include pagination or record counts for large result sets.
- Include a summary row when totals are relevant.

Example columns:

```text
Select | Status | Item Code | Item Name | Warehouse | On Hand |
Reserved | Available | Last Updated
```

Toolbar actions above a grid should apply to either the current query or the
selected rows. Row-level actions should be compact and predictable.

## Tabs

Use classic square tabs for detail subdivisions:

```text
[Basic Info] [Transactions] [Inventory History] [Attachments] [Approval History]
```

Tab rules:

- No pill styling.
- Active tab should visually connect to the content panel.
- Keep tab labels short.
- Do not place cards inside tab panels unless each card is a repeated item.

## Lookup Dialogs

Lookup dialogs should follow an Oracle LOV-style workflow:

- Title example: `Item Code Lookup`.
- Top area: search criteria.
- Middle area: result grid.
- Bottom area: `Select` and `Close` buttons.
- Double-clicking a row should select it.
- Return both code and display name to the calling form when available.

## Messages

Use short, operational copy:

```text
Saved.
Required value is missing. [Item Code]
No row is selected.
Approved documents cannot be deleted.
Search complete. 18 records found.
```

Message placement:

- Success: status bar.
- Validation error: inline field state plus status bar text when possible.
- Blocking warning: small modal confirmation.
- System error: modal alert with concise text.

## Recommended MVP Screens

If a UI is added to the current ERP domain, start with:

- Main menu and work shortcuts.
- Employee management.
- Purchase request list.
- Purchase request detail.
- Approval queue.
- Approval history.
- User and role policy reference.

Future ERP modules can follow the same pattern:

- Item master.
- Vendor master.
- Inventory status.
- Inventory transaction history.
- Purchase order entry.
- Accounting document inquiry.

## Implementation Checklist

Before accepting a UI screen, verify:

- The first screen is the usable ERP workspace, not a landing page.
- The screen uses the app shell: title bar, menu bar, toolbar, work area,
  status bar.
- Search criteria, result grid, and detail form are present where relevant.
- Required, optional, and read-only fields are visually distinct.
- Tables remain readable at dense row heights.
- Text does not overflow buttons, tabs, fields, or grid headers.
- Status and error messages use short ERP-style wording.
- No generated build output, local config, secrets, or logs are committed.
