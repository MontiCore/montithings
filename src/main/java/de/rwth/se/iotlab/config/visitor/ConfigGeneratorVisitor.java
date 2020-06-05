package de.rwth.se.iotlab.config.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.json._ast.ASTJSONArray;
import de.monticore.lang.json._ast.ASTJSONBoolean;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._ast.ASTJSONNode;
import de.monticore.lang.json._ast.ASTJSONNull;
import de.monticore.lang.json._ast.ASTJSONNumber;
import de.monticore.lang.json._ast.ASTJSONObject;
import de.monticore.lang.json._ast.ASTJSONProperty;
import de.monticore.lang.json._ast.ASTJSONString;
import de.monticore.lang.json._ast.ASTJSONValue;

import de.rwth.se.iotlab.config._ast.ASTConfig;
import de.rwth.se.iotlab.config._ast.ASTConfigNode;
import de.rwth.se.iotlab.config._visitor.ConfigVisitor;

import java.io.Console;

public class ConfigGeneratorVisitor implements ConfigVisitor {
    ConfigVisitor realThis = this;

    public ConfigGeneratorVisitor() {
    }

    @Override
    public ConfigVisitor getRealThis() {
        // TODO Auto-generated method stub
        return realThis;
    }


    @Override
    public void setRealThis(ConfigVisitor realThis) {
        this.realThis = realThis;
    }

    

    //--------------- Visitor methods
    @Override
    public void endVisit(ASTNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTConfig node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTConfigNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void handle(ASTConfig node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTConfigNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }


    @Override
    public void traverse(ASTConfig node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void visit(ASTNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTConfig node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTConfigNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void endVisit(ASTJSONDocument node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONObject node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONProperty node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONBoolean node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONString node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONNumber node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONArray node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONNull node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONValue node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void endVisit(ASTJSONNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.endVisit(node);
    }

    @Override
    public void handle(ASTJSONDocument node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONObject node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONProperty node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONBoolean node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONString node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONNumber node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONArray node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONNull node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONValue node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void handle(ASTJSONNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.handle(node);
    }

    @Override
    public void traverse(ASTJSONDocument node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONObject node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONProperty node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONBoolean node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONString node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONNumber node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONArray node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void traverse(ASTJSONNull node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.traverse(node);
    }

    @Override
    public void visit(ASTJSONDocument node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONObject node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONProperty node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONBoolean node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONString node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONNumber node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONArray node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONNull node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONValue node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }

    @Override
    public void visit(ASTJSONNode node) {
        // TODO Auto-generated method stub
        ConfigVisitor.super.visit(node);
    }
    
}