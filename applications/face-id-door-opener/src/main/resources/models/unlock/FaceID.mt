package unlock;

component FaceID {
    port in Camera.Image image;
    port out Visitor.Person visitor;

    behavior image {
        log("Recognizing visitor");
    }
}