package unlock;

component FaceID {
    port in String image;
    port out Visitor.Person visitor;

    behavior image {
        log("Recognizing visitor");
    }
}