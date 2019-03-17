public class AbstractNode {
  int level;
  int consOrAtom;
  public AbstractNode(){level = 0;}
  public AbstractNode(int x,int y) {
    level = x;
    consOrAtom = y;
  }
  public int getLevel() {
    return level;
  }
  public String toString() {
    String indentation = "";
    for(int i = 0; i < level; i++) {
      indentation += "    ";
    }
    return indentation;
  }
  public int getTypeNode() {return consOrAtom;}
}
