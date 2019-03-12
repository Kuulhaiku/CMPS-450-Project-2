/* RErecognizer.java
Core class for  Assignment 2: Implementing a Scanner Recognizer and Parser for
  Regular Expressions
Implemented by: Chau Cao
  ULID: c00035898
I certify that the entirety of this implementation is my own work */

import java.io.*;


public class RErecognizer {
  static PrintStream cout = System.out;
  static TokenType curr_type;
  static int curr_char;
  static String curr_line;
  static PushbackReader pbIn;
  static FileReader fileIn;
  static FileReader temp;
  static BufferedReader currentLine;
  public static void main(String args[]) throws IOException {
    if (args.length < 1) throw new IllegalArgumentException("No Arguments");

      fileIn = new FileReader(args[0]);
      cout.printf("%nEchoing File: %s%n", args[0]);
      echoFile();
      fileIn = new FileReader(args[0]);
      pbIn = new PushbackReader(fileIn);
      temp = new FileReader(args[0]);
      currentLine = new BufferedReader(temp);
      fullScanner();



      fileIn = new FileReader("inputFile2.txt");
      cout.printf("%nEchoing File: inputFile2.txt%n");
      echoFile();
      fileIn = new FileReader("inputFile2.txt");
      pbIn = new PushbackReader(fileIn);
      temp = new FileReader("inputFile2.txt");
      currentLine = new BufferedReader(temp);
      curr_line = currentLine.readLine();
      getToken();
      recognize_re(0);

  }

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

  static void getToken() throws IOException{
    curr_char = pbIn.read();
    getNonBlank();
    lookUp();
  }

  static void getNonBlank() throws IOException{
    while(curr_char == 32)
    {
      curr_char = pbIn.read();
    }
  }

  static void fullScanner() throws IOException {
    curr_line = "";
    curr_line = currentLine.readLine();
    while(curr_line != null) {
      cout.println("Processing Expression: \"" + curr_line + "\"");
      while(curr_type != TokenType.EOL) {
        getToken();
        if(curr_type == TokenType.CHAR)
        {
          cout.printf("%s: %c %n", curr_type, curr_char);
        }
        else if(curr_type == TokenType.EOL) {
          cout.printf("%s%n%n", curr_type);
        }
        else {
          cout.printf("%s: %n", curr_type);
        }
      }
      curr_line = currentLine.readLine();
      curr_type = TokenType.CHAR;
    }
  }

  static void match(TokenType ttype) throws IOException {
    if(ttype == curr_type) {
      getToken();
    }
    else {
      cout.println("Match Error: " + ttype);
      System.exit(1);
    }
  }

  static void print_indentation(int level) {
    for(int i = 0; i < level; i++) {
      cout.print("    ");
    }
  }

  static void recognize_re(int level) throws IOException {
    if(level == 0) {
      cout.printf("%nProcessing Expression: \"%s\"%n", curr_line);
    }
    print_indentation(level);
    cout.println("RE");

    recognize_simple_re(level + 1);

    while (curr_type == TokenType.VERT) {
      print_indentation(level + 1);
      cout.printf("%c %s%n", curr_char, curr_type);
      match(TokenType.VERT);
      recognize_simple_re(level + 1);
    }
    if(curr_type == TokenType.EOL && level == 0) {
      curr_line = currentLine.readLine();
      if(curr_line != null) {
        getToken();
        recognize_re(0);
      }
    }
  }

  static void recognize_simple_re(int level) throws IOException {
    print_indentation(level);
    cout.println("S_RE");

    recognize_basic_re(level + 1);

    while (curr_type != TokenType.VERT && curr_type != TokenType.EOL && curr_type != TokenType.RPAREN) {
      recognize_basic_re(level + 1);
    }
  }

  static void recognize_basic_re(int level) throws IOException {
    print_indentation(level);
    cout.println("B_RE");

    recognize_elementary_re(level + 1);

    while (curr_type == TokenType.STAR || curr_type == TokenType.PLUS || curr_type == TokenType.QMARK) {
      print_indentation(level + 1);
      cout.printf("%c %s%n", curr_char, curr_type);
      switch(curr_type) {
        case STAR:
          match(TokenType.STAR);
          break;
        case PLUS:
          match(TokenType.PLUS);
          break;
        case QMARK:
          match(TokenType.QMARK);
          break;
        default:
          recognize_elementary_re(level + 1);
      }
    }
  }

  static void recognize_elementary_re(int level) throws IOException {
    print_indentation(level);
    cout.println("E_RE");
    switch(curr_type) {
      case LPAREN:
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char,  curr_type);
        match(TokenType.LPAREN);
        recognize_re(level + 1);
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char, curr_type);
        match(TokenType.RPAREN);
        break;
      case PERIOD:
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char, curr_type);
        match(TokenType.PERIOD);
        break;
      case LPOSSET:
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char, curr_type);
        match(TokenType.LPOSSET);
        recognize_sitems(level + 1);
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char, curr_type);
        match(TokenType.RSET);
        break;
      case LNEGSET:
        print_indentation(level + 1);
        cout.printf("%c%c %s%n", curr_char, 94,  curr_type);
        match(TokenType.LNEGSET);
        recognize_sitems(level + 1);
        print_indentation(level + 1);
        cout.printf("%c %s%n", curr_char, curr_type);
        match(TokenType.RSET);
        break;
      case CHAR:
        recognize_char_or_meta(level + 1);
        match(TokenType.CHAR);
        break;
      case BSLASH:
        recognize_char_or_meta(level + 1);
        break;
      case RANGLE:
        recognize_char_or_meta(level + 1);
        match(TokenType.RANGLE);
        break;
      case LANGLE:
        recognize_char_or_meta(level + 1);
        match(TokenType.LANGLE);
        break;
    }
  }
  static void recognize_char_or_meta(int level) throws IOException {
    print_indentation(level);
    cout.println("CHAR_OR_META");
    print_indentation(level + 1);
    cout.printf("%s %c%n", curr_type, curr_char);
    if(curr_type == TokenType.BSLASH) {
      match(TokenType.BSLASH);
      recognize_char_or_meta(level);
      match(curr_type);
    }
  }

  static void recognize_sitems(int level) throws IOException {
    print_indentation(level);
    cout.println("SITEMS");
    while(curr_type != TokenType.RSET) {
      recognize_char_or_meta(level + 1);
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
    }
  }
}
