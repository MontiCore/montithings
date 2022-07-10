package unlock;

component FaceID {
    port in String image;
    port out String name;

    behavior image {
        log("Recognizing visitor");
    }
}