/* REabsparse.java
Class for implementing a parser that generates abstract syntax trees
Implemented by: Chau Cao
  ULID: c00035898
I certify that the entirety of this implementation is my own work */

import java.io.*;


//Constructor for the Anstract Syntax Tree Parser. Continues until end of file has been
//reached. After each regex is parsed, the tree is printed and the next regex
//is parsed from the read in file. As documented in RErecognizer there is a slight
//problem with the logic involved with the node filtering that involves the
//PLUS STAR and QMARK nodes.
public class REabsparse {
  static PrintStream cout = System.out;
  static TokenType curr_type;
  static int curr_char;
  static String curr_line;
  static PushbackReader pbIn;
  static FileReader fileIn;
  static FileReader temp;
  static BufferedReader currentLine;
  public REabsparse (String input) throws IOException {
    fileIn = new FileReader(input);
    cout.printf("%nEchoing File: %s%n", input);
    echoFile();
    fileIn = new FileReader(input);
    pbIn = new PushbackReader(fileIn);
    temp = new FileReader(input);
    currentLine = new BufferedReader(temp);
    curr_line = "";
    curr_line = currentLine.readLine();
    AbstractNode root;
    while(curr_line != null) {
      getToken();
      root = parse_re_alt(0);
      printTree(root, 0);
      curr_line = currentLine.readLine();
    }
  }

  //void echofile()
  //Function uses global variable fileIn and reads in the entire text file
  //line by line and prints the text to the console.
  //File closes upon completion
  static void echoFile() {
    BufferedReader echo = new BufferedReader(fileIn);
    String curr_char = "";
    try {
      while((curr_char = echo.readLine()) != null) {
      System.out.println(curr_char);
      }
      cout.println();
    }
    catch(IOException e) {
      System.out.println("echofile error");
    }
  }

  //void lookUp()
  //Helper function to determine token type
  //Part of Scanner functionality
  static void lookUp() throws IOException {
    switch (curr_char) {
      case 124:
        curr_type = TokenType.VERT;
        break;
      case 42:
        curr_type = TokenType.STAR;
        break;
      case 43:
        curr_type = TokenType.PLUS;
        break;
      case 63:
        curr_type = TokenType.QMARK;
        break;
      case 40:
        curr_type = TokenType.LPAREN;
        break;
      case 41:
        curr_type = TokenType.RPAREN;
        break;
      case 46:
        curr_type = TokenType.PERIOD;
        break;
      case 92:
        curr_type = TokenType.BSLASH;
        break;
      case 91:
        int temp = 0;
        temp = pbIn.read();
        if(temp == 94) {
          curr_type = TokenType.LNEGSET;
        }
        else {
          pbIn.unread(temp);
          curr_type = TokenType.LPOSSET;
        }
        break;
      case 93:
        curr_type = TokenType.RSET;
        break;
      case 60:
        curr_type = TokenType.LANGLE;
        break;
      case 62:
        curr_type = TokenType.RANGLE;
        break;
      case 13:
        int checkCRLF;
        curr_type = TokenType.EOL;
        checkCRLF = pbIn.read();
        if(checkCRLF != 10)
        {
          pbIn.unread(checkCRLF);
        }
        break;
      default:
        if(Character.isDigit((char)curr_char) || Character.isLetter((char)curr_char) || curr_char == 94 || curr_char == 47)
        {
          curr_type = TokenType.CHAR;
        }
        else {
          curr_type = TokenType.ERROR;
        }
    }
  }

  //void getToken()
  //Main component of scanner
  //Reads in the next item from the file
  //Calls lookUp() to determine the current tokens type and stores in global
  //  variable curr_char
  static void getToken() throws IOException{
    curr_char = pbIn.read();
    getNonBlank();
    lookUp();
  }

  //void getNonBlank()
  //Skips whitespace in regex
  static void getNonBlank() throws IOException{
    while(curr_char == 32)
    {
      curr_char = pbIn.read();
    }
  }

  //void match(TokenType ttype)
  //Advancing component of the Regex Recognizer
  //Communication between the Recognizer and Scanner happens in this function
  static void match(TokenType ttype) throws IOException {
    if(ttype == curr_type) {
      getToken();
    }
    else {
      cout.println("Match Error: " + ttype);
      System.exit(1);
    }
  }

