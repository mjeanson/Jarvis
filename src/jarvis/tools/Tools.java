package jarvis.tools;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisClosure;
import jarvis.atoms.JarvisInt;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;
import java.util.Stack;

public class Tools {
	
	public static JarvisAtom buildIntAtom(int val, JarvisInterpreter ji)
	{
		
		
		return new JarvisInt(val);
		
		
		
	}

	public static JarvisAtom buildClosureAtom(ArrayList<String> params,
			Stack<String> body, JarvisInterpreter ji) {
		
		
		
		
		
		return new JarvisClosure(params,body);
	}
	
	

}
