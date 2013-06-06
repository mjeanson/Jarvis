package jarvis.interpreter;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisDictionnary;

//SOUSLECAPOT
/*
 * Implantation d'un environnement.
 * Un environnement contient tout simplement
 * une table de hashage permettant d'associer
 * des symboles à des valeurs.
 * De plus, les environnements sont enchaînés entre eux.
 * Lorsqu'on évalue une fermeture, on entre dans 
 * un sous-environnement. Une fois la fermeture 
 * évaluée, on revient dans l'environnement précédent.
 * Un environnement enfant a accès à tous les symboles définis
 * dans ses environnements parents. 
 * 
 */

public class JarvisEnvironment {

	private JarvisDictionnary dictionnary;

	private JarvisEnvironment parent;
	private JarvisInterpreter interpreter;

	

	public JarvisEnvironment(JarvisInterpreter ji) {

		dictionnary = new JarvisDictionnary();
		parent = null;
		
		interpreter = ji;

	}

	public JarvisEnvironment(JarvisInterpreter ji,JarvisEnvironment p) {

		dictionnary = new JarvisDictionnary();
		parent = p;
		
		interpreter = ji;

	}

	public void put(String id, JarvisAtom val) {

		dictionnary.put(id, val);
	}

	public JarvisAtom getLocal(String id)
	{
		return  dictionnary.get(id);
	}
	public JarvisAtom get(String id) {

		// recurse parent...
		JarvisAtom res = dictionnary.get(id);
		if (res == null) {
			if (hasParent()) {
				return parent.get(id);
			}

		}
		return res;

	}

	public void print() {
		dictionnary.print(interpreter);
		
	}

	public boolean hasParent() {
		
		return parent!=null;
	}

	public JarvisEnvironment getParent() {
		
		return parent;
	}

	public void get(JarvisAtom atom) {
		
		get(atom.makeKey());
		
	}

}
