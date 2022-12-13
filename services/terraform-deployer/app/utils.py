import os
import shutil

from base64 import b64encode
from pathlib import Path
from typing import List

from app.models import TerraformFileInfo


def mkdir(dirname: str):
    """
    Creates directory to store and apply terraform files if not exists
    """
    print("Setup terraform directory")
    Path(dirname).mkdir(parents=True, exist_ok=True)


def rmdir(dirname: str):
    """
    Removes directory to store and apply terraform files recursively
    """
    try:
        print("Remove terraform directory")
        shutil.rmtree(dirname)
    except:
        pass


def rm_files_with_extension(dirname: str, exts: List[str]):
    """
    Removes all files with extension in directory
    """
    for ext in exts:
        print(f"Remove all files with ending {ext} from {dirname} directory")
        files = os.listdir(dirname)
        for item in files:
            if item.endswith("." + ext):
                os.remove(os.path.join(dirname, item))


def read_file_to_base64(path: str) -> str:
    """
    Reads file as base64 encoded string
    """
    with open(path, "rb") as data:
        return b64encode(data.read()).decode("utf-8")


def get_file(filename: str, files: List[TerraformFileInfo]):
    """
    Returns file with filename from list if available
    """
    for file in files:
        if file.filename == filename:
            return file
    return None


def get_files_except(filename: str, files: List[TerraformFileInfo]):
    """
    Returns all files without filename
    """
    rfiles: List[TerraformFileInfo] = []

    for file in files:
        if file.filename != filename:
            rfiles.append(file)

    return rfiles


def create_empty_file(filepath: str):
    """
    Creates empty file without content
    """
    open(filepath, "a").close()
