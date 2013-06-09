package jarvis.interpreter;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisCommand;
import jarvis.atoms.JarvisDictionnary;
import jarvis.atoms.JarvisList;
import jarvis.atoms.JarvisObject;
import jarvis.atoms.JarvisString;
import jarvis.atoms.cheatcode.OperatorNewFunction;
import jarvis.atoms.cheatcode.integers.IntegerAddFunction;
import jarvis.atoms.cheatcode.integers.IntegerEqualsFunction;
import jarvis.atoms.cheatcode.integers.IntegerMultiplyFunction;
import jarvis.atoms.cheatcode.integers.IntegerSubtractFunction;
import jarvis.exceptions.UndefinedSymbolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;

public class JarvisInterpreter {

	// Référence à l'environnement courant
	private JarvisEnvironment environment;

	// Détermine si les valeurs lues par l'interpréteur sont réaffichées sur la
	// console
	private boolean echo;
	// Détermine si l'invite de commande doit apparaître
	private boolean prompt;

	// File contenant les arguments pour le prochain envoi de message.
	// Les fonctions et closures peuvent avoir l'effet de bord de mettre des
	// atomes dans cette file.
	// C'est le mécanisme de retour et de passage de paramètres.
	private Queue<JarvisAtom> argQueue;

	/*
	 * Pile contenant des commandes à évaluer. Utilisée lorsqu'une closure est
	 * évaluéeou qu'un fichier est chargé avec la commande CMD_LOAD.Les lignes
	 * de texte qui contiennent plus qu'une valeur placent les valeurs suivantes
	 * sur cette pile.Cette pile est doit être vide avant de lire des commandes
	 * dans un fichier ou sur la ligne de commande.
	 */
	private Stack<String> evalStack;

	// Indique qu'une errerur est survenue plus tôt (outputError).
	private boolean hasCrashed;

	// Indique que la commande CMD_QUIT a été interprétée.
	private boolean hasQuit;

	// Le flot d'entrée courant (fichier ou clavier)
	private BufferedReader reader;

	/*
	 * Constructeur. Au démarrage, un flot d'entrée doit être spécifié. lorsque
	 * c'est un fichier, l'echo et le prompt devraient être mis à false.
	 */
	public JarvisInterpreter(BufferedReader r, boolean e, boolean p) {

		echo = e;
		prompt = p;
		reader = r;
		init();

	}

	/*
	 * Initialisation de l'interpréteur.
	 */

	private void init() {

		hasCrashed = false;
		hasQuit = false;

		// Création de l'environnement racine. Il contient uniquement les
		// fonctions tricheuses.
		environment = new JarvisEnvironment(this);

		// Ajout des fonctions tricheuses (primitives codées en Java).
		addCheaterCode();

		// Création de l'environnement d'interprétation de départ. Son parent
		// est l'environnement de base.
		environment = new JarvisEnvironment(this, environment);

		// Initialisation de la file d'arguments
		argQueue = new LinkedList<JarvisAtom>();

		// Initialisation de la pile d'évaluation
		evalStack = new Stack<String>();

		// Création de la classe de base.
		createClassClass();
	}

	
	//FONCTIONSTRICHEUSES
	//MUTATEUR
	/*
	 * Cette fonction ajoute les primitives du langage dans l'environnement. Les
	 * primitives sont des fonctions qui s'exécutent en Java. 
	 * Elles sont cachées dans un environnement parent à celui dans lequel
	 * l'interpréteur démarre.
	 */
	private void addCheaterCode() {

		environment.put("_integerAddFunction", new IntegerAddFunction());
		environment.put("_integerSubtractFunction",
				new IntegerSubtractFunction());
		environment.put("_integerMultiplyFunction",
				new IntegerMultiplyFunction());
		environment.put("_integerEqualsFunction", new IntegerEqualsFunction());
		environment.put("_operatorNewFunction", new OperatorNewFunction());
	}