  //void printTree
  //Prints out the Concrete Syntax Tree of the passed in file name
  //as outlined by the specifications of project 2
  static void printTree(AbstractNode root, int level) throws IOException {
    if (root == null) {
      return;
    }
    System.out.println(root.toString(level));
    if(root.getTypeNode() == 0) {
      ConscellNode nextNode = (ConscellNode)root;
      if(nextNode.getType() == "K_alt" || nextNode.getType() == "K_qmark" || nextNode.getType() == "K_plus" || nextNode.getType() == "K_star") {
        printTree(nextNode.getFirst(), level + 1);
        printTree(nextNode.getNext(), level + 1);
        print_indentation(level);
        cout.println(")");
      }

      else {
        printTree(nextNode.getFirst(), level + 1);
        print_indentation(level);
        cout.println(")");
        if(nextNode.getNext() != null) {
          if(nextNode.getNext().getType() == "K_qmark" || nextNode.getNext().getType() == "K_plus" || nextNode.getNext().getType() == "K_star") {
            if(nextNode.getNext().getLevel() < nextNode.getLevel()) {
              printTree(nextNode.getNext(), level);
            }
            else {
              printTree(nextNode.getNext(), level - 1);
            }
          }
          else {
            printTree(nextNode.getNext(), level);
          }
        }
        else {
          printTree(nextNode.getNext(), level);
        }

      }
    }
  }

  //void print_indentation
  //Helper function to print out certain indentation levels
  static void print_indentation(int level) {
    for(int i = 0; i < level; i++) {
      cout.print("    ");
    }
  }

  //The following functions beginning with the name parse_ correspond
  //to the functions of similar names from RErecognizer.java.
  //They correspond to the supplied EBNF grammar from the project 2 specifications
  //and instead of being void function that print during the recursive calls, these
  //functions build a tree of nodes as defined in the classes: AbstractNode, ConscellNode.
  //and AtomicNode. This iteration of the parser includes selective node filtering
  //in order to generate an Abstract Syntax Tree as opposed to a Concrete Syntax tree.
  static ConscellNode parse_re_alt(int level) throws IOException {
    if(level == 0) {
      cout.printf("%nProcessing Expression: \"%s\"%n", curr_line);
    }
    ConscellNode root = new ConscellNode(parse_simple_re_cat(level + 1), K_op_type.K_alt.toString(), level);
    ConscellNode last = root;

    while (curr_type == TokenType.VERT) {
      match(TokenType.VERT);
      last.setNext((ConscellNode)(new ConscellNode(parse_simple_re_cat(level + 1), K_op_type.K_alt.toString(), level + 1)).getFirst());
      last = last.getNext();
    }
    if(root.getNext() != null) {
      return root;
    }
    else {
      return (ConscellNode)root.getFirst();
    }
  }

  static ConscellNode parse_simple_re_cat(int level) throws IOException {
    ConscellNode root = new ConscellNode(parse_basic_re(level + 1), K_op_type.K_cat.toString(), level);
    ConscellNode temp = (ConscellNode)root.getFirst();
    if(temp.getNext() != null && root.getNext() == null){
      return root;
    }
    else{
      return (ConscellNode)root.getFirst();
    }
  }

  static ConscellNode parse_basic_re(int level) throws IOException {
    ConscellNode root = new ConscellNode(parse_elementary_re(level + 1), "B_RE", level);
    root = (ConscellNode)root.getFirst();
    ConscellNode last = root;

    while (curr_type == TokenType.STAR || curr_type == TokenType.PLUS || curr_type == TokenType.QMARK) {
      ConscellNode temp = null;
      switch(curr_type) {
        case STAR:
          temp = new ConscellNode(K_op_type.K_star.toString(), level + 1);
          temp.setNext(root);
          match(TokenType.STAR);
          break;
        case PLUS:
          temp = new ConscellNode(K_op_type.K_plus.toString(), level + 1);
          temp.setNext(root);
          match(TokenType.PLUS);
          break;
        case QMARK:
          temp = new ConscellNode(K_op_type.K_qmark.toString(), level + 1);
          temp.setNext(root);
          match(TokenType.QMARK);
          break;
      }
      //last = last.getNext();
      root = temp;
    }

    while (curr_type != TokenType.VERT && curr_type != TokenType.EOL && curr_type != TokenType.RPAREN) {
      last.setNext(parse_basic_re(level));

      last = last.getNext();
    }

    return root;
  }

