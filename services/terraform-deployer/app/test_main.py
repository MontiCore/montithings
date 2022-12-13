from fastapi.testclient import TestClient
from pathlib import Path

from .main import app
from .utils import read_file_to_base64

client = TestClient(app)

_token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9"
_credentials = {
    "clientId": "7928e8a1-92fc-48de-9254-6fd6c8343453",
    "clientSecret": "secret",
    "subscriptionId": "c5faee3c-8346-4ea4-aba7-04172c659c32",
    "tenantId": "78be03a3-08f8-4517-a069-eedd96caa4a7",
}


def test_read_main():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == "API callable"


def test_apply():
    filecontent = read_file_to_base64("./app/assets/test.tf")
    file1 = {"filename": "test1", "filecontent": filecontent}
    body = {"files": [file1], "credentials": _credentials}

    response = client.post(
        "/apply",
        headers={"X-Token": _token},
        json=body,
    )

    assert response.status_code == 201


def test_apply_bad_token():
    filecontent = read_file_to_base64("./app/assets/test.tf")
    file1 = {"filename": "test1", "filecontent": filecontent}
    body = {"files": [file1], "credentials": _credentials}

    response = client.post(
        "/apply",
        headers={"X-Token": "sth-else"},
        json=body,
    )

    assert response.status_code == 400


def test_destroy():
    filecontent = read_file_to_base64("./app/assets/test.tf")
    file1 = {"filename": "test1", "filecontent": filecontent}
    body = {"files": [file1], "credentials": _credentials}

    response = client.post(
        "/destroy",
        headers={"X-Token": _token},
        json=body,
    )

    assert response.status_code == 204


def test_destroy_bad_token():
    filecontent = read_file_to_base64("./app/assets/test.tf")
    file1 = {"filename": "test1", "filecontent": filecontent}
    body = {"files": [file1], "credentials": _credentials}

    response = client.post(
        "/destroy",
        headers={"X-Token": "sth-else"},
        json=body,
    )

    assert response.status_code == 400
