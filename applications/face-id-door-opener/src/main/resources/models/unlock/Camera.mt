package unlock;

component Camera {
    port out String image;

    every 1s {
        image = "Sebastian";
    }
}
