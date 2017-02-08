import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

public class DateTest {

  @Test public void shouldPass() {
    Assert.assertEquals(1, 1);
  }

  @Test public void testingDates() {
    Calendar cal = new GregorianCalendar(Locale.UK);

    System.out.println(cal.getFirstDayOfWeek());
  }
}
