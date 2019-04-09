public class StringLexer implements Lexer {
	String chaine;
	int position;

	public StringLexer(String chaine) {
	}

	@Override
	public char current() {
		// TODO Auto-generated method stub
		if (position != chaine.length()) {
			return chaine.charAt(position);
		} else
			return chaine.charAt(position);
	}

	@Override
	public char get() throws EOFException {
		// TODO Auto-generated method stub

		if (position != chaine.length()) {
			position = position + 1;
			return (chaine.charAt(position));
		} else {
			throw new EOFException();
		}

	}

	@Override
	public void skipWhiteSpace() {
		// TODO Auto-generated method stub
		while (Character.isWhitespace(this.current())) {
			position = position + 1;
		}

	}

	@Override
	public void next() throws EOFException {
		// TODO Auto-generated method stub
		if (position != chaine.length()) {
			position = position + 1;
			skipWhiteSpace();
		}else {
			throw new EOFException();
		}
	}
}
