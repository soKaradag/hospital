#!/usr/bin/env python3

from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from typing import Any


class SmokeError(RuntimeError):
    pass


def log(step: str, message: str) -> None:
    print(f"[{step}] {message}")


def fail(message: str) -> "NoReturn":
    raise SmokeError(message)


def require(condition: bool, message: str) -> None:
    if not condition:
        fail(message)


def unique_suffix(prefix: str) -> str:
    millis = int(time.time() * 1000)
    return f"{prefix}-{millis}"


@dataclass
class JsonHttpClient:
    base_url: str
    timeout_seconds: int = 20

    def request(
        self,
        method: str,
        path: str,
        *,
        token: str | None = None,
        body: dict[str, Any] | None = None,
        expected_statuses: tuple[int, ...] = (200,),
    ) -> dict[str, Any]:
        url = self.base_url.rstrip("/") + path
        headers = {"Accept": "application/json"}
        data: bytes | None = None
        if token:
            headers["Authorization"] = f"Bearer {token}"
        if body is not None:
            headers["Content-Type"] = "application/json"
            data = json.dumps(body).encode("utf-8")

        request = urllib.request.Request(url, data=data, headers=headers, method=method.upper())
        try:
            with urllib.request.urlopen(request, timeout=self.timeout_seconds) as response:
                payload_text = response.read().decode("utf-8")
                if response.status not in expected_statuses:
                    fail(f"Unexpected HTTP {response.status} from {method} {path}: {payload_text}")
                return json.loads(payload_text) if payload_text else {}
        except urllib.error.HTTPError as exc:
            payload_text = exc.read().decode("utf-8", errors="replace")
            if exc.code not in expected_statuses:
                fail(f"HTTP {exc.code} from {method} {path}: {payload_text}")
            return json.loads(payload_text) if payload_text else {}
        except urllib.error.URLError as exc:
            fail(f"Request failed for {method} {path}: {exc}")


def api_data(response: dict[str, Any], message_hint: str) -> Any:
    require(response.get("success") is True, f"{message_hint}: expected success=true, got {response}")
    return response.get("data")


def page_content(response: dict[str, Any], message_hint: str) -> list[dict[str, Any]]:
    data = api_data(response, message_hint)
    content = data.get("content") if isinstance(data, dict) else None
    require(isinstance(content, list), f"{message_hint}: expected paged content list, got {response}")
    return content


def decimal_value(value: Any) -> float:
    return float(value)


def load_env(name: str, default: str) -> str:
    value = os.environ.get(name, default).strip()
    require(bool(value), f"Environment variable {name} must not be blank")
    return value


def login(core_base_url: str, username: str, password: str) -> str:
    client = JsonHttpClient(core_base_url)
    response = client.request(
        "POST",
        "/api/auth/login",
        body={"username": username, "password": password},
    )
    data = api_data(response, "Core login")
    access_token = data.get("accessToken") if isinstance(data, dict) else None
    require(isinstance(access_token, str) and access_token, "Core login did not return accessToken")
    return access_token


def wait_for_http(url: str, timeout_seconds: int = 120) -> None:
    deadline = time.time() + timeout_seconds
    while time.time() < deadline:
        try:
            with urllib.request.urlopen(url, timeout=5):
                return
        except Exception:
            time.sleep(2)
    fail(f"Timed out waiting for {url}")


def main_guard(main_fn) -> None:
    try:
        main_fn()
    except SmokeError as exc:
        print(f"SMOKE FAILED: {exc}", file=sys.stderr)
        raise SystemExit(1) from exc

