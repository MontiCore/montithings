package unlock;

component Door {
    port in Visitor.Person visitor;

    behavior visitor {
        log("Checking access");
    }
}