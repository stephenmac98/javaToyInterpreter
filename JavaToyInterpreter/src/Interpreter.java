/*
 * Shannon Duvall and <Put your name here.>
 * A small Java Interpreter
 * Practice using reflection and understanding how Java works.
 */

import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {
	// The symbol table is a dictionary of variable identifiers to bindings.
	private HashMap<String, Object> mySymbolTable = new HashMap<String, Object>();
	private Parser myParser;
	
	public Interpreter(){
		mySymbolTable = new HashMap<String,Object>();
		myParser = new Parser();
	}
	
	/*
	 * Continually ask the user to enter a line of code.
	 */
	public void promptLoop(){
		System.out.println("This is a simple interpreter.  I'm not a good compiler, so be careful and follow my special rules:\n" +
				"Each class name should be fully qualified, \n"+
				"I only create objects and call methods. \n" +
				"I only use literals of integers and Strings. \n"+
				"Enter 'Q' to quit.");
		Scanner scan = new Scanner(System.in);
		String line = scan.nextLine();
		while(!line.equalsIgnoreCase("q")){
			// Find the important words out of the line.
			ParseResults parse = myParser.parse(line);
			// Do the command, and give a String response telling what happened.
			String answer = process(parse);
			System.out.println(answer);
			line = scan.nextLine();
		}
		scan.close();
	}
	
	/*
	 * This method does the work of carrying out the intended command.
	 * The input is a ParseResults object with all the important information
	 * from the typed line identified.  The output is the String to print telling
	 * the result of carrying out the command.
	 */
	public String process(ParseResults parse){
		//System.out.println(parse);
		if (parse.isMethodCall){
			return callMethod(parse);
		}
		else return makeObject(parse);
		
	}
	
	/*
	 * TODO: convertNameToInstance
	 * Given a name (identifier or literal) return the object.
	 * If the name is in the symbol table, you can just return the object it is 
	 * associated with.  If it's not in the symbol table, then it is either 
	 * a String literal or it is an integer literal.  Check to see if the 
	 * first character is quotation marks.  If so, create a new String object to
	 * return that has the same characters as the name, just without the quotes.
	 * If there aren't quotation marks, then it is an integer literal.  Use Integer.parseInt
	 * to turn the String into an int.
	 * 
	 * Examples:  Suppose symbol table has:
	 * "x" -> 3
	 * convertNameToInstance("x") returns 3.
	 * convertNameToInstance("4") returns 4.
	 * convertNameToInstance(""hey"") returns "hey"
	 */
	public Object convertNameToInstance(String name){
		if(mySymbolTable.containsKey(name)) {
			return mySymbolTable.get(name);
		}
		else if (name.charAt(0) == '"') {
			return name.substring(1, name.length() - 1);
		}
		else {
			int ans = Integer.parseInt(name);
			return ans;
		}
	}
	
	
	/*TODO: convertNameToInstance.  
	 * Takes an array of Strings and converts all of them to their associated objects.
	 * Simply call the other helper method with the same name as this method (above)
	 * on each item in the array, store the result in an Object array, and return the new Object array.
	 * (The key problem here is that String Objects come with an extra set of double quotes
	 * that we do not want)
	 */
	public Object[] convertNameToInstance(String[] names){
		System.out.println("----------\nRUNNING convertNameToInstance \n----------\n");
		int len = names.length;
		Object[] objs = new Object[len];
		for(int i = 0; i < len; i++) {
			objs[i] = convertNameToInstance(names[i]);
		}
		return objs;
	}
	
	
	/* TODO: makeObject
	 * This method does the "process" job for making objects.
	 * This is where we use the ReflectionUtilities.
	 * Don't forget to add the new object to the symbol table.
	 * The String that is returned should be a basic message telling what happened.
	 */
	public String makeObject(ParseResults parse){	
		System.out.println("----------\nRUNNING makeObject\n----------\n");
		// HINT: Looking at the ParseResults class can help you make sense of 
		// what information you receive and how to access the information here.
		
		// Once you collect the correct parsed information, use ReflectionUtilities to 
		// make an object and add it to the symbol table.
		
		// This method will also have to print an appropriate statement to give feedback
		// to the user (see examples in the project description)
		
		String className = parse.className;
		String objName = parse.objectName;
		
		Object[] args = convertNameToInstance(parse.argumentNames);
		
		Object result = ReflectionUtilities.createInstance(className, args);
		
		
		
		if(result != null) {
			mySymbolTable.put(objName, result);
			return "The object " + objName + " has been created and added to the symbol table";
		}
		
		return "The object could not be created and has not been added to the symbol table";
	}
	
	/*
	 * TODO: callMethod
	 * This method does the "process" job for calling methods.
	 * You MUST use the ReflectionUtilities to call the method for 
	 * this to work, because ints can't be directly transformed into Objects.
	 * When you call a method, if it has an output, be sure to 
	 * either create a new object for that output or change the 
	 * existing object.  This will require either adding a new 
	 * thing to the symbol table or removing what is currently there
	 * and replacing it with something else.
	 */
	public String callMethod(ParseResults parse){
		System.out.println("----------\nRUNNING callMethod\n----------\n");
		// HINT: Looking at the ParseResults class can help you make sense of 
		// what information you receive and how to access the information here.
				
		// This method will also have to print an appropriate statement to give feedback
		// to the user (see examples in the project description). This printout may depend
		// on what the method accomplishes (for example, it may change values or create new objects)
		
		String returnType = parse.answerTypeName;
		String finObjName = parse.answerName;
		String methodName =  parse.methodName;
		String objName = parse.objectName;
		
		;
				
//		System.out.println("finObjName: " + finObjName);
//		System.out.println("objName: " + objName);
//		System.out.println("returnType: " + returnType);
//		System.out.println("methodName: " + methodName);
		

		
		//System.out.println("NAME: " + mySymbolTable.get(objName).getClass().getSimpleName());
		
		Object[] args = convertNameToInstance(parse.argumentNames);
		
		
		Object result = ReflectionUtilities.callMethod(mySymbolTable.get(objName), methodName, args);
		
		System.out.println("RESULT: " + result);
		if(mySymbolTable.containsKey(finObjName)) {
			mySymbolTable.replace(finObjName, result);
			return "The value of object: " + finObjName + 
					" has been changed to: " + result.toString();
		}
		mySymbolTable.put(finObjName, result);
		
		if(result != null) {
			return "Object: " + finObjName + 
					" has been created with value: " + result.toString();
		}
		else {
			return "I performed the operation and it returned null";
		}
	}

}
