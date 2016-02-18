package org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Created by vagrant on 2/15/16.
 */
public class BetweenMatchers {

    static public <T extends Number> Matcher<T> between(T min, T max) {
        return new BaseMatcher<T>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("between ").appendValue(min).appendText(" and ").appendValue(max);
            }

            @Override
            public boolean matches(Object o) {
                Number v = (Number) o;
                return min.doubleValue() <= v.doubleValue() && v.doubleValue()<= max.doubleValue() ;
            }
        };
    }


    public static Matcher<OffsetDateTime> between(OffsetDateTime min, OffsetDateTime max) {
        return new BaseMatcher<OffsetDateTime>() {
            @Override
            public boolean matches(Object o) {
                OffsetDateTime v = (OffsetDateTime) o;
                return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("between ").appendValue(min).appendText(" and ").appendValue(max);
            }
        };
    }

    public static Matcher<LocalDateTime> between(LocalDateTime min, LocalDateTime max) {
        return new BaseMatcher<LocalDateTime>() {
            @Override
            public boolean matches(Object o) {
                LocalDateTime v = (LocalDateTime) o;
                return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("between ").appendValue(min).appendText(" and ").appendValue(max);
            }
        };
    }
}
