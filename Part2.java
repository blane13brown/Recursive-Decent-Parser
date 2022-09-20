import java.io.IOException;
import java.lang.StringBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

	private static String content = "";
	private static String input_token = "";
	private static String tokenContent = "";
	private static StringBuffer Tree = new StringBuffer();
	
	public static void main(String[] args) throws IOException {

		System.out.println("Enter your file name (including the .txt):");
		Scanner scanner = new Scanner(System.in);
		String fileName = scanner.nextLine();
		scanner.close();
		Path filePath = Path.of(fileName);
		content = Files.readString(filePath);
		content = content.replace("\r", ""); 
		
		System.out.println(content);
		input_token = Scan();
		System.out.println(Program());
		System.out.println(Tree);
	}
	
	private static String Program() {
		Tree.append("<Program>\n");
		switch (input_token) {
			case "id": 
			case "read":
			case "write":
			case "$$":
				if(stmt_list().equals("ok")) {
					Tree.append("</Program>");
					return match("$$");
				}
				else
					return "Error";
			default: return "Error";
		}
	}
	
	private static String stmt_list() {
		Tree.append("<stmt_list>\n");
		String answer;
		switch (input_token) {
			case "id":
			case "read":
			case "write":
				if(stmt() == "ok") {
					answer = stmt_list();
				}
				else
					answer = "Error";
				break;
			case "$$":
				answer = "ok";
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</stmt_list>\n");
		return answer;
	}
	
	private static String stmt() {
		Tree.append("<stmt>\n");
		String answer;
		switch(input_token) {
			case "id":
				match("id");
				if (match("assign") == "ok")
					answer = expr();
				else
					answer = "Error";
				break;
			case "read":
				match("read");
				answer = match("id");
				break;
			case "write": match("write");
				answer = expr();
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</stmt>\n");
		return answer;
	}
	
	private static String expr() {
		Tree.append("<expr>\n");
		String answer;
		switch(input_token) {
			case "lparen": 
			case "id":
			case "number":
				if(term() == "ok")
					answer = term_tail();
				else
					answer = "Error";
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</expr>\n");
		return answer;
	}
	
	private static String term() {
		Tree.append("<term>\n");
		String answer;
		switch(input_token) {
			case "lparen": 
			case "id":
			case "number":
				if(factor() == "ok")
					answer = factor_tail();
				else
					answer = "Error";
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</term>\n");
		return answer;
	}
	
	private static String term_tail() {
		Tree.append("<term_tail>\n");
		String answer;
		switch(input_token) {
			case "minus": 
			case "plus":
				if(add_op() == "ok")
					if(term() == "ok")
						answer = term_tail();
					else
						answer = "Error";
				else
					answer = "Error";
				break;
			case "rparen":
			case "id":
			case "read":
			case "write":
			case "$$": answer = "ok";
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</term_tail>\n");
		return answer;
	}
	
	private static String factor() {
		Tree.append("<factor>\n");
		String answer;
		switch(input_token) {
			case "lparen": match("lparen");
				if (expr() == "ok")
					answer = match("rparen");
				else
					answer = "Error";
				break;
			case "id": answer = match("id");
				break;
			case "number": answer = match("number");
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</factor>\n");
		return answer;
	}
	
	private static String factor_tail() {
		Tree.append("<factor_tail>\n");
		String answer = "";
		switch(input_token) {
			case "times": 
			case "div": 
				if(mult_op().equals("ok"))
					if(factor().equals("ok"))
						answer = factor_tail();
				break;
			case "plus":
			case "minus":
			case "rparen":
			case "id":
			case "read":
			case "write":
			case "$$": answer = "ok";
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</factor_tail>\n");
		return answer;
	}
	
	private static String add_op() {
		Tree.append("<add_op>\n");
		String answer;
		switch (input_token) {
			case "plus": answer = match("plus");
				break;
			case "minus": answer = match("minus");
				break;
			default: answer = "Error";
				break;
		}
		Tree.append("</add_op>\n");
		return answer;
	}
	
	private static String mult_op() {
		Tree.append("<mult_op>\n");
		String answer;
		switch (input_token) {
		case "times": answer = match("times");
			break;
		case "div": answer = match("div");
			break;
		default: answer = "Error";
			break;
		}
		Tree.append("</mult_op>\n");
		return answer;
	}
	
	private static String match(String expectedToken) {
		if(expectedToken == input_token) {
			if(expectedToken != "$$") 
				Tree.append("<" + expectedToken + ">\n" + "    " + tokenContent + "\n" + "</" + expectedToken + ">\n");
			if(content.contains("$$") && content.length() <= 3) {
				input_token = "$$";
			}
			else
				input_token = Scan();
			return "ok";
		}
		else
			return "Error";
	}
	
	private static String Scan() {
		int index = 0;
		int currState = 1;
		char currChar = content.charAt(index);
		String token = "";
		
		//while our index isn't that of the last character in s
		while(index < content.length() - 1) {
			if(TransitionTable(currChar, currState) != 0) {
				if(TransitionTable(currChar, currState) == -1)
					return "ERROR";
				index++;
				currState = TransitionTable(currChar, currState);
				currChar = content.charAt(index);
			}
			else {
				if (FinalStateToToken(currState, token) != "ERROR") {
					token = content.substring(0, index);
					content = content.substring(index, content.length());
					return FinalStateToToken(currState, token);
				}
				else {
					return "ERROR";
				}
			}
		}
		//if there is only 1 character left in s that we havn't read, come here instead of while loop
		if(TransitionTable(currChar, currState) == 0) {
				token = content.substring(0, index);
				content = content.substring(index, content.length());
				return FinalStateToToken(currState, token);
		}
		currState = TransitionTable(currChar, currState);
		token = content;
		content = "";
		return FinalStateToToken(currState, token);
	}
	
	private static int TransitionTable(char currChar, int currState) {
		//transition table so we know where to go next
		int[][] nextStateTable = {
				{1, 1, 2, 10, 6, 7, 8, 9, 11,  -1, 13, 14, 16, -1},
				{0, 0, 3,  4, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{3, 1, 3,  3, 3, 3, 3, 3,  3,  3,  3,  3,   3,  3},
				{4, 4, 4,  5, 4, 4, 4, 4,  4,  4,  4,  4,   4,  4},
				{4, 4, 1,  5, 4, 4, 4, 4,  4,  4,  4,  4,   4,  4},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0, 12,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  0,   0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  15,  0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0, 15,  14,  0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  15,  0, -1},
				{0, 0, 0,  0, 0, 0, 0, 0,  0,  0,  0,  16, 16, -1},
				
		};
		
		int currCharNum = 0;
		
		//turn character into its corresponding number so we can index the transition table
		if(currChar == ' ')
			currCharNum = 1;
		else if(currChar == '\n')
			currCharNum = 2;
		else if(currChar == '/')
			currCharNum = 3;
		else if(currChar == '*')
			currCharNum = 4;
		else if(currChar == '(')
			currCharNum = 5;
		else if(currChar == ')')
			currCharNum = 6;
		else if(currChar == '+')
			currCharNum = 7;
		else if(currChar == '-')
			currCharNum = 8;
		else if(currChar == ':')
			currCharNum = 9;
		else if(currChar == '=')
			currCharNum = 10;
		else if(currChar == '.')
			currCharNum = 11;
		else if(currChar >= '0' && currChar <= '9')
			currCharNum = 12;
		else if(currChar >= 'a' && currChar <= 'z')
			currCharNum = 13;
		else if(currChar >= 'A' && currChar <= 'Z')
			currCharNum = 13;
		else //character not in the alphabet, example: $ or \
			currCharNum = 14;
		
		//return the next state
		return nextStateTable[currState - 1][currCharNum - 1];
	}
	
	private static String FinalStateToToken(int currState, String token) {
		//return token type depending on if were in a final state or not
		String[] tokenTypes = {"start", "div", "ERROR", "ERROR", "ERROR", "lparen", "rparen", "plus", "minus", "times", "ERROR", "assign", "ERROR", "number", "number", "id"};
		
		tokenContent = token.replaceAll(" ", "");
		
		//if our current state is -1 were in a error state meaning we found a character not in our alphabet 
		if (currState == -1)
			return "ERROR";
		//checks for read and write since they are keywords
		else if (token.replaceAll(" ", "").equalsIgnoreCase("read")) 
			return "read";
		else if (token.replaceAll(" ", "").equalsIgnoreCase("write")) 
			return "write";
		else 
			return tokenTypes[currState-1];
	
	}
}