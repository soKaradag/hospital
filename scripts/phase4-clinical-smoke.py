#!/usr/bin/env python3

from __future__ import annotations

from datetime import datetime, timedelta, timezone

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


def inventory_item_by_code(inventory: JsonHttpClient, token: str, code: str) -> dict:
    items = page_content(
        inventory.request("GET", f"/api/inventory/items?search={code}", token=token),
        f"Search inventory item {code}",
    )
    require(items, f"Inventory item not found by code search: {code}")
    for item in items:
        if item.get("code") == code:
            return item
    return items[0]


def main() -> None:
    core_base_url = load_env("CORE_BASE_URL", "http://127.0.0.1:8080")
    inventory_base_url = load_env("INVENTORY_BASE_URL", "http://127.0.0.1:8081")
    admin_username = load_env("PHASE4_ADMIN_USERNAME", "admin")
    admin_password = load_env("PHASE4_ADMIN_PASSWORD", "admin123")

    token = login(core_base_url, admin_username, admin_password)
    core = JsonHttpClient(core_base_url)
    inventory = JsonHttpClient(inventory_base_url)
    suffix = unique_suffix("clinical")

    consultation_item = inventory_item_by_code(inventory, token, "CONSULTATION")
    general_med_item = inventory_item_by_code(inventory, token, "GENERAL_MED")
    consultation_before = api_data(
        inventory.request("GET", f"/api/inventory/items/{consultation_item['id']}/availability", token=token),
        "Consultation availability before encounter",
    )
    general_med_before = api_data(
        inventory.request("GET", f"/api/inventory/items/{general_med_item['id']}/availability", token=token),
        "General medication availability before prescription",
    )

    log("department", "Creating department")
    department = api_data(
        core.request(
            "POST",
            "/api/departments",
            token=token,
            body={"name": f"Phase 4 Surgery {suffix}", "description": "Phase 4 clinical smoke department"},
        ),
        "Create department",
    )

    log("doctor", "Creating doctor")
    doctor = api_data(
        core.request(
            "POST",
            "/api/doctors",
            token=token,
            body={
                "firstName": "Phase",
                "lastName": "Doctor",
                "specialization": "General Surgery",
                "departmentId": department["id"],
            },
        ),
        "Create doctor",
    )

    log("patient", "Creating patient")
    patient = api_data(
        core.request(
            "POST",
            "/api/patients",
            token=token,
            body={
                "firstName": "Phase",
                "lastName": "Patient",
                "nationalId": suffix.replace("-", "")[:20],
                "birthDate": "1990-01-01",
                "gender": "MALE",
            },
        ),
        "Create patient",
    )

    log("encounter", "Creating encounter and expecting consultation stock consumption")
    encounter_time = datetime.now(timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z")
    encounter = api_data(
        core.request(
            "POST",
            "/api/encounters",
            token=token,
            body={
                "patientId": patient["id"],
                "doctorId": doctor["id"],
                "complaint": "Severe abdominal pain",
                "diagnosisNote": "Possible appendicitis",
                "treatmentNote": "Observation and surgery evaluation",
                "encounterDateTime": encounter_time,
            },
        ),
        "Create encounter",
    )
    require(encounter["procedureCount"] >= 1, f"Expected procedureCount >= 1, got {encounter['procedureCount']}")
    consultation_after = api_data(
        inventory.request("GET", f"/api/inventory/items/{consultation_item['id']}/availability", token=token),
        "Consultation availability after encounter",
    )
    require(
        decimal_value(consultation_before["availableQuantity"]) - decimal_value(consultation_after["availableQuantity"]) == 1.0,
        "Encounter did not consume one CONSULTATION unit",
    )

    log("prescription", "Creating prescription and expecting medication consumption")
    prescription = api_data(
        core.request(
            "POST",
            "/api/prescriptions",
            token=token,
            body={
                "encounterId": encounter["id"],
                "patientId": patient["id"],
                "doctorId": doctor["id"],
                "prescriptionDate": datetime.now(timezone.utc).date().isoformat(),
                "notes": "Phase 4 clinical smoke prescription",
            },
        ),
        "Create prescription",
    )
    require(prescription["itemCount"] == 1, f"Expected one prescription item, got {prescription['itemCount']}")
    require(prescription["dispenseCount"] == 1, f"Expected one dispense row, got {prescription['dispenseCount']}")
    general_med_after_prescription = api_data(
        inventory.request("GET", f"/api/inventory/items/{general_med_item['id']}/availability", token=token),
        "General medication availability after prescription",
    )
    require(
        decimal_value(general_med_before["availableQuantity"])
        - decimal_value(general_med_after_prescription["availableQuantity"])
        == 14.0,
        "Prescription did not consume fourteen GENERAL_MED units",
    )

    procedure_code = f"APPEND_{suffix}".upper().replace("-", "_")[:40]

    log("surgery-setup", "Creating operating room, privilege and supply template")
    operating_room = api_data(
        core.request(
            "POST",
            "/api/surgeries/operating-rooms",
            token=token,
            body={
                "departmentId": department["id"],
                "code": f"OR-{suffix}".upper()[:100],
                "name": f"Operating Room {suffix}"[:150],
            },
        ),
        "Create operating room",
    )
    api_data(
        core.request(
            "POST",
            "/api/surgeries/doctor-privileges",
            token=token,
            body={
                "doctorId": doctor["id"],
                "procedureCode": procedure_code,
                "procedureName": "Appendectomy",
            },
        ),
        "Grant doctor privilege",
    )
    supply_template = api_data(
        core.request(
            "POST",
            "/api/surgeries/supply-templates",
            token=token,
            body={
                "code": f"TPL-{suffix}".upper()[:100],
                "name": f"Surgery Template {suffix}"[:150],
                "procedureCode": procedure_code,
                "items": [{"inventoryItemCode": "GENERAL_MED", "quantity": 2, "note": "Smoke reserve"}],
            },
        ),
        "Create surgery supply template",
    )

    def create_surgery_request(note: str) -> dict:
        return api_data(
            core.request(
                "POST",
                "/api/surgeries/requests",
                token=token,
                body={
                    "encounterId": encounter["id"],
                    "requestedByDoctorId": doctor["id"],
                    "procedureCode": procedure_code,
                    "procedureName": "Appendectomy",
                    "priority": "HIGH",
                    "preferredDate": (datetime.now(timezone.utc) + timedelta(days=1)).date().isoformat(),
                    "note": note,
                },
            ),
            "Create surgery request",
        )

    log("surgery-cancel", "Scheduling one surgery and cancelling it to test release")
    first_request = create_surgery_request("Phase 4 cancel path")
    first_surgery = api_data(
        core.request(
            "POST",
            "/api/surgeries",
            token=token,
            body={
                "surgeryRequestId": first_request["id"],
                "primaryDoctorId": doctor["id"],
                "operatingRoomId": operating_room["id"],
                "supplyTemplateId": supply_template["id"],
                "scheduledAt": (datetime.now(timezone.utc) + timedelta(hours=4)).replace(microsecond=0).isoformat().replace("+00:00", "Z"),
                "note": "Phase 4 cancel flow",
            },
        ),
        "Schedule surgery for cancel path",
    )
    require(first_surgery["inventoryStatus"] == "RESERVED", f"Expected RESERVED, got {first_surgery['inventoryStatus']}")
    reservation_status = api_data(
        inventory.request("GET", f"/api/inventory/surgeries/{first_surgery['id']}/reservation-status", token=token),
        "Get surgery reservation status",
    )
    require(reservation_status["status"] == "ACTIVE", f"Expected ACTIVE reservation, got {reservation_status['status']}")
    cancelled_surgery = api_data(
        core.request(
            "POST",
            f"/api/surgeries/{first_surgery['id']}/cancel",
            token=token,
            body={"note": "Phase 4 cancel smoke"},
        ),
        "Cancel surgery",
    )
    require(cancelled_surgery["inventoryStatus"] == "RELEASED", f"Expected RELEASED, got {cancelled_surgery['inventoryStatus']}")
    reservation_status = api_data(
        inventory.request("GET", f"/api/inventory/surgeries/{first_surgery['id']}/reservation-status", token=token),
        "Get surgery reservation status after cancel",
    )
    require(reservation_status["status"] == "INACTIVE", f"Expected INACTIVE reservation, got {reservation_status['status']}")

    log("surgery-complete", "Scheduling a second surgery and completing it to test final consumption")
    general_med_before_completion = api_data(
        inventory.request("GET", f"/api/inventory/items/{general_med_item['id']}/availability", token=token),
        "General medication availability before surgery completion",
    )
    second_request = create_surgery_request("Phase 4 complete path")
    second_surgery = api_data(
        core.request(
            "POST",
            "/api/surgeries",
            token=token,
            body={
                "surgeryRequestId": second_request["id"],
                "primaryDoctorId": doctor["id"],
                "operatingRoomId": operating_room["id"],
                "supplyTemplateId": supply_template["id"],
                "scheduledAt": (datetime.now(timezone.utc) + timedelta(hours=6)).replace(microsecond=0).isoformat().replace("+00:00", "Z"),
                "note": "Phase 4 complete flow",
            },
        ),
        "Schedule surgery for completion path",
    )
    require(second_surgery["inventoryStatus"] == "RESERVED", f"Expected RESERVED, got {second_surgery['inventoryStatus']}")
    completed_surgery = api_data(
        core.request(
            "POST",
            f"/api/surgeries/{second_surgery['id']}/complete",
            token=token,
            body={"note": "Phase 4 complete smoke"},
        ),
        "Complete surgery",
    )
    require(completed_surgery["inventoryStatus"] == "CONSUMED", f"Expected CONSUMED, got {completed_surgery['inventoryStatus']}")
    reservation_status = api_data(
        inventory.request("GET", f"/api/inventory/surgeries/{second_surgery['id']}/reservation-status", token=token),
        "Get surgery reservation status after completion",
    )
    require(reservation_status["status"] == "INACTIVE", f"Expected INACTIVE reservation, got {reservation_status['status']}")
    general_med_after_completion = api_data(
        inventory.request("GET", f"/api/inventory/items/{general_med_item['id']}/availability", token=token),
        "General medication availability after surgery completion",
    )
    require(
        decimal_value(general_med_before_completion["totalOnHand"])
        - decimal_value(general_med_after_completion["totalOnHand"])
        == 2.0,
        "Completed surgery did not consume two GENERAL_MED units",
    )

    log("done", "Phase 4 clinical smoke passed")


if __name__ == "__main__":
    main_guard(main)
