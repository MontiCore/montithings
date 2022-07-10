package unlock;

component Door {
    port in String name;
    port out int unlocked;

    behavior name {
        log("Checking access");
    }
}