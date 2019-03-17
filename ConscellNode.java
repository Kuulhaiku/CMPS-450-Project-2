//Class ConscellNode
//sub class of AbstractNode
//Contains a pointer to left child, right child, and a String identifier for the
//type of node. Helper functions to return private values and functions that
//overwrite the default toString() function. Additional functionality Implemented
//to facilitate the completion of part 4 that are not utilized in part 3.

public class ConscellNode extends AbstractNode {
  AbstractNode first = null;
  ConscellNode next = null;
  String typeNode;

  public ConscellNode() {super();}
  public ConscellNode(String type, int iLevel) {
    super(iLevel, 0);
    this.typeNode = type;
  }
  public ConscellNode(AbstractNode first, String type, int iLevel) {
    super(iLevel, 0);
    this.first = first;
    this.typeNode = type;
  }
  public AbstractNode getFirst() {return first;}
  public ConscellNode getNext() {return next;}
  public String getType() {return typeNode;}
  public void setFirst(AbstractNode node) {first = node;}
  public void setNext(ConscellNode node) {next = node;}
  public void setType(String type) {typeNode = type;}
  public String toString() {
    String rString = "";
    rString += super.toString();
    rString += typeNode;
    return rString;
  }
  public String toString(int iLevel) {
    String rString = "";
    rString += super.toString(iLevel);
    rString += typeNode;
    return rString;
  }
}
