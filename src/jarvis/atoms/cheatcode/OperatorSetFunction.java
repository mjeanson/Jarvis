package jarvis.atoms.cheatcode;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisBool;
import jarvis.atoms.JarvisString;
import jarvis.atoms.JarvisList;
import jarvis.atoms.JarvisObject;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

public class OperatorSetFunction extends JarvisFunction{

	protected void init() {
		argCount = 2;
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
		
		// Le premier argument se trouve dans la file de l'interpréteur
		// On assume que c'est une string qui represente l'attribut a modifier
		JarvisString member = (JarvisString) ji.getArg();
		
		// Tester si c'est un JarvisObject et recupérer l'attribut value
		
		// Le second argument se trouve dans la file de l'interpréteur
		// C'est la valeur de l'attribut a modifier	
		JarvisAtom nexval = ji.getArg();
		
		// Recuperer la classe de l'objet
		JarvisList attributes = (JarvisList) self.getJarvisClass().message("attributes");
		
		// Trouver la position du membre a modifier dans la liste
		int pos = attributes.find(member);
		
		// Inserer la valeur
		self.setAttribute(pos, nexval);
		
		// Recuperer l'object values au complet
		// Modifier la valeur
		// Reinserer dans l'environement ?
		
		
		//Ici, construit un objet bool manuellement... plus simple qu'avec l'environnement et le new
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();
		data.add(new JarvisBool(true));
		
		return new JarvisObject(((JarvisObject)ji.getEnvironment().get("bool")),data,ji);
	}

	@Override
	public String makeKey() {
		
		return "OperatorSetFunction";
	}

}
