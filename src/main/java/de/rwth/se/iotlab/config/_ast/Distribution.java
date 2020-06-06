package de.rwth.se.iotlab.config._ast;

import java.util.ArrayList;

public class Distribution {
    private String name;
    final private ArrayList<Constraint> selectionConjunctionProperties = new ArrayList<>();
    final private ArrayList<Constraint> lteConstraints = new ArrayList<>();
    final private ArrayList<Constraint> equalConstraints = new ArrayList<>();
    final private ArrayList<Constraint> gteConstraints = new ArrayList<>();
    final private ArrayList<Constraint> checkAllConstraints = new ArrayList<>();

    public static class Constraint {
        private String key;
        private String value;
        private String operator;
        private String number;

        public Constraint(String key, String value, String operator, String number) {
            this.key = key;
            this.value = value;
            this.operator = operator;
            this.number = number;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    public void addSelectionConjunctionProperty(String key, String value, String operator, String number) {
        this.selectionConjunctionProperties.add(new Constraint(key, value, operator, number));
    }

    public ArrayList<Constraint> getSelectionConjunctionProperties() {
        return this.selectionConjunctionProperties;
    }

    public void addLteConstraint(String key, String value, String operator, String number) {
        this.lteConstraints.add(new Constraint(key, value, operator, number));
    }
    public void addEqualConstraint(String key, String value, String operator, String number) {
        this.equalConstraints.add(new Constraint(key, value, operator, number));
    }
    public void addGteConstraint(String key, String value, String operator, String number) {
        this.gteConstraints.add(new Constraint(key, value, operator, number));
    }

    public void addCheckAllConstraint(String key, String value, String operator, String number) {
        this.checkAllConstraints.add(new Constraint(key, value, operator, number));
    }

    public ArrayList<Constraint> getEqualConstraints() {
        return equalConstraints;
    }

    public ArrayList<Constraint> getGteConstraints() {
        return gteConstraints;
    }

    public ArrayList<Constraint> getLteConstraints() {
        return lteConstraints;
    }

    public ArrayList<Constraint> getCheckAllConstraints() {
        return checkAllConstraints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
