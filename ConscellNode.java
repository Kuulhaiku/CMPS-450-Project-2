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
}
