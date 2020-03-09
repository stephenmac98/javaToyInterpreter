/*
 * Shannon Duvall and <Stephen Blackwell>
 * This object does basic reflection functions
 */
import java.lang.reflect.*;

public class ReflectionUtilities {
	
	/* Given a class and an object, tell whether or not the object represents 
	 * either an int or an Integer, and the class is also either int or Integer.
	 * This is really yucky, but the reflection things we need like Class.isInstance(arg)
	 * just don't work when the arg is a primitive.  Luckily, we're only worrying with ints.
	 * This method works - don't change it.
	 * 
	 * Note that if the inputs aren't integers at all it just returns false.
	 */
	private static boolean typesMatchInts(Class<?> maybeIntClass, Object maybeIntObj){
		//System.out.println("I'm checking on "+maybeIntObj);
		//System.out.println(maybeIntObj.getClass());
		try{
			return (maybeIntClass == int.class) &&
				(int.class.isAssignableFrom(maybeIntObj.getClass()) || 
						maybeIntObj.getClass()==Class.forName("java.lang.Integer"));
		}
		catch(ClassNotFoundException e){
			return false;
		}
	}
	
	/*
	 * TODO: typesMatch
	 * Takes an array of Classes and an array of Objects.
	 * This method should return true if and only if the following 
	 * two things are true:
	 * 1) The 2 arrays are the same length
	 * 2) The ith Object has a type equal to the ith Class.
	 * 
	 * For examples:
	 * typesMatch([String.class, int.class], ["hey", 3]) returns true
	 * typesMatch([],[]) returns true
	 * typesMatch([String.class], [3]) returns false
	 * typesMatch([String.class, String.class], ["hey"]) returns false
	 * 
	 * Note: call my typesMatchInts method to see if an object and class
	 * match as int types.  If it returns false, check if they match 
	 * using isInstance.
	 */
	
	public static boolean typesMatch(Class<?>[] formals, Object[] actuals){
		System.out.println("----------\nRUNNING typesMatch\n----------\n");
		if(formals.length == actuals.length) {
			
			for(int i = 0; i < formals.length; i++) {
				Object act = actuals[i];
				Class form = formals[i];
				Integer filler = 3;
				if( ! form.isInstance(act) && ! typesMatchInts(form, act)) {
					return false;
				}
			}
			System.out.println("PASS");
			System.out.println();
			return true;
		}
		System.out.println("FAIL: Your two inputs were different lengths");
		System.out.println();
		return false;
	}
	
	
	
	
	
	/*
	 * TODO: createInstance
	 * Given String representing fully qualified name of a class and and
	 * object array containing the actual parameters for a constructor, 
	 * returns initialized instance of the corresponding 
	 * class using the matching constructor.  
	 * 
	 * Examples:
	 * createInstance("java.lang.String", []) returns an empty String.
	 * createInstance("java.lang.Integer", [3]) returns a new Integer, 3.
	 * 
	 * You need to call typeMatch (above) to do this correctly.  Use the class to 
	 * get all the Constructors, then check each one to see if the types of the
	 * constructor parameters match the actual parameters given. Once a match
	 * is found, use the "newInstance" method from the constructor class to create
	 * and return an instance of the class.
	 */
	
	
	
	public static Object createInstance(String name, Object[] args){
		System.out.println("----------\nRUNNING createInstance\n----------\n");
		try {
			int len = args.length;
			Class c = Class.forName(name);
			Class<?>[] objClasses = new Class<?>[len];
			for(int i = 0; i < len; i++) {
				Class<?> objClass = args[i].getClass();
				
				
				objClasses[i] = objClass;
				
			}
			Constructor con = c.getConstructor(objClasses);
			Object finObj = con.newInstance(args);
			
			return finObj;
			
		} catch (ClassNotFoundException e) {
			System.out.println("Your class name could not be found");
		} catch (NoSuchMethodException e) {
			System.out.println("Method could not be found");
		} catch (InvocationTargetException e) {
			System.out.println("Objects could not be passes as arguments");
		} catch (IllegalAccessException e) {
			System.out.println("Objects could not be passes as arguments");
		}catch (InstantiationException e) {
			System.out.println("Objects could not be passes as arguments");
		}
		return null;
	}
	
	/*
	 * TODO: callMethod
	 * Given a target object with a method of the given name that takes 
	 * the given actual parameters, call the named method on that object 
	 * and return the result. 
	 * 
	 * If the method's return type is void, null is returned, but the 
	 * method should still be invoked.
	 * 
	 * Again, to do this correctly, you should get a list of the object's 
	 * methods that match the method name you are given.  Then check each one to 
	 * see which has formal parameters to match the actual parameters given.  When
	 * you find one that matches, invoke it (using the "invoke" method from the Method class).
	 */
	public static Object callMethod (Object target, String methodName, Object[] args)
	{
		System.out.println("----------\nRUNNING callMethod\n----------\n");		//System.out.println("RU: METHODNAME: " + methodName);
		Class<?> c = target.getClass();
		Class<?>[] argsClass = new Class[args.length];
		Class<?>[] argsClassInteger = new Class[args.length];
		//System.out.println("RU: RETURN CLASSTYPE: " + c.getSimpleName());
		Method[] methods = c.getMethods();
//		for(Method met: methods) {
//			System.out.println("METHOD: " + met.getName());
//		}
		
		for(int i = 0; i < args.length; i++) {
			Object obj = args[i];
			Class cls = obj.getClass();
			//TODO: MAKE THIS BETTER *****
			//I don't understand how to deal with some methods needing Integer
			//and some methods needing int to look them up
			//this is just a patch job where it runs both options
			//there must be a better way
			if(cls == Integer.class) {
				argsClass[i] = int.class;
			}
			else {
				argsClass[i] = cls;
			}
			argsClassInteger[i] = cls;
			
			//System.out.println("ARG: " + obj.toString());
			//System.out.println("	ARG-Type: " + cls.getSimpleName());
		}
		try {
			Method m;
			try {
				m = c.getMethod(methodName, argsClass);
			} catch (NoSuchMethodException e) {
				m = c.getMethod(methodName, argsClassInteger);
			}
	
			System.out.println();
			return m.invoke(target, args);
			
		} catch (NoSuchMethodException e) {
			System.out.println("RU: METHOD NOT FOUND");
		} catch (SecurityException e) {
			System.out.println("RU: SECURITY EXCEPTION");
		} catch (InvocationTargetException e) {
			System.out.println("RU: SECURITY EXCEPTION");
		} catch (IllegalAccessException e){
			System.out.println("RU: SECURITY EXCEPTION");
		}
		return null;
	}
	
	
	
}
