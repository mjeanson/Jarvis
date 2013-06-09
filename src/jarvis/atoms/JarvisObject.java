package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

/*
 * Cette classe implante l'objet de base.
 * L'interpréteur comprend un objet comme
 * une simple liste de valeurs.
 * L'organisation de ses données est spécifiée
 * par la classe. Celle-ci peut être retrouvée
 * via le lien classReference.
 */
public class JarvisObject extends JarvisAtom {

	/*
	 * Si vous ajoutez des champs à JarvisClass
	 * ces constantes doivent le refléter.
	 * Elles sont utilisées pour retrouver
	 * les membres d'une classe.
	 * 
	 */
	public static final int ATTRIBUTE_FIELD =0;
	public static final int METHOD_FIELD =1;
	public static final int SUPER_FIELD =2;
	
	/*
	 * Référence à la classe de cet objet.
	 */
	private JarvisObject classReference;
	private ArrayList<JarvisAtom> values;
	
	//Référence utile pour faire des reverse lookup
	private JarvisInterpreter ji;

	// Constructeur d'objet générique
	// Utilisé comme raccourci par les fonctions tricheuses.
	public JarvisObject(JarvisObject theClass, ArrayList<JarvisAtom> vals,JarvisInterpreter ji) {

		classReference = theClass;

		values = new ArrayList<JarvisAtom>();
		values.addAll(vals);
		
		this.ji=ji;
	}
	
	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {	
		return this;
	}

	public JarvisObject getJarvisClass() {
		return classReference;
	}
	
	
	//Cas spécial où le selecteur n'est pas encore encapsulé dans un atome
	//Supporté pour alléger la syntaxe.
	public JarvisAtom message(String selector) {
		
		return message(new JarvisString(selector));
		
	}
	
    //HÉRITAGE
	//VARIABLESCLASSE
	/*
	 * Algorithme de gestion des messages.
	 * Ce bout de code a pour responsabilité de déterminer si le message
	 * concerne un attribut ou une méthode. 
	 * Pour implanter l'héritage, cet algorithme doit nécessairement être modifié.
	 */	
	public JarvisAtom message(JarvisAtom selector) {
		
		
		//Va chercher les attributs
		JarvisList members = (JarvisList) classReference.values.get(ATTRIBUTE_FIELD);
		


		//Vérifie si c'est un attribut 
		int pos = members.find(selector);
		
		
		if (pos == -1) {
			// pas un attribut...
			
			// Va chercher les méthodes
			JarvisDictionnary methods = (JarvisDictionnary) classReference.values
					.get(METHOD_FIELD);

			// Cherche dans le dictionnaire
			JarvisAtom res = methods.get(selector.makeKey());

			if (res == null) {
				// Ce n'est pas une methode locale
				
				// Aller chercher l'objet de la super classe
				System.out.print(classReference);
				JarvisAtom classSuper = (JarvisAtom) classReference.values.get(SUPER_FIELD);
				System.out.print(classSuper);
				
				// Vérifier si on est arrivé à la racine de l'arbre d'héritage
				if ((classSuper instanceof JarvisString) && ((JarvisString) classSuper == new JarvisString("NULL")) )
				{
					// On est arrivé a la racine, rien ne correspond au message
					return new JarvisString("SuperComprendPas "+ selector);	
				} else {
					// Sinon chercher recursivement dans les membres de la superclasse
					System.out.print("Recherche recursive pour la methode: ");
					return ((JarvisObject) classSuper).message(selector);
				}
			} else {
				//C'est une méthode locale
				return res;
			}

		}

		else {
			//C'est un attribut.
			return values.get(pos);
		}
		
		
	}

	public void setClass(JarvisObject theClass) {
		classReference = theClass;
	}

	
	
	//Surtout utile pour l'affichage dans ce cas-ci...
	@Override
	public String makeKey() {
		String s="";
		int i=0;
		
		s += "\""+ji.getEnvironment().reverseLookup(classReference)+"\":";
		
		for (JarvisAtom atom : values) {
			
			s+=" "+((JarvisList)classReference.values.get(0)).get(i).makeKey()+":";
			if(atom instanceof JarvisClosure)
			{
				s+=atom;
			}
			else
			{
				s+=atom.makeKey();
			}
			
			i++;
		}
		
		return s;
	}	

}
