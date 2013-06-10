package jarvis.atoms.cheatcode;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisList;
import jarvis.atoms.JarvisObject;
import jarvis.atoms.JarvisString;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

public class OperatorNewFunction extends JarvisFunction{

	/*
	 * Triche pour pouvoir avoir des arguments variables.
	 * Le nombre d'arguments nécessaire est déterminé par la taille
	 * de la liste des attributs de la classe qui crée l'instance.
	 */
	protected void init() {
		argCount = -1;
	}
	
	
	//HÉRITAGE	
	/*
	 * Operator new. 
	 * Rien de bien mystérieux: Lorsqu'on fabrique un objet
	 * il suffit de prendre les arguments reçus et de les copier
	 * un à un dans l'objet qu'on fabrique.
	 * Devient plus complexe si on hérite les membres d'une autre classe.
	 * 
	 */
	@Override
	protected JarvisAtom execute(JarvisInterpreter ji,JarvisObject self) {	
		
		
		//Seule une classe peut faire new. Ramasser de la classe combien d'attributs ça prend.
		
		JarvisList attributes = (JarvisList)self.message("attributes");
		
		JarvisAtom superclass = (JarvisAtom)self.message("super");
		
		JarvisList superattributes;
		
		//System.out.println("Ma super classe est: " + superclass.makeKey());
		
		while (true) {
			if (superclass instanceof JarvisString) {
				// On a atteint la racine de l'arbre
				//System.out.println("On a atteint la racine: " + superclass.makeKey());
				break;
			} else {
				superattributes = (JarvisList) ((JarvisObject) superclass).message("attributes");
				for(int i=0;i<superattributes.size();i++) {
					//System.out.println("Attribut: " + superattributes.get(i));
					// Si l'attribut n'existe pas deja, l'ajouter
					if (attributes.size() == 0) {
						//System.out.println("Vide: " + superattributes.get(i).makeKey());
					} else if (attributes.indexOf(superattributes.get(i)) == -1) {
						System.out.println("Ajouter ATT: " + superattributes.get(i).makeKey());
						attributes.add(superattributes.get(i));
					} else {
						//System.out.println("DOUBLE: " + superattributes.get(i).makeKey());
					}
				}
				// Allez chercher la prochaine superclasse
				superclass = (JarvisAtom) ((JarvisObject) superclass).message("super");
			}
		}
		
		System.out.println("Attributes: " + attributes.makeKey());
		
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		for(int i=0;i<attributes.size();i++)
		{
			if(ji.getArgCount()<=0)
			{
				throw new IllegalArgumentException("Operator new: Bad number of arguments. Expected "+attributes.size()+" got "+i);
			}
			data.add(ji.getArg());
		}		
				
		JarvisObject res = new JarvisObject(self, data,ji);		
		
		return res;		
		
	}

	@Override
	public String makeKey() {
		
		return "OperatorNewFunction";
	}

}