	//HÉRITAGE
	//VARIABLESCLASSE
	/*
	 * Cette fonction crée la classe des classes. Celle-ci spécifie qu'une
	 * classe a deux champs: attributes et methods. Pour créer une classe
	 * valide, attributes doit être une liste de JarvisString et methods doit
	 * être un dictionnaire de paires JarvisString:JarvisAtom.
	 * 
	 * Cette classe ne contient qu'une méthode: la fonction tricheuse
	 * OperatorNewFunction.
	 * 
	 * Cette classe ne devrait pas être codée en Jarvis. Un changement dans
	 * l'organisation des membres de cette classe implique aussi potentiellement
	 * un changement de l'algorithme d'interprétation de messages se trouvant
	 * dans JarvisObject (JarvisObjet.message( ... )). De plus, cette classe
	 * doit être sa propre classe. Le symbole Class ne peut pas être résolu par
	 * l'interpréteur avant d'exister! La référence à la classe doit également
	 * demeurer un lien direct vers l'objet-classe en question. Si c'était
	 * uniquement un symbole à interpréter, on pourrait définir avec le même nom
	 * qu'une classe et ainsi empêcher les objets de retrouver leur classe.
	 */
	
	private void createClassClass() {

		/*
		 * Création de la liste de membres. Un objet instancié par cette classe
		 * comprend deux membres, la liste des attributs, ainsi que le
		 * dictionnaire des méthodes. ATTENTION! Rien ne garantit qu'une
		 * instance sera créée avec une liste et un dictionnaire comme
		 * arguments! Lisez bien les définitions des classes de base
		 * (basictypes.txt) pour avoir des exemples. Si vous modifiez
		 * l'organisation de Class, il faut aussi modifier comment instancier
		 * des classes. Il est possible que vous deviez changer toutes les
		 * définitions de classes de basictypes.txt.
		 */
		JarvisList members = new JarvisList();
		members.add(new JarvisString("attributes"));
		members.add(new JarvisString("methods"));

		HashMap<String, JarvisAtom> m = new HashMap<String, JarvisAtom>();
		JarvisDictionnary methods = new JarvisDictionnary(m);

		/*
		 * Cette classe ne contient qu'une méthode, new. Celle-ci fait usage de
		 * la fonction tricheuse OperatorNewFunction. Comme un objet résultant
		 * de !(Class new) est instance de Class, il supportera nécessairement
		 * new aussi (C'est donc une classe). Class supporte new parce qu'elle
		 * est instance d'elle-même.
		 */
		methods.put("new", new OperatorNewFunction());

		/*
		 * Création d'un objet qui sera instance de Class Ses données seront la
		 * liste des attributs dictionnaire de méthodes créés plus haut.
		 */
		ArrayList<JarvisAtom> data = new ArrayList<JarvisAtom>();

		data.add(members);
		data.add(methods);

		JarvisObject ClassClass = new JarvisObject(null, data,this);

		/*
		 * Cet objet contient la définition d'une classe et est instance de
		 * lui-même.
		 */
		ClassClass.setClass(ClassClass);

		/*
		 * Ajout de la classe de base dans l'environnement. Elle s'appelle
		 * Class, bien sûr. L'appeler Hippopotamme n'aurait pas été très
		 * pratique.
		 */
		environment.put("Class", ClassClass);

	}

	/*
	 * Cette fonction implante une partie de la boucle d'interprétation. Elle
	 * lit une commande à partir du flot d'entrée et l'interprète. Les
	 * exceptions lancées un peu partout dans l'interpréteur sont attrappées
	 * ici. Lorsqu'une exception survient, l'interpréteur tente de redémarrer
	 * afin que l'utilisateur puisse avoir un peu de contrôle après l'erreur.
	 * Pour déterminer où se trouve une erreur dans du code Jarvis, il faut
	 * ajouter des points d'arrêt dans le fichier de code avec la commande
	 * CMD_DEBUG. L'état de l'environnement n'est pas nécessairement correct
	 * après qu'une erreur soit survenue.
	 */

