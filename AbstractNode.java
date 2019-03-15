public class AbstractNode {
  int level;
  public AbstractNode(){level = 0;}
  public AbstractNode(int x) {
    level = x;
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
}
