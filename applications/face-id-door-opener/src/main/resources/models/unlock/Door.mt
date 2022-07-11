package unlock;

component Door {
    port in Person visitor;

    behavior visitor {
        log("Checking access");
    }
}