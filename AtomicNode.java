
public class AtomicNode extends AbstractNode {
  int value;
  TokenType typeChar;
  public AtomicNode(){super();}
  public AtomicNode(int x, TokenType t, int iLevel){
    super(iLevel);
    this.value = x;
    this.typeChar = t;
  }
  public int getChar() {return value;}
  public TokenType typeChar() {return typeChar;}
}
