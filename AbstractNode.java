//AbstractNode class
//Super class of ConscellNode and AtomicNode

public class AbstractNode {
  int level;
  int consOrAtom;
  //Default Constructor
  public AbstractNode(){level = 0;}

  //Constructor mostly used with part 4
  public AbstractNode(int x,int y) {
    level = x;
    consOrAtom = y;
  }

  //int getLevel
  //returns the indentation level of the node
  public int getLevel() {
    return level;
  }

  //String toString()
  //overwrites the default toString() function from object
  public String toString() {
    String indentation = "";
    for(int i = 0; i < level; i++) {
      indentation += "    ";
    }
    return indentation;
  }

  //modified toString() for use with part 4
  public String toString(int iLevel) {
    String indentation = "";
    for(int i = 0; i < iLevel; i++) {
      indentation += "    ";
    }
    if (consOrAtom == 0) {
      indentation += "(";
    }
    return indentation;
  }

  //int getTypeNode
  //returns type of node
  public int getTypeNode() {return consOrAtom;}
}