	public void run() {		
		

		JarvisCommand cmd;
		prompt();
		try {
			cmd = readCommandFromInput();
			cmd.interpret(this);
		} catch (UndefinedSymbolException e) {
			outputErrorNoCrash(e.getMessage());
			reset();
		} catch (IllegalArgumentException e) {
			outputErrorNoCrash(e.getMessage());
			reset();
		} catch (NoSuchElementException e) {
			outputErrorNoCrash(e.getMessage());
			reset();
		}

		if (getArgCount() > 0) {
			output(peekArg().makeKey());
		}

		while (!hasQuit()) {

			prompt();

			try {
				cmd = readCommandFromInput();
				cmd.interpret(this);
			} catch (UndefinedSymbolException e) {
				outputErrorNoCrash(e.getMessage());
				reset();
			} catch (IllegalArgumentException e) {
				outputErrorNoCrash(e.getMessage());
				reset();
			} catch (NoSuchElementException e) {
				outputErrorNoCrash(e.getMessage());
				reset();
			}

			if (hasCrashed) {

				outputError("Stopping due to earlier error");
				hasQuit = true;
			}

		}

	}

	/*
	 * Tentative de redémarrage. Vide la liste d'arguments et la pile
	 * d'évaluation. Vous pouvez faire quelque-chose de plus intelligent ici.
	 */
	private void reset() {

		argQueue = new LinkedList<JarvisAtom>();
		evalStack = new Stack<String>();
	}

	/*
	 * Cas spécial qui survient lorsque l'interpréteur
	 * dépile CMD_ENDOFENVIRONMENT.
	 * Cette commande se trouve empilée lorsqu'on entre
	 * dans une closure et qu'on crée son environnement.
	 * 
	 */
	public void endOfEnvironment() {
		if (environment.hasParent()) {

			environment = environment.getParent();
		} else {
			outputErrorNoCrash("Cannot close global environment!");
		}
	}

	//SOUSLECAPOT
	/*
	 * Interprétation de message.
	 * Plusieurs trucs pas parfaitement propres ici
	 * Ceci est la partie de l'interpréteur qui sert à
	 * fabriquer des messages et les envoyer aux objets.
	 * 
	 */
	
	public void message() {

		// Les atomes liés à des symboles ne doivent pas être interprétés
		// récursivement ici
		JarvisCommand.setDontInterpret(true);

		JarvisCommand cmd;

		JarvisObject obj;

		// Les symboles indéfinis sont détectés localement au cas où un
		// traitement spécial
		// devrait être fait dans le futur
		JarvisCommand.setAcceptUndefined(true);

		// Lecture de la première valeur: le destinataire du message.
		// Celui-ci peut se trouver dans la liste d'arguments.
		// Pour l'en récupérer, il faut utiliser la commande CMD_ARG
		cmd = readCommandFromInput();
		JarvisAtom res = cmd.interpretNoPut(this);
		try {
			obj = (JarvisObject) res;
		} catch (ClassCastException e) {

			throw new IllegalArgumentException(cmd.makeKey()
					+ " is not an object");
		}

		if (res.isUndefined()) {
			throw new UndefinedSymbolException(cmd.makeKey()
					+ ": symbole indéfini");
		}

		// Lecture de la seconde valeur: le sélecteur.
		// Celui-ci peut se trouver dans la liste d'arguments.
		// Pour l'en récupérer, il faut utiliser la commande CMD_ARG
		cmd = readCommandFromInput();
		JarvisAtom selector = cmd.interpretNoPut(this);
		if (selector.isUndefined()) {
			selector = new JarvisString(selector.makeKey());
		}

		JarvisCommand.setAcceptUndefined(false);

		// L'objet qui recoit le message possède son propre environnement.
		// Dans cet environnement, on peut envoyer des messages à self,
		// qui est un symbole référençant l'objet courant.
		JarvisEnvironment objectEnvironment = new JarvisEnvironment(this,
				getEnvironment());

		// Le symbole auquel est lié l'objet courant est self.
		objectEnvironment.put("self", obj);
		setEnvironment(objectEnvironment);

		// Lecture des arguments directement
		// Vous pouvez inclure des valeurs atomiques ou des symboles définis
		// comme arguments. Les arguments restants seront récupérés dans la
		// file d'arguments. Attention à l'ordre! Ceux dans la parenthèse
		// passent en priorité.
		cmd = readCommandFromInput();
		while (!cmd.isEndOfList()) {

			cmd.interpret(this);
			cmd = readCommandFromInput();
		}
		JarvisCommand.setDontInterpret(false);

		// Interprète le message, obtient le membre correspondant.

		res = obj.message(selector);

		// Interprétation du résultat. Si c'est une méthode, elle sera
		// exécutée dans l'environnement de self, avec les arguments
		// présentement dans la file.
		res.interpret(this);

		// Message interprété. On sort de l'environnement de l'objet.
		setEnvironment(environment.getParent());

		// Vérification du retour.
		// Décommentez ces lignes si vous voulez un affichage de la valeur en
		// tête
		// de file après chaque message.
		// Il est mieux d'insérer une commande CMD_ARGS directement dans votre
		// code
		// Jarvis pour voir la file au complet.

		/*
		 * if (argQueue.size() > 0) { print("Retour:");
		 * peekArg().interpretNoPut(this); }
		 */

	}

