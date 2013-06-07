package jarvis.atoms.cheatcode.booleans;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisBool;
import jarvis.atoms.JarvisObject;
import jarvis.atoms.cheatcode.JarvisFunction;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

//FONCTIONSTRICHEUSES
/*
 * Cette fonction est un bon exemple de fonction tricheuse
 * implantée pour un des types de base.
 * Elle doit être appelée par un objet de type int.
 * Elle peut prendre comme second argument un objet de type int,
 * ou un atome JarvisInt.
 * 
 */


public class BooleanOrFunction extends JarvisFunction{
	
	protected void init() {
		argCount = 1;
	}

	/*
	 * Méthode qui implante la fonction de NOT boolean. 
	 * 
	 */
	
	@Override
	protected JarvisAtom execute(JarvisInterpreter ji,JarvisObject self) {

		//Le premier argument est l'objet qui a reçu le message.
		//On assume ici qu'il contient un attribut "value" 
		//qui contient un atome bool.
		JarvisBool bool1 = (JarvisBool) self.message("value");
		
		//Le second argument se trouve dans la file de l'interpréteur.
		JarvisAtom arg2=ji.getArg();
		JarvisBool bool2;
		
		//Cet argument peut être un atome bool, ou un objet bool.
		if(arg2 instanceof JarvisBool)
		{
			bool2=(JarvisBool)arg2;
			
		}
		else
		{
			//Si c'est un objet bool, il faut aller chercher 
			//son attribut "value", qui devrait être un atome bool.
			bool2 =(JarvisBool) ((JarvisObject) arg2).message("value");
		}
			
		//Finalement, le clou du spectacle, un OU boolean
		boolean or = bool1.getValue() || bool2.getValue();
		
		//Ici, construit un objet bool manuellement... plus simple qu'avec l'environnement et le new
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisBool(or));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("bool")),data);

	}

	@Override
	public String makeKey() {
		
		return "BooleanOrFunction";
	}
}