import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Proj1 {

	private static String content = "";
	
	public static void main(String[] args) throws IOException {

		System.out.println("Enter your file name (including the .txt):");
		Scanner scanner = new Scanner(System.in);
		String fileName = scanner.nextLine();
		scanner.close();
		//String fileName = "testtxt.txt";
		Path filePath = Path.of(fileName);
		content = Files.readString(filePath);
		content = content.replace("\r", ""); 
		
		//currently "//" does not work unless there is a new line after it, is this intended? by the looks of the automata it is

		ArrayList<String> tokens = new ArrayList<String>();
		boolean wasError = false;
		//System.out.println(content); //used for testing what string looks like after taken from file
		
		while(!content.isEmpty()) {
			String token = Scan();
			
			if(token != "ERROR") {
				if(token != "start")
					tokens.add(token);
			}
			else {
				System.out.println("error.");
				wasError = true;
				break;
			}
		}

		if(!wasError)
			if(tokens.isEmpty())
				System.out.println("No tokens found.");
			else	
				System.out.println(tokens);
		
		
		
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
