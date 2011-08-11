/**
 * Game Scene Compiler
 * -------------------
 */
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AST {
	public interface Node {
		<T> T accept (Visitor<T> v);
	}
	public interface Visitor<T> {
		T visit (Loop loop);
		T visit (Branch branch);
		T visit (Block block);
		T visit (Assign assign);
		T visit (Id id);
		T visit (Operator op);
		T visit (Plus op);
		T visit (Minus op);
		T visit (Times op);
		T visit (Divide op);
		T visit (Number num);
		T visit (Obj obj);
		T visit (Variables var);
		T visit (Camera cam);
		T visit (Sprite spri);
		T visit (Move mov);
	}
	public interface Statement extends Node {}
	public interface Expression extends Node {}
	public static class Id implements Expression {
		String id;
		public Id (String id) { this.id = id; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static Id id (String id) { return new Id (id); }
	public static class Assign implements Statement {
		Id variable; Expression value;
		public Assign (Id variable, Expression value) { this.variable = variable; this.value = value; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static Assign assign (Id var, Expression val) { return new Assign (var, val); }
	public static class Block implements Statement {
		Statement[] statements;
		public Block (Statement ... statements) { this.statements = statements; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static Block block (Statement... statements) { return new Block (statements); }
	public static class Branch implements Statement {
		Expression predicate; Statement ifBranch; Statement elseBranch;
		public Branch (Expression p, Statement a, Statement b) { predicate = p; ifBranch = a; elseBranch = b; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static Branch branch (Expression predicate, Statement ifBranch, Statement elseBranch) { return new Branch (predicate, ifBranch, elseBranch); }
	public static class Loop implements Statement {
		Expression predicate; Statement body;
		public Loop (Expression p, Statement body) { predicate = p; this.body = body; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }
	}
	public static Loop loop (Expression predicate, Statement body) { return new Loop (predicate, body); }
	
	public static class Number implements Expression {
		int n;
		public Number (int n) { this.n = n; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static Number number (int n) { return new Number (n); }
	
	public static class Operator implements Expression {
		Expression left; Expression right; 
		private Operator (Expression left, Expression right) {this.left = left; this.right = right; }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }		
	}
	public static class Plus extends Operator {
		public Plus(Expression left, Expression right) { super(left, right); }
		public <T> T accept(Visitor<T> v) { return v.visit(this); }
	}
	public static Plus plus (Expression left, Expression right) { return new Plus (left, right); }
	
	public static class Minus extends Operator {
		public Minus (Expression left, Expression right) { super(left, right); }
		public <T> T accept (Visitor<T> v) { return v.visit(this); }
	}
	public static Minus minus (Expression left, Expression right) { return new Minus (left, right); }
	
	public static class Times extends Operator {
		public Times (Expression left, Expression right) { super(left, right); }
		public <T> T accept (Visitor<T> v) { return v.visit(this); }
	}
	public static Times times (Expression left, Expression right) { return new Times (left, right); }
	
	public static class Divide extends Operator {
		public Divide (Expression left, Expression right) { super(left, right); }
		public <T> T accept (Visitor<T> v) { return v.visit(this); }
	}
	public static Divide divide (Expression left, Expression right) { return new Divide (left, right); }
	
	public static class Obj implements Statement 
	{
		Id id;
		Variables v;
		public Obj (Id id, Variables v) 
		{
			this.id = id; this.v = v;
		}
		public <T> T accept (Visitor<T> v) 
		{ 
			return v.visit(this); 
		}
	}
	public static Obj obj (Id id, Variables v) {return new Obj (id, v); }
	
	public static class Variables implements Statement
	{
		Number x;
		Number y;
		Number w;
		Number h;
		Id f;
		public Variables (Number x, Number y)
		{
			this.x = x;
			this.y = y;
		}
		public Variables (Number x, Number y, Number w, Number h, Id f)
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.f = f;
		}
		public <T> T accept (Visitor<T> v) 
		{ 
			return v.visit(this); 
		}
	}
	public static Variables variables (Number x, Number y) {return new Variables (x, y); }
	public static Variables variables (Number x, Number y, Number w, Number h, Id f) {return new Variables (x, y, w, h, f); }
	
	public static class Camera extends Obj
	{
		public Camera (Id id, Variables v)
		{
			super(id, v);
		}
		public <T> T accept (Visitor<T> v) 
		{ 
			return v.visit(this); 
		}
	}
	public static Camera camera (Id id, Variables v) { return new Camera (id, v); }
	
	public static class Sprite extends Obj
	{
		public Sprite (Id id, Variables v)
		{
			super(id, v);
		}
		public <T> T accept (Visitor<T> v) 
		{ 
			return v.visit(this); 
		}
	}
	public static Sprite sprite (Id id, Variables v) { return new Sprite (id, v); }
	
	public static class Move implements Statement
	{
		Id id;
		Expression x;
		Expression y;
		public Move (Id id, Expression x, Expression y)
		{
			this.id = id;
			this.x = x;
			this.y = y;
		}
		public <T> T accept (Visitor<T> v) 
		{ 
			return v.visit(this); 
		}
	}
	public static Move move (Id id, Expression x, Expression y) { return new Move (id, x, y); }
	
	public static class ExpressionInterpreter implements Visitor<Integer> {
		Map<String, Integer> symbols;
		public ExpressionInterpreter(Map<String, Integer> symbols) {
			this.symbols = symbols;
		}
		public Integer visit(Id id) {
			if (symbols.containsKey(id.id))
				return symbols.get(id.id);
			else return 0;
		}
		public Integer visit(Operator op) {
			return null;
		}
		public Integer visit(Plus op) {
			return op.left.accept(this) + op.right.accept(this);
		}
		public Integer visit(Minus op) {
			return op.left.accept(this) - op.right.accept(this);
		}
		public Integer visit(Times op) {
			return op.left.accept(this) * op.right.accept(this);
		}
		public Integer visit(Divide op) {
			return op.left.accept(this) / op.right.accept(this);
		}
		public Integer visit(Number num) {
			return num.n;
		}
		public Integer visit(Loop loop) { return null; }
		public Integer visit(Branch branch) { return null; }
		public Integer visit(Block block) { return null; }
		public Integer visit(Assign assign) { return null; }
		public Integer visit(Obj obj) { return null; }
		public Integer visit(Variables v) { return null; }
		public Integer visit(Sprite spri) { return null; }
		public Integer visit(Move mov) { return null; }
		public Integer visit(Camera cam) { return null; };
	}
	public static class StatementInterpreter implements Visitor<Void> {
		Map<String,Integer> symbols = new HashMap<String, Integer>();
		ExpressionInterpreter eval = new ExpressionInterpreter(symbols);
		public Void visit(Loop loop) {
			while (loop.predicate.accept(eval) != 0)
				loop.body.accept(this);
			return null;
		}
		public Void visit(Branch branch) {
			if (branch.predicate.accept(eval) != 0)
				branch.ifBranch.accept(this);
			else
				branch.elseBranch.accept(this);
			return null;
		}
		public Void visit(Block block) {
			for (Statement s : block.statements)
				s.accept(this);
			return null;
		}
		public Void visit(Assign assign) {
			symbols.put(assign.variable.id, assign.value.accept(eval));
			return null;
		}
		
		public Void visit(Obj obj) {
			obj.id.accept(this);
			obj.v.accept(this);
			return null;
		}
		public Void visit(Variables var) { 
			var.x.accept(this);
			var.y.accept(this);
			try{
				var.w.accept(this);
				var.h.accept(this);
				var.f.accept(this);
			}catch(Exception e) {
				
			}
			return null; 
		}
		public Void visit(Sprite spri) { 
			spri.id.accept(this);
			spri.v.accept(this);
			//System.out.println("Texture2D " + spri.id.id +" = content.Load<Texture2D>(" +"\""+spri.v.f.id+"\")");
			//System.out.println("spriteBatch.Draw(" +spri.id.id+ ", " + spri.v.x.n + ", " + spri.v.y.n +", null, Color.Transparent, 0.0f, Vector2.Zero," + spri.v.w.n/spri.v.h.n + ", SpriteEffects.None, 1.0f)");

			return null;
		}
		public Void visit(Move mov) { 
			mov.id.accept(this);
			mov.x.accept(this);
			mov.y.accept(this);
			return null; 
		}
		public Void visit(Camera cam) { 
			cam.id.accept(this);
			cam.v.accept(this);
			//System.out.println("Camera: " + cam.id.id + " Variables: " + cam.v.x.n);
			return null; 
		}
		
		public Void visit(Id id) { return null; }
		public Void visit(Operator op) { return null; }
		public Void visit(Plus op) { return null; }
		public Void visit(Minus op) { return null; }
		public Void visit(Times op) { return null; }
		public Void visit(Divide op) { return null; }
		public Void visit(Number num) { return null; }
	}
	
	public class Sequence implements Node
	{
		ArrayList<Node> elements = new ArrayList<Node>();
		public <T> T accept (Visitor<T> v)
		{
			for(int i = 0; i < elements.size(); i++)
			{
				elements.get(i).accept(v);
			}
			return null;
		}
	}
	
	public static class CompilerVisitor<T> implements Visitor
	{
		public Object visit(Loop loop) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Branch branch) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Block block) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Assign assign) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Id id) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Operator op) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Plus op) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Minus op) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Times op) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Divide op) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(Number num) {
			System.out.print(num.n);
			return null;
		}

		public Object visit(Obj obj) {
			System.out.println(obj.id.id + ", " + obj.v.x.n + ", " + obj.v.y.n + ", " + obj.v.h.n + ", " + obj.v.w.n);
			return null;
		}

		public Object visit(Variables var) {
			System.out.println(var.x.n + ", " + var.y.n + ", " + var.h.n + ", " + var.w.n + ", " + var.f.id);
			return null;
		}

		public Object visit(Camera cam) {
			System.out.println("Camera: " + cam.id.id + " Variables: " + cam.v.x.n);
			return null;
		}

		public Object visit(Sprite spri) {
			System.out.println("Texture2D " + spri.id.id +" = content.Load<Texture2D>(" +"\""+spri.v.f.id+"\")");
			System.out.println("spriteBatch.Draw(" +spri.id.id+ ", " + spri.v.x.n + ", " + spri.v.y.n +", null, Color.Transparent, 0.0f, Vector2.Zero," + spri.v.w.n/spri.v.h.n + ", SpriteEffects.None, 1.0f)");
			return null;
		}

		public Object visit(Move mov) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static Sequence Tokenizer()
	{
		AST ass = new AST();
		Sequence seq = ass.new Sequence();
		try {
			
			//ArrayList<Node> Nodes = new ArrayList<Node>();
			FileReader inFile = new FileReader("Test.scn");
			StreamTokenizer st = new StreamTokenizer(inFile);
			
			st.ordinaryChar('.');
			st.ordinaryChar('/');
			st.eolIsSignificant(true);
			
			String ID;
			int x;
			int y;
			int w;
			int h;
			String fileName;
			
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF) 
			{
				char ch;
				String s;
								
				switch(token)
				{
				
				case StreamTokenizer.TT_WORD:
					
					s = st.sval;
					if(s.equals("Move")) {
						st.nextToken();
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						seq.elements.add(new Move(new Id(ID), new Number(x), new Number(y)));
						System.out.println("Move " + ID + " " + x + ", " + y);
					}
					/*else if(s.equals("Object") || s.equals("Camera") || s.equals("Sprite")) {
						System.out.print("<Type> " + s + " ");
						
					}*/
					else if(s.equals("Object")) {
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						st.nextToken();
						w = (int)st.nval;
						st.nextToken();
						st.nextToken();
						h = (int)st.nval;
						st.nextToken();
						st.nextToken();
						fileName = st.sval;
						st.nextToken();
						seq.elements.add(new Obj(new Id(ID), new Variables(new Number(x), new Number(y), new Number(w), new Number(h), new Id(fileName))));
						System.out.println("Object " + ID + " " + x + ", " + y + ", " + w + ", " + h + ", " + fileName);
					}
					else if(s.equals("Camera"))
					{
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						seq.elements.add(new Camera(new Id(ID), new Variables(new Number(x), new Number(y))));
						System.out.println("Camera " + ID + " " + x + ", " + y);
					}
					else if(s.equals("Sprite"))
					{
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						st.nextToken();
						w = (int)st.nval;
						st.nextToken();
						st.nextToken();
						h = (int)st.nval;
						st.nextToken();
						st.nextToken();
						fileName = st.sval;
						st.nextToken();
						seq.elements.add(new Sprite(new Id(ID), new Variables(new Number(x), new Number(y), new Number(w), new Number(h), new Id(fileName))));
						System.out.println("Sprite " + ID + " " + x + ", " + y + ", " + w + ", " + h + ", " + fileName);
					}
					else {
						System.out.print("<ID> " + s + " ");
					}
					break;
					
					
				case StreamTokenizer.TT_NUMBER:
					int n = (int)st.nval;
					System.out.print("<Number> " + n);
					seq.elements.add(new Number(n));
					break;
					
				case '(':
					ch = (char)st.ttype;
					System.out.print("<Variables>" + ch);
					break;
					
				case ')':
					ch = (char)st.ttype;
					System.out.print(ch);
					break;
					
				case ',':
					ch = (char)st.ttype;
					System.out.print(ch + " ");
					break;
					
				case '"':
					s = st.sval;
					System.out.print("<ID> " + "\"" + s + "\"");
					break;
					
				case StreamTokenizer.TT_EOL:
					System.out.println();
					
				case '\0':
					break;
					
				default:
					s = st.sval;
					System.out.println("ERROR: Unrecognized Token: " + s);
					break;
					
				}
				token = st.nextToken();
			}
			
			inFile.close();
			System.out.println();
			
						
		} catch(IOException e) {
			System.out.println("Error: " + e);
		}
		return seq;
		
	}
	
	public static void main (String[] args) {

		/*try {
			
			//ArrayList<Node> Nodes = new ArrayList<Node>();
			AST ass = new AST();
			Sequence seq = ass.new Sequence();
			FileReader inFile = new FileReader("Test.scn");
			StreamTokenizer st = new StreamTokenizer(inFile);
			
			st.ordinaryChar('.');
			st.ordinaryChar('/');
			st.eolIsSignificant(true);
			
			String ID;
			int x;
			int y;
			int w;
			int h;
			String fileName;
			
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF) 
			{
				char ch;
				String s;
								
				switch(token)
				{
				
				case StreamTokenizer.TT_WORD:
					
					s = st.sval;
					if(s.equals("Move")) {
						st.nextToken();
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						seq.elements.add(new Move(new Id(ID), new Number(x), new Number(y)));
						System.out.println("Move " + ID + " " + x + ", " + y);
					}
					/*else if(s.equals("Object") || s.equals("Camera") || s.equals("Sprite")) {
						System.out.print("<Type> " + s + " ");
						
					}*/
					/*else if(s.equals("Object")) {
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						st.nextToken();
						w = (int)st.nval;
						st.nextToken();
						st.nextToken();
						h = (int)st.nval;
						st.nextToken();
						st.nextToken();
						fileName = st.sval;
						st.nextToken();
						seq.elements.add(new Obj(new Id(ID), new Variables(new Number(x), new Number(y), new Number(w), new Number(h), new Id(fileName))));
						System.out.println("Object " + ID + " " + x + ", " + y + ", " + w + ", " + h + ", " + fileName);
					}
					else if(s.equals("Camera"))
					{
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						seq.elements.add(new Camera(new Id(ID), new Variables(new Number(x), new Number(y))));
						System.out.println("Camera " + ID + " " + x + ", " + y);
					}
					else if(s.equals("Sprite"))
					{
						st.nextToken();
						ID = st.sval;
						st.nextToken();
						st.nextToken();
						x = (int)st.nval;
						st.nextToken();
						st.nextToken();
						y = (int)st.nval;
						st.nextToken();
						st.nextToken();
						w = (int)st.nval;
						st.nextToken();
						st.nextToken();
						h = (int)st.nval;
						st.nextToken();
						st.nextToken();
						fileName = st.sval;
						st.nextToken();
						seq.elements.add(new Sprite(new Id(ID), new Variables(new Number(x), new Number(y), new Number(w), new Number(h), new Id(fileName))));
						System.out.println("Sprite " + ID + " " + x + ", " + y + ", " + w + ", " + h + ", " + fileName);
					}
					else {
						System.out.print("<ID> " + s + " ");
					}
					break;
					
					
				case StreamTokenizer.TT_NUMBER:
					int n = (int)st.nval;
					System.out.print("<Number> " + n);
					seq.elements.add(new Number(n));
					break;
					
				case '(':
					ch = (char)st.ttype;
					System.out.print("<Variables>" + ch);
					break;
					
				case ')':
					ch = (char)st.ttype;
					System.out.print(ch);
					break;
					
				case ',':
					ch = (char)st.ttype;
					System.out.print(ch + " ");
					break;
					
				case '"':
					s = st.sval;
					System.out.print("<ID> " + "\"" + s + "\"");
					break;
					
				case StreamTokenizer.TT_EOL:
					System.out.println();
					
				case '\0':
					break;
					
				default:
					s = st.sval;
					System.out.println("ERROR: Unrecognized Token: " + s);
					break;
					
				}
				token = st.nextToken();
			}
			inFile.close();
			System.out.println();
						
		} catch(IOException e) {
			System.out.println("Error: " + e);
		}*/

		Node trial = Tokenizer();
		trial.accept(new AST.CompilerVisitor());
		
		Node Object = sprite(id("TEST"), variables(number(1), number(4), number(1), number(4), id("file.png")));
		
		StatementInterpreter runner = new StatementInterpreter();
		Object.accept(runner);
	}
}