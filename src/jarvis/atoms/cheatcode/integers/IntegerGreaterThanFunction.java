package jarvis.atoms.cheatcode.integers;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisInt;
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


public class IntegerGreaterThanFunction extends JarvisFunction{
	
	protected void init() {
		argCount = 1;
	}

	/*
	 * Méthode qui implante la fonction de plus grand que. 
	 * 
	 */
	
	@Override
	protected JarvisAtom execute(JarvisInterpreter ji,JarvisObject self) {

		//Le premier argument est l'objet qui a reçu le message.
		//On assume ici qu'il contient un attribut "value" 
		//qui contient un atome entier.
		JarvisInt num1 = (JarvisInt) self.message("value");
		
		//Le second argument se trouve dans la file de l'interpréteur.
		JarvisAtom arg2=ji.getArg();
		JarvisInt num2;
		
		//Cet argument peut être un atome entier, ou un objet entier.
		if(arg2 instanceof JarvisInt)
		{
			num2=(JarvisInt)arg2;
			
		}
		else
		{
			//Si c'est un objet entier, il faut aller chercher 
			//son attribut "value", qui devrait être un atome entier.
			num2 =(JarvisInt) ((JarvisObject) arg2).message("value");
		}
			
		//Finalement, le clou du spectacle, une comparaison d'entiers
		boolean greater = num1.getValue() > num2.getValue();
	
		//Ici, construit un objet bool manuellement... plus simple qu'avec l'environnement et le new
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisBool(greater));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("bool")),data,ji);

	}

	@Override
	public String makeKey() {
		
		return "IntegerGreaterThanFunction";
	}

}
