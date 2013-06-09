package jarvis.atoms.cheatcode.integers;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisBool;
import jarvis.atoms.JarvisInt;
import jarvis.atoms.JarvisObject;
import jarvis.atoms.cheatcode.JarvisFunction;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

public class IntegerEqualsFunction extends JarvisFunction{

	protected void init() {
		argCount = 1;
	}

	//FONCTIONSTRICHEUSES
	/*
	 * Cette fonction donne un exemple d'une situation où on doit retourner
	 * un objet bool.
	 * 
	 */
	
	
	@Override
	protected JarvisAtom execute(JarvisInterpreter ji,JarvisObject self) {

		
		JarvisInt num1 = (JarvisInt) self.message("value");
		
		
		JarvisAtom arg2=ji.getArg();
		JarvisInt num2;
		
		if(arg2 instanceof JarvisInt)
		{
			num2=(JarvisInt)arg2;
			
		}
		else
		{
			num2 =(JarvisInt) ((JarvisObject) arg2).message("value");
		}
			
		
		
		boolean result = num1.getValue() == num2.getValue();
	
		
		
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisBool(result));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("bool")),data,ji);

	}

	@Override
	public String makeKey() {
		
		return "IntegerEqualsFunction";
	}

}
