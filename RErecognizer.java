/* RErecognizer.java
Core class for  Assignment 2: Implementing a Scanner Recognizer and Parser for
  Regular Expressions
Implemented by: Chau Cao
  ULID: c00035898
I certify that the entirety of this implementation is my own work */

import java.io.*;
import java.lang.*;


public class RErecognizer {
  static PrintStream cout = System.out;
  static TokenType curr_type;
  static int curr_char;
  static String curr_line;
  static PushbackReader pbIn;
  static FileReader fileIn;
  static FileReader temp;
  public static void main(String args[]) throws IOException {
    if (args.length < 1) throw new IllegalArgumentException("No Arguments");
      try {
        fileIn = new FileReader(args[0]);
      }
      catch (IOException e) {
        System.out.println("The file " + args[0] + " could not be opened");
      }
      cout.printf("%nEchoing File: %s%n", args[0]);
      echoFile();
      fileIn = new FileReader(args[0]);
      pbIn = new PushbackReader(fileIn);
      temp = new FileReader(args[0]);
      curr_char = -1;
      fullScanner();

      /*
      fileIn = new FileReader(args[1]);
      echoFile(fileIn);
      fileIn = new FileReader(args[1]);
      pbIn = new PushbackReader(fileIn);
      curr_type = getToken();
      recognize_re(0);
      */
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
    BufferedReader currentLine = new BufferedReader(temp);
    String readInLine = "";
    readInLine = currentLine.readLine();
    while(readInLine != null) {
      cout.println("Processing Expression: " + readInLine);
      while(curr_type != TokenType.EOL) {
        getToken();
        if(curr_type == TokenType.LNEGSET) {
          cout.printf("%s: %c%c%n", curr_type, curr_char, 94);
        }
        else if (curr_type == TokenType.EOL)
        {
          cout.printf("%s %n%n", curr_type);
        }
        else {
          cout.printf("%s: %c  %n", curr_type, curr_char);
        }
      }
      readInLine = currentLine.readLine();
      curr_type = TokenType.CHAR;
    }
  }
}
