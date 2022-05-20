package tagging._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.List;

public class ASTTagging extends ASTTaggingTOP{
    protected List<ASTMCQualifiedName> allComponents;
    protected List<String> allFeatures;

    protected ASTTagging() { super();}

    public void initAllComponents () {
        allComponents = new ArrayList();
        for (ASTTag tag : this.getTagList()){
            for (ASTMCQualifiedName componentName : tag.getComponentsList()){
                if (!allComponents.stream().filter(o -> o.getQName().equals(componentName.getQName())).findFirst().isPresent()){
                    allComponents.add(componentName);
                }
            }
        }
    }

    public void initAllFeatures () {
        allFeatures = new ArrayList();
        for (ASTTag tag : this.getTagList()){
            allFeatures.add(tag.getFeature().getQName());
        }
    }

    public List<ASTMCQualifiedName> getAllComponents() {return this.allComponents;}
    public List<String> getAllFeatures() {return this.allFeatures;}

}
