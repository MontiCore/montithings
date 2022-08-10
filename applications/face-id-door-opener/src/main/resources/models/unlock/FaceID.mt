package unlock;

component FaceID {
    port in Person.Image image;
    port out Person.Person visitor;

    behavior image {
        log("Recognizing visitor");
    }
}