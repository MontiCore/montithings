// (c) https://github.com/MontiCore/monticore
package unlock;

component FaceUnlock {
    Camera camera;
    FaceID faceid;
    Door door;

    camera.image -> faceid.image;
    faceid.visitor -> door.visitor;
}