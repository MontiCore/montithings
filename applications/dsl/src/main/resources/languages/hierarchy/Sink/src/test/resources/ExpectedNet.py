# (c) https://github.com/MontiCore/monticore
def generate_list():

    # Add Notes
    print("----")
    print("Erster")
    print("Zweiter")
    print("Dritter")
    print("Vierter")
    print("Fuenfter")
    print("----")
    print("%s-%s" % ("Erster","Zweiter"))
    print("%s-%s" % ("Zweiter","Dritter"))
    print("%s-%s" % ("Vierter","Fuenfter"))
    print("%s-%s" % ("Fuenfter","Dritter"))
    print("%s-%s" % ("Dritter","Erster"))

if __name__ == '__main__':
    generate_list()