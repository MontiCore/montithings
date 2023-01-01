# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>
def generate_list():

    # Add Notes
    print("----")
    <#list ast.getKnotenList() as knoten>
        <#assign name = knoten.getName()>
    print("${name}")
    </#list>
    print("----")
    <#list ast.getConnectionList() as cons>
        <#assign left = cons.getKnoten1Name()>
        <#assign right = cons.getKnoten2Name()>
    print("%s-%s" % ("${left}","${right}"))
    </#list>

if __name__ == '__main__':
    generate_list()