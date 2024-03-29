/* RErecognizer.java
Core class for  Assignment 2: Implementing a Scanner Recognizer and Parser for
  Regular Expressions
Implemented by: Chau Cao
  ULID: c00035898
I certify that the entirety of this implementation is my own work */

import java.io.*;

//The entirety of Project 4 is run from this file
//The correct way to run this program is by running
//  java RErecognizer inputFile1.txt
//from the directory that contains all the .java files and the 2 inputFiles
//from the specifications of the program after compilation
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

      //The following code block carries out the specs of the first 10% of project 2
      fileIn = new FileReader(args[0]);
      cout.printf("%nEchoing File: %s%n", args[0]);
      echoFile();
      fileIn = new FileReader(args[0]);
      pbIn = new PushbackReader(fileIn);
      temp = new FileReader(args[0]);
      currentLine = new BufferedReader(temp);
      fullScanner();

      //The following code block carries out the specs of the following 30% of project 2
      fileIn = new FileReader("inputFile2.txt");
      cout.printf("Echoing File: inputFile2.txt%n");
      echoFile();
      fileIn = new FileReader("inputFile2.txt");
      pbIn = new PushbackReader(fileIn);
      temp = new FileReader("inputFile2.txt");
      currentLine = new BufferedReader(temp);
      curr_line = currentLine.readLine();
      getToken();
      recognize_re(0);

      //The foloowing statement carries out the next 30% of project 2
      REparser parse = new REparser("inputFile2.txt");

      //The following statemente carries out the last 30% of project 2
      //*************NOTE**************
      //The output of the last REGEX read during part 4 is partially incorrect
      //The K_plus operator is being interpreted as a right child of K_char
      //intead of K_qmark and I could not figure out logically how to do the correct
      //node filtering to have it returned as a right child of K_qmark instead of
      //K_char
      REabsparse absparse = new REabsparse("inputFile2.txt");

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
  //Main component of scnnaer
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

  //void fullScanner()
  //Outputs each regex line from file and determins the identity and token type
  //  of each item.
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
          cout.printf("%s %n", curr_type);
        }
      }
      curr_line = currentLine.readLine();
      curr_type = TokenType.CHAR;
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

  //void print_indentation
  //Prints out the passed in indentation level
  static void print_indentation(int level) {
    for(int i = 0; i < level; i++) {
      cout.print("    ");
    }
  }

  //void recognize_re
  //Carries out the first line of the EBNF grammar in the project specs
  //Calls simple_re
  //when that function returns checks if the token type is VERT and calls simple_re again
  //until that token is not present.
  //continues until EOF is reached
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

  //void recognize_simple_re
  //Carries out the second line of the EBNF grammar
  //Calls recognize_basic_re. Calls until VERT, EOL, or RPAREN are found
  static void recognize_simple_re(int level) throws IOException {
    print_indentation(level);
    cout.println("S_RE");

    recognize_basic_re(level + 1);
    while (curr_type != TokenType.VERT && curr_type != TokenType.EOL && curr_type != TokenType.RPAREN) {
      recognize_basic_re(level + 1);
    }
  }

  //void recognize_basic_re
  //Carries out the 3rd line of the EBNF grammar.
  //Calls recognize_elementary_re. Checks for STAR PLUS AND QMARK
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
      }
    }
  }

//void recognize_elementary_re
//Carries out the 4th line of the EBNF grammar
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

  //void recognize_char_or_meta
  //Carries out the 5th line of the EBNF Grammar
  static void recognize_char_or_meta(int level) throws IOException {
    print_indentation(level);
    cout.println("CHAR_OR_META");
    print_indentation(level + 1);
    cout.printf("%c %s%n", curr_char, curr_type);
    if(curr_type == TokenType.BSLASH) {
      match(TokenType.BSLASH);
      print_indentation(level + 1);
      cout.printf("%c %s%n", curr_char, curr_type);
      match(curr_type);
    }
  }

  //void recognize_sitems
  //carries out the 6th line of the EBNF grammar in regards to handling set items.
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
