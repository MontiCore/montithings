package unlock;

component FaceID {
    port in Image.Image image;
    port out Person.Person visitor;

    behavior image {
        log("Recognizing visitor");
    }
}