	// Appelé lorsque la commande CMD_QUIT est interprétée.
	public void quit() {
		hasQuit = true;
	}

	// Décompte du nombre d'arguments dans la file d'arguments.
	public int getArgCount() {
		return argQueue.size();
	}

	//Défile l'argument en tête de file. 
	public JarvisAtom getArg() {
		return argQueue.remove();
	}
	
	//Enfile un argument. Utilisé lorsqu'un atome est interprété normalement (JarvisAtom.interpret)
	public void putArg(JarvisAtom obj) {
		argQueue.add(obj);
	}

	//Empile des commandes à évaluer. 	
	public void pushEval(String expression) {
		evalStack.push(expression);
	}

	//Envoie un message d'erreur à la console sans arrêter l'interpréteur.
	private void outputErrorNoCrash(Object value) {
		System.err.println(value);
		println("");

	}
	
	//Permet de voir l'argument en tête de file.
	public JarvisAtom peekArg() {

		return argQueue.peek();
	}

	//Utilisée pour obtenir la prochaine commande à interpréter.
	public JarvisCommand readCommandFromInput() {
		JarvisCommand cmd = new JarvisCommand(nextInput());
		return cmd;
	}

	//Affichage de la file d'arguments. Appelé par la commande CMD_ARGS.
	public void printArgs() {
		print("Args: (");
		for (JarvisAtom arg : argQueue) {
			print(arg + ",");
		}
		println(")");
	}

	//Lorsqu'un nouveau symbole est créé avec la commande CMD_REF, il faut lier
	//celui-ci à une valeur.
	public JarvisAtom getValue() {

		// Obtient d'abord ses valeurs de la file d'arguments
		if (argQueue.size() > 0) {

			return getArg();
		}
		
		//Si celle-ci est vide, assume que la valeur sera la prochaine commande.
		return readAtomFromInput();
	}

	//Affichage de l'environnement courant. Appelé par la commande CMD_ENV
	public void printEnvironment() {

		environment.print();
	}

	//Raccourci d'affichage.
	public static void print(Object obj) {

		System.out.print(obj);

	}
	//Raccourci d'affichage.
	public static void println(Object obj) {

		System.out.println(obj);
	}

