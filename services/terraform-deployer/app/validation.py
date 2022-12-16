from fastapi import HTTPException

from app.models import TerraformBody

_token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9"


def is_auth(x_token: str):
    """
    Checks that request is authenticated (X-Token header with correct token must be present)
    """
    print(f"Check auth for token {x_token}")

    if x_token != _token:
        raise HTTPException(status_code=400, detail="Invalid X-Token header")

    print("Request successfully authenticated")
