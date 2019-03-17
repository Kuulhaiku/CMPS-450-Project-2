
public class AtomicNode extends AbstractNode {
  int value;
  TokenType typeChar;
  public AtomicNode(){super();}
  public AtomicNode(int x, TokenType t, int iLevel){
    super(iLevel, 1);
    this.value = x;
    this.typeChar = t;
  }
  public int getChar() {return value;}
  public TokenType typeChar() {return typeChar;}
  public String toString() {
    String rString = "";
    rString += super.toString();
    rString += Character.toString((char)value);
    rString += " ";
    rString += typeChar.toString();
    return rString;
  }
}
