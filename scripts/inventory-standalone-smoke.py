#!/usr/bin/env python3

from __future__ import annotations

from phase4_smoke_lib import (
    JsonHttpClient,
    api_data,
    decimal_value,
    load_env,
    log,
    login,
    main_guard,
    page_content,
    require,
    unique_suffix,
)


def main() -> None:
    core_base_url = load_env("CORE_BASE_URL", "http://127.0.0.1:8080")
    inventory_base_url = load_env("INVENTORY_BASE_URL", "http://127.0.0.1:8081")
    admin_username = load_env("PHASE4_ADMIN_USERNAME", "admin")
    admin_password = load_env("PHASE4_ADMIN_PASSWORD", "admin123")

    token = login(core_base_url, admin_username, admin_password)
    inventory = JsonHttpClient(inventory_base_url)
    suffix = unique_suffix("inv-smoke")

    log("auth", "Resolving inventory session from a hospital-core token")
    session = api_data(
        inventory.request("GET", "/api/inventory/system/session", token=token),
        "Inventory session",
    )
    permissions = session.get("permissions", [])
    require(any(code.startswith("inventory.") for code in permissions), "Inventory permissions were not resolved")

    log("category", "Creating inventory category")
    category = api_data(
        inventory.request(
            "POST",
            "/api/inventory/categories",
            token=token,
            body={
                "code": suffix.upper().replace("-", "_"),
                "name": f"Inventory Smoke {suffix}",
                "description": "Phase 4 standalone smoke category",
            },
        ),
        "Create inventory category",
    )

    log("warehouse", "Creating warehouse")
    warehouse = api_data(
        inventory.request(
            "POST",
            "/api/inventory/warehouses",
            token=token,
            body={
                "code": f"WH-{suffix}".upper(),
                "name": f"Warehouse {suffix}",
                "type": "FACILITY",
                "description": "Phase 4 standalone smoke warehouse",
            },
        ),
        "Create warehouse",
    )

    log("supplier", "Creating supplier")
    supplier = api_data(
        inventory.request(
            "POST",
            "/api/inventory/suppliers",
            token=token,
            body={
                "code": f"SUP-{suffix}".upper(),
                "name": f"Supplier {suffix}",
                "contactPerson": "Smoke Contact",
                "email": f"{suffix}@example.com",
                "phone": "+90-555-000-0000",
            },
        ),
        "Create supplier",
    )

    log("item", "Creating inventory item with a base unit")
    item = api_data(
        inventory.request(
            "POST",
            "/api/inventory/items",
            token=token,
            body={
                "code": f"ITEM-{suffix}".upper(),
                "name": f"Item {suffix}",
                "description": "Phase 4 standalone smoke item",
                "trackBatches": True,
                "trackExpiry": True,
                "categoryId": category["id"],
                "units": [
                    {
                        "unitCode": "EA",
                        "unitName": "Each",
                        "conversionFactor": 1,
                        "baseUnit": True,
                    }
                ],
            },
        ),
        "Create inventory item",
    )

    log("purchase-order", "Creating and approving purchase order")
    purchase_order = api_data(
        inventory.request(
            "POST",
            "/api/inventory/purchase-orders",
            token=token,
            body={
                "supplierId": supplier["id"],
                "code": f"PO-{suffix}".upper(),
                "notes": "Phase 4 standalone smoke purchase order",
                "items": [
                    {
                        "inventoryItemId": item["id"],
                        "quantity": 25,
                        "unitPrice": 4.5,
                        "unitCode": "EA",
                    }
                ],
            },
        ),
        "Create purchase order",
    )
    purchase_order = api_data(
        inventory.request(
            "POST",
            f"/api/inventory/purchase-orders/{purchase_order['id']}/approve",
            token=token,
        ),
        "Approve purchase order",
    )
    require(purchase_order["status"] == "APPROVED", f"Expected APPROVED purchase order, got {purchase_order['status']}")

    log("receipt", "Receiving ordered stock into the warehouse")
    receipt = api_data(
        inventory.request(
            "POST",
            "/api/inventory/receipts",
            token=token,
            body={
                "purchaseOrderId": purchase_order["id"],
                "warehouseId": warehouse["id"],
                "code": f"GR-{suffix}".upper(),
                "notes": "Phase 4 standalone smoke receipt",
                "items": [
                    {
                        "purchaseOrderItemId": purchase_order["items"][0]["id"],
                        "batchNumber": f"BATCH-{suffix}".upper(),
                        "expiresAt": "2030-01-01",
                        "quantity": 25,
                    }
                ],
            },
        ),
        "Create goods receipt",
    )
    require(len(receipt["items"]) == 1, "Expected one goods receipt line")

    log("availability", "Checking available quantity and inbound ledger movement")
    availability = api_data(
        inventory.request("GET", f"/api/inventory/items/{item['id']}/availability", token=token),
        "Get availability",
    )
    require(decimal_value(availability["totalOnHand"]) == 25.0, f"Expected totalOnHand=25, got {availability['totalOnHand']}")
    require(
        decimal_value(availability["availableQuantity"]) == 25.0,
        f"Expected availableQuantity=25, got {availability['availableQuantity']}",
    )

    movements = page_content(
        inventory.request("GET", f"/api/inventory/items/{item['id']}/movements", token=token),
        "Get movements",
    )
    require(len(movements) >= 1, "Expected at least one stock movement")
    require(
        any(movement["movementType"] == "INBOUND" for movement in movements),
        "Expected an INBOUND stock movement after goods receipt",
    )

    log("done", "Inventory standalone smoke passed")


if __name__ == "__main__":
    main_guard(main)
