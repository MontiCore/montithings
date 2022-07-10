package unlock;

component FaceUnlock {
    Camera camera;
    FaceID faceid;
    Door door;

    camera.image -> faceid.image;
    faceid.name -> door.name;
}