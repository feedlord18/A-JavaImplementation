import java.util.Comparator;

public class SortByFn implements Comparator<Board> {

	/**
	 * Used to sort in ascending order of f(n).
	 */
	@Override
	public int compare(Board b1, Board b2) {
		return b1.get_f() - b2.get_f();
	}
}