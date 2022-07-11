package unlock;

component Door {
    port in String name;

    behavior name {
        log("Checking access");
    }
}