  static ConscellNode parse_elementary_re(int level) throws IOException {
    ConscellNode root = new ConscellNode("E_RE", level);
    ConscellNode last;
    boolean paren = false;
    switch(curr_type) {
      case LPAREN:
        last = new ConscellNode(Character.toString((char)curr_char) + " " + curr_type.toString(), level + 1);
        match(TokenType.LPAREN);
        last.setFirst(parse_re_alt(level + 1));
        root.setFirst(last);
        root.setNext(new ConscellNode(Character.toString((char)curr_char) + " " + curr_type.toString(), level + 1));
        match(TokenType.RPAREN);
        paren = true;
        break;
      case PERIOD:
        root.setFirst(new ConscellNode(Character.toString((char)curr_char) + " " + curr_type.toString(), level + 1));
        match(TokenType.PERIOD);
        break;
      case LPOSSET:
        last = new ConscellNode(K_op_type.K_pos_set.toString(), level + 1);
        match(TokenType.LPOSSET);
        last.setFirst(parse_sitems(level + 1));
        root.setFirst(last);
        root.setNext(new ConscellNode(curr_type.toString(), level + 1));
        match(TokenType.RSET);
        break;
      case LNEGSET:
        last = new ConscellNode(K_op_type.K_neg_set.toString(), level + 1);
        match(TokenType.LNEGSET);
        last.setFirst(parse_sitems(level + 1));
        root.setFirst(last);
        root.setNext(new ConscellNode(Character.toString((char)curr_char) + " " + curr_type.toString(), level + 1));
        match(TokenType.RSET);
        break;
      case CHAR:
        last = new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1);
        root.setFirst(last);
        match(TokenType.CHAR);
        break;
      case BSLASH:
        last = new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1);
        root.setFirst(last);
        break;
      case RANGLE:
        last = new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1);
        root.setFirst(last);
        match(TokenType.RANGLE);
        break;
      case LANGLE:
        last = new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1);
        root.setFirst(last);
        match(TokenType.LANGLE);
        break;
    }

    if(paren == true) {
      return (ConscellNode)((ConscellNode)root.getFirst()).getFirst();
    }
    else {
      return (ConscellNode)root.getFirst();
    }

  }

  static AbstractNode parse_char_or_meta(int level) throws IOException {
    if(curr_type == TokenType.BSLASH)
    {
      ConscellNode node = new ConscellNode(Character.toString((char)curr_char) + " " + curr_type.toString(), level + 1);
      match(TokenType.BSLASH);
      AtomicNode aNode = new AtomicNode(curr_char, curr_type, level + 1);
      node.setFirst(aNode);
      match(curr_type);
      return node.getFirst();
    }
    else {
      AtomicNode aNode = new AtomicNode(curr_char, curr_type, level + 1);
      return aNode;
    }
  }

  static AbstractNode parse_sitems(int level) throws IOException {
    ConscellNode root = new ConscellNode("SITEMS", level);
    ConscellNode last = new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1);
    switch(curr_type) {
      case CHAR:
        match(TokenType.CHAR);
        break;
      case BSLASH:
        break;
      case RANGLE:
        match(TokenType.RANGLE);
        break;
      case LANGLE:
        match(TokenType.LANGLE);
        break;
    }
    root.setFirst(last);
    while(curr_type != TokenType.RSET) {
      last.setNext(new ConscellNode(parse_char_or_meta(level + 1), K_op_type.K_char.toString(), level + 1));
      switch(curr_type) {
        case CHAR:
          match(TokenType.CHAR);
          break;
        case BSLASH:
          break;
        case RANGLE:
          match(TokenType.RANGLE);
          break;
        case LANGLE:
          match(TokenType.LANGLE);
          break;
      }
      last = last.getNext();
    }
    if(root.getNext() != null) {
      return root;
    }
    else {
      return (ConscellNode)root.getFirst();
    }
  }
}
