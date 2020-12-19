import java.util.Comparator;

public class SortByHn implements Comparator<Board> {

	/**
	 * Used to sort in ascending order of h(n).
	 */
	@Override
	public int compare(Board b1, Board b2) {
		return b1.get_h() - b2.get_h();
	}
}