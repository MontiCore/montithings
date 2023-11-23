# (c) https://github.com/MontiCore/monticore
from fastapi import FastAPI, Header

from .models import TerraformBody
from .terraform import apply_tf, set_env
from .validation import is_auth

app = FastAPI()


@app.get("/", status_code=200)
def read_root():
    print("GET '/'")
    return "API callable"


@app.post("/apply", status_code=201)
def apply(body: TerraformBody, x_token: str = Header()):
    print("POST '/apply'")
    is_auth(x_token)
    set_env(body.credentials)
    res = apply_tf(body)
    return res