	/*
	 * Horrible fonction qui sert à lire plusieurs commandes sur la même ligne.
	 * Le seul semblant de décortication de syntaxe se trouve ici, ainsi qu'éparpillé
	 * dans les diverses fonctions de JarvisCommand.
	 * Ne regardez pas ça de trop près... ça fait mal aux yeux.
	 */
	public String putTokensOnStack(String line) {
		final String delims = " ()[]{}";
		Stack<String> tokens = new Stack<String>();

		StringTokenizer tokenizer = new StringTokenizer(line, delims, true);

		while (tokenizer.hasMoreTokens()) {
			tokens.push(tokenizer.nextToken());
		}

		String previousToken = "";
		String currentToken = "";
		boolean glue = false;
		while (!tokens.isEmpty()) {
			currentToken = tokens.pop();
			currentToken = currentToken.trim();
			if (currentToken.compareTo("(") == 0
					|| currentToken.compareTo("{") == 0) {
				previousToken = currentToken;
				glue = true;
				continue;
			} else if (currentToken.compareTo("") == 0) {
				continue;
			}

			if (glue) {
				glue = false;
				if (currentToken.compareTo("") == 0) {
					evalStack.push(previousToken);
				} else {
					currentToken += previousToken;
					evalStack.push(currentToken);
				}
				previousToken = "";
			} else {
				evalStack.push(currentToken);
			}

		}
		if (glue) {
			evalStack.push(previousToken);
		}

		if (!evalStack.isEmpty()) {
			return evalStack.pop();
		} else {
			return "";
		}

	}

	
	/*
	 * Fonction appelée pour obtenir la prochaine commande sous forme 
	 * de string. 
	 * Regarde d'abord dans la pile d'évaluation.
	 */
	public String nextInput() {

		String s = "";
		if (evalStack.size() > 0) {
			s = evalStack.pop();
			if (s.length() == 0) {
				return nextInput();
			} else if (s.startsWith("#")) {
				return nextInput();
			}
			return s;

		} else {
			return readLine();
		}

	}

	/*
	 * Patch pour lire correctement des fichiers avec la commande
	 * CMD_LOAD. Devrait être utilisée par readLine, mais 
	 * la combinaison est encore buggy.
	 */
	public String readSignificantLine(BufferedReader r) {
		String line = "";
		while (line.length() == 0 || line.startsWith("#")) {
			try {
				line = r.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				return null;
			}
			line = line.trim();
		}

		return line;
	}

	/*
	 * Épouvantable fonction servant à lire la prochaine
	 * commande sous forme de string, lorsque la pile d'évaluation
	 * est vide.
	 * Un peu trop de singeries pour donner le bon comportement
	 * à l'interpréteur.
	 * Ne regardez pas de trop près... ça fait saigner les yeux.
	 * 
	 */
	public String readLine() {
		String s = "";

		try {
			s = reader.readLine();

			if (s == null) {
				println("End of file. Going to console...");
				prompt = true;
				prompt();
				reader = new BufferedReader(new InputStreamReader(System.in));
				echo = false;
				return nextInput();
			} else {
				s = s.trim();

				if (s.length() == 0) {
					return nextInput();
				} else if (s.startsWith("#")) {
					return nextInput();
				} else {
					s = putTokensOnStack(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (echo) {
			println(s);
		}
		return s;
	}

	
	public void output(Object value) {
		println(value);

	}

	public void outputDebug(Object value) {
		print("#debug# | ");
		output(value);

	}

	public JarvisEnvironment getEnvironment() {
		return environment;
	}

	public void outputError(Object value) {

		hasCrashed = true;
		outputErrorNoCrash(value);
	}

	public void setEnvironment(JarvisEnvironment newEnvironment) {

		environment = newEnvironment;
	}

	/*
	 * Fonction utile pour débugger. 
	 * Affiche le contenu de la pile d'évaluation. 
	 * Attention, un peu cryptique à lire.
	 * 
	 */
	
	public void printEvalStack() {

		println("----------Eval stack------------");

		for (String s : evalStack) {

			println(s);

		}
		println("----------Eval stack------------");

	}

	public boolean hasQuit() {

		return hasQuit;
	}

	public void setReader(BufferedReader r) {
		reader = r;

	}

	public void setEcho(boolean e) {
		echo = e;

	}

	public void clearArgs() {
		argQueue.clear();

	}

	public JarvisAtom readAtomFromInput() {
		JarvisCommand cmd = readCommandFromInput();
		JarvisAtom atom = cmd.interpretNoPut(this);
		return atom;
	}

	public void prompt() {
		if (prompt && evalStack.isEmpty()) {
			print(">");
		}
	}

	public void setPrompt(boolean p) {
		prompt = p;
	}

}