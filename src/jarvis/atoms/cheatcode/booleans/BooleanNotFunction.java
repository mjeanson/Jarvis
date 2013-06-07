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


public class BooleanNotFunction extends JarvisFunction{
	
	protected void init() {
		argCount = 0;
	}

	/*
	 * Méthode qui implante la fonction de NOT boolean. 
	 * 
	 */
	
	@Override
	protected JarvisAtom execute(JarvisInterpreter ji,JarvisObject self) {

		//Le premier argument est l'objet qui a reçu le message.
		//On assume ici qu'il contient un attribut "value" 
		//qui contient un atome boolean.
		JarvisBool bool1 = (JarvisBool) self.message("value");
		
		
		//Finalement, le clou du spectacle, on flip le bool
		boolean not = !bool1.getValue();
	
		//Ici, construit un objet bool manuellement... plus simple qu'avec l'environnement et le new
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisBool(not));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("bool")),data);

	}

	@Override
	public String makeKey() {
		
		return "BooleanNotFunction";
	}
}