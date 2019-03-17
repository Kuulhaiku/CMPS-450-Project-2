//Class AtomicNode
//sub class of AbstractNode
//Contains an int that holds the curr_char from the scanner and a typeChar that
//contains the current TokenType that is also obtained from the Scanner
//Helper functions to return private values and functions that
//overwrite the default toString() function. Additional functionality Implemented
//to facilitate the completion of part 4 that are not utilized in part 3.

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
  public String toString(int iLevel) {
    String rString = "";
    rString += super.toString(iLevel);
    rString += Character.toString((char)value);
    rString += " ";
    rString += typeChar.toString();
    return rString;
  }
}
