package unlock;

component Camera {
    port out Camera.Image image;

    every 1s {
        image = "Sebastian";
    }
}
