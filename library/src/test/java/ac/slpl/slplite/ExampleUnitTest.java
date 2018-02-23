package ac.slpl.slplite;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will onSucceededReadingFromDB on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private Integer count =0;
    private List<Integer> list = new ArrayList<>();
    @Test
    public void addition_isCorrect() throws Exception {
        list.add(count);
        count++;
        list.add(count);
    }
}