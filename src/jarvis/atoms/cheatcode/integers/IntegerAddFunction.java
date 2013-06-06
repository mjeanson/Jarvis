package jarvis.atoms.cheatcode.integers;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisInt;
import jarvis.atoms.JarvisObject;
import jarvis.atoms.cheatcode.JarvisFunction;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

public class IntegerAddFunction extends JarvisFunction {

	protected void init() {
		argCount = 1;
	}

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
			
		
		
		int total = num1.getValue() + num2.getValue();
	
		
		
		
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisInt(total));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("int")),data);

	}

	@Override
	public String makeKey() {
		
		return "IntegerAddFunction";
	}

	

}
