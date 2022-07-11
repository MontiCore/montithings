package unlock;

component Door {
    port in Person.Person visitor;

    behavior visitor {
        log("Checking access");
    }
}