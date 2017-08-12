/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eru.util;

import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * User: hansolo
 * Date: 16.12.13
 * Time: 15:03
 */
public class LocalDateTimeAxis extends Axis<LocalDateTime> {
    private enum Interval {
        DECADE(ChronoUnit.DECADES, 1),
        YEAR(ChronoUnit.YEARS, 1),
        MONTH_6(ChronoUnit.MONTHS, 6),
        MONTH_3(ChronoUnit.MONTHS, 3),
        MONTH_1(ChronoUnit.MONTHS, 1),
        WEEK(ChronoUnit.WEEKS, 1),
        DAY(ChronoUnit.DAYS, 1),
        HOUR_12(ChronoUnit.HOURS, 12),
        HOUR_6(ChronoUnit.HOURS, 6),
        HOUR_3(ChronoUnit.HOURS, 3),
        HOUR_1(ChronoUnit.HOURS, 1),
        MINUTE_15(ChronoUnit.MINUTES, 15),
        MINUTE_5(ChronoUnit.MINUTES, 5),
        MINUTE_1(ChronoUnit.MINUTES, 1),
        SECOND_15(ChronoUnit.SECONDS, 15),
        SECOND_5(ChronoUnit.SECONDS, 5),
        SECOND_1(ChronoUnit.SECONDS, 1),
        MILLISECOND(ChronoUnit.MILLIS, 1);

        private final ChronoUnit INTERVAL;
        private final int        AMOUNT;

        private Interval(final ChronoUnit INTERVAL, final int AMOUNT) {
            this.INTERVAL = INTERVAL;
            this.AMOUNT = AMOUNT;
        }
    }

    private final LongProperty                                   currentLowerBound;
    private final LongProperty                                   currentUpperBound;
    private final ObjectProperty<StringConverter<LocalDateTime>> tickLabelFormatter;
    private       LocalDateTime                                  minDate;
    private       LocalDateTime                                  maxDate;
    private       ObjectProperty<LocalDateTime>                  lowerBound;
    private       ObjectProperty<LocalDateTime>                  upperBound;
    private       ChartLayoutAnimator                            animator;
    private       Object                                         currentAnimationID;
    private       Interval                                       actualInterval;


    // ******************** Constructors **************************************
    public LocalDateTimeAxis() {
        this("", LocalDateTime.now(), LocalDateTime.now().plusHours(1), true);
    }
    public LocalDateTimeAxis(final boolean AUTO_RANGING) {
        this("", LocalDateTime.now(), LocalDateTime.now().plusHours(1), AUTO_RANGING);
    }
    public LocalDateTimeAxis(final LocalDateTime LOWER_BOUND, final LocalDateTime UPPER_BOUND) {
        this("", LOWER_BOUND, UPPER_BOUND, true);
    }
    public LocalDateTimeAxis(final LocalDateTime LOWER_BOUND, final LocalDateTime UPPER_BOUND, final boolean AUTO_RANGING) {
        this("", LOWER_BOUND, UPPER_BOUND, AUTO_RANGING);
    }
    public LocalDateTimeAxis(final String AXIS_LABEL, final LocalDateTime LOWER_BOUND, final LocalDateTime UPPER_BOUND, final boolean AUTO_RANGING) {
        super();
        if (LOWER_BOUND.isAfter(UPPER_BOUND)) throw new IllegalArgumentException("Lower bound must be before upper bound!!!");
        currentLowerBound  = new SimpleLongProperty(this, "currentLowerBound");
        currentUpperBound  = new SimpleLongProperty(this, "currentUpperBound");
        tickLabelFormatter = new ObjectPropertyBase<StringConverter<LocalDateTime>>() {
            @Override protected void invalidated() {
                if (!isAutoRanging()) {
                    invalidateRange();
                    requestAxisLayout();
                }
            }
            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "tickLabelFormatter";
            }
        };
        lowerBound         = new ObjectPropertyBase<LocalDateTime>(LOWER_BOUND) {
            @Override protected void invalidated() {
                if (!isAutoRanging()) {
                    invalidateRange();
                    requestAxisLayout();
                }
            }
            @Override public Object getBean() {
                return LocalDateTimeAxis.this;
            }
            @Override public String getName() {
                return "lowerBound";
            }
        };
        upperBound         = new ObjectPropertyBase<LocalDateTime>(UPPER_BOUND) {
            @Override protected void invalidated() {
                if (!isAutoRanging()) {
                    invalidateRange();
                    requestAxisLayout();
                }
            }
            @Override public Object getBean() {
                return LocalDateTimeAxis.this;
            }
            @Override public String getName() {
                return "upperBound";
            }
        };
        animator           = new ChartLayoutAnimator(this);
        actualInterval     = Interval.DECADE;
        setLabel(AXIS_LABEL);
        setAutoRanging(AUTO_RANGING);
    }


    // ******************** Methods *******************************************
    @Override protected Object getRange() {
        return new Object[]{getLowerBound(), getUpperBound()};
    }
    @Override protected void setRange(final Object RANGE, final boolean ANIMATED) {
        Object[]      range         = (Object[]) RANGE;
        LocalDateTime oldLowerBound = getLowerBound();
        LocalDateTime oldUpperBound = getUpperBound();
        LocalDateTime lower         = (LocalDateTime) range[0];
        LocalDateTime upper         = (LocalDateTime) range[1];
        setLowerBound(lower);
        setUpperBound(upper);

        if (ANIMATED) {
            animator.stop(currentAnimationID);
            currentAnimationID = animator.animate(
                new KeyFrame(Duration.ZERO,
                             new KeyValue(currentLowerBound, toMillis(oldLowerBound)),
                             new KeyValue(currentUpperBound, toMillis(oldUpperBound))
                ),
                new KeyFrame(Duration.millis(700),
                             new KeyValue(currentLowerBound, toMillis(lower)),
                             new KeyValue(currentUpperBound, toMillis(upper))
                )
            );

        } else {
            currentLowerBound.set(toMillis(getLowerBound()));
            currentUpperBound.set(toMillis(getUpperBound()));
        }
    }
    @Override public void invalidateRange(final List<LocalDateTime> LIST) {
        super.invalidateRange(LIST);

        Collections.sort(LIST);
        if (LIST.isEmpty()) {
            minDate = maxDate = LocalDateTime.now();
        } else if (LIST.size() == 1) {
            minDate = maxDate = LIST.get(0);
        } else if (LIST.size() > 1) {
            minDate = LIST.get(0);
            maxDate = LIST.get(LIST.size() - 1);
        }
    }
    @Override protected Object autoRange(final double LENGTH) {
        if (isAutoRanging()) {
            return new Object[]{minDate, maxDate};
        } else {
            if (null == getLowerBound() || null == getUpperBound()) {
                throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
            }
            return getRange();
        }
    }

    @Override public double getZeroPosition() {
        return 0;
    }
    @Override public double getDisplayPosition(LocalDateTime date) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        // Then get the difference from the actual date to the min date and divide it by the total difference.
        // We get a value between 0 and 1, if the date is within the min and max date.
        double d = (toMillis(date) - currentLowerBound.get()) / diff;

        // Multiply this percent value with the range and add the zero offset.
        if (getSide().isVertical()) {
            return getHeight() - d * range + getZeroPosition();
        } else {
            return d * range + getZeroPosition();
        }
    }

    @Override public LocalDateTime getValueForDisplay(double displayPosition) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();
        Instant instant;
        if (getSide().isVertical()) {
            // displayPosition = getHeight() - ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero - getHeight())/range * diff + lowerBound                                        
            instant = Instant.ofEpochMilli((long) ((displayPosition - getZeroPosition() - getHeight()) / -range * diff + currentLowerBound.get()));
        } else {
            // displayPosition = ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero)/range * diff + lowerBound
            instant = Instant.ofEpochMilli((long) ((displayPosition - getZeroPosition()) / range * diff + currentLowerBound.get()));
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override public boolean isValueOnAxis(LocalDateTime date) {
        return toMillis(date) > currentLowerBound.get() && toMillis(date) < currentUpperBound.get();
    }

    @Override protected List<LocalDateTime> calculateTickValues(final double VALUE, final Object RANGE) {
        Object[]      range = (Object[]) RANGE;
        LocalDateTime lower = (LocalDateTime) range[0];
        LocalDateTime upper = (LocalDateTime) range[1];

        List<LocalDateTime> dateList = new ArrayList<>();
        LocalDateTime       calendar = LocalDateTime.now();

        // The preferred gap which should be between two tick marks.
        double averageTickGap = 100;
        double averageTicks   = VALUE / averageTickGap;

        List<LocalDateTime> previousDateList = new ArrayList<>();

        Interval previousInterval = Interval.values()[0];

        // Starting with the greatest interval, add one of each calendar unit.
        for (Interval interval : Interval.values()) {
            // Reset the calendar.                        
            calendar = LocalDateTime.of(lower.toLocalDate(), lower.toLocalTime());
            // Clear the list.
            dateList.clear();
            previousDateList.clear();
            actualInterval = interval;

            // Loop as long we exceeded the upper bound.            
            while(calendar.isBefore(upper)) {
                dateList.add(calendar);
                calendar = calendar.plus(interval.AMOUNT, interval.INTERVAL);
            }

            // Then check the size of the list. If it is greater than the amount of ticks, take that list.
            if (dateList.size() > averageTicks) {
                calendar = LocalDateTime.of(lower.toLocalDate(), lower.toLocalTime());
                // Recheck if the previous interval is better suited.                
                while(calendar.isBefore(upper) || calendar.isEqual(upper)) {
                    previousDateList.add(calendar);
                    calendar = calendar.plus(previousInterval.AMOUNT, previousInterval.INTERVAL);
                }
                break;
            }

            previousInterval = interval;
        }
        if (previousDateList.size() - averageTicks > averageTicks - dateList.size()) {
            dateList = previousDateList;
            actualInterval = previousInterval;
        }

        // At last add the upper bound.
        dateList.add(upper);

        List<LocalDateTime> evenDateList = makeDatesEven(dateList);
        
        // If there are at least three dates, check if the gap between the lower date and the second date is at least half the gap of the second and third date.
        // Do the same for the upper bound.
        // If gaps between dates are to small, remove one of them.
        // This can occur, e.g. if the lower bound is 25.12.2013 and years are shown. Then the next year shown would be 2014 (01.01.2014) which would be too narrow to 25.12.2013.
        if (evenDateList.size() > 2) {

            LocalDateTime secondDate       = evenDateList.get(1);
            LocalDateTime thirdDate        = evenDateList.get(2);
            LocalDateTime lastDate         = evenDateList.get(dateList.size() - 2);
            LocalDateTime previousLastDate = evenDateList.get(dateList.size() - 3);

            // If the second date is too near by the lower bound, remove it.                        

            if (secondDate.toEpochSecond(ZoneOffset.ofHours(0)) - lower.toEpochSecond(ZoneOffset.ofHours(0)) < (thirdDate.toEpochSecond(ZoneOffset.ofHours(0)) - secondDate.toEpochSecond(ZoneOffset.ofHours(0))) * 0.5) {
                evenDateList.remove(secondDate);
            }

            // If difference from the upper bound to the last date is less than the half of the difference of the previous two dates,
            // we better remove the last date, as it comes to close to the upper bound.
            if (upper.toEpochSecond(ZoneOffset.ofHours(0)) - lastDate.toEpochSecond(ZoneOffset.ofHours(0)) < ((lastDate.toEpochSecond(ZoneOffset.ofHours(0)) - previousLastDate.toEpochSecond(ZoneOffset.ofHours(0))) * 0.5)) {
                evenDateList.remove(lastDate);
            }
        }
        return evenDateList;
    }

    @Override protected void layoutChildren() {
        if (!isAutoRanging()) {
            currentLowerBound.set(toMillis(getLowerBound()));
            currentUpperBound.set(toMillis(getUpperBound()));
        }
        super.layoutChildren();
    }

    @Override protected String getTickMarkLabel(LocalDateTime date) {

        StringConverter<LocalDateTime> converter = getTickLabelFormatter();
        if (converter != null) {
            return converter.toString(date);
        }

        DateTimeFormatter dateTimeFormat;
        LocalDateTime calendar = LocalDateTime.of(date.toLocalDate(), date.toLocalTime());

        if (actualInterval.INTERVAL == ChronoUnit.YEARS && calendar.getMonthValue() == 0 && calendar.getDayOfMonth() == 1) {
            dateTimeFormat = DateTimeFormatter.ofPattern("yyyy");
        } else if (actualInterval.INTERVAL == ChronoUnit.MONTHS && calendar.getDayOfMonth() == 1) {
            dateTimeFormat = DateTimeFormatter.ofPattern("MMMM yy");
        } else {
            switch (actualInterval.INTERVAL) {
                case DAYS:
                case WEEKS:
                default:
                    dateTimeFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                    break;
                case HOURS:
                case MINUTES:
                    dateTimeFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
                    break;
                case SECONDS:
                    dateTimeFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                    break;
                case MILLIS:
                    dateTimeFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
                    break;
            }
        }

        return dateTimeFormat.format(date);
    }

    private List<LocalDateTime> makeDatesEven(List<LocalDateTime> dates) {
		// If the dates contain more dates than just the lower and upper bounds,
		// make the dates in between even.
		if (dates.size() > 2) {
			List<LocalDateTime> evenDates = new ArrayList<>();

			// For each interval, modify the date slightly by a few millis, to
			// make sure they are different days.
			// This is because Axis stores each value and won't update the tick
			// labels, if the value is already known.
			// This happens if you display days and then add a date many years
			// in the future the tick label will still be displayed as day.
			for (int i = 0; i < dates.size(); i++) {
				LocalDateTime calendar = dates.get(i);
				switch (actualInterval.INTERVAL) {
				case YEARS:
					// If its not the first or last date (lower and upper
					// bound), make the year begin with first month and let the
					// months begin with first day.
					if (i != 0 && i != dates.size() - 1) {
						calendar = calendar.withMonth(1).withDayOfMonth(1);
					}
					calendar = calendar.withHour(0)
							.withMinute(0)
							.withSecond(0)
							.withNano(6000000);
					break;
				case MONTHS:
					// If its not the first or last date (lower and upper
					// bound), make the months begin with first day.
					if (i != 0 && i != dates.size() - 1) {
						calendar = calendar.withDayOfMonth(1);
					}
					calendar = calendar.withHour(0)
							.withMinute(0)
							.withSecond(0)
							.withNano(5000000);
					break;
				case WEEKS:
					// Make weeks begin with first day of week?
					calendar = calendar.withHour(0)
						.withMinute(0)
						.withSecond(0)
						.withNano(4000000);
					break;
				case DAYS:
					calendar = calendar.withHour(0)
						.withMinute(0)
						.withSecond(0)
						.withNano(3000000);
					break;
				case HOURS:
					if (i != 0 && i != dates.size() - 1) {
						calendar = calendar.withSecond(0);
					}
					calendar = calendar.withNano(2000000);
					break;
				case MINUTES:
					calendar = calendar.withNano(1000000);
					break;
				case SECONDS:
					calendar = calendar.withSecond(0);
					break;
				default:
					break;

				}
				evenDates.add(calendar);
			}

			return evenDates;
		} else {
			return dates;
		}
	}

    public final LocalDateTime getLowerBound() {
        return lowerBound.get();
    }
    public final void setLowerBound(LocalDateTime date) {
        lowerBound.set(date);
    }
    public final ObjectProperty<LocalDateTime> lowerBoundProperty() {
        return lowerBound;
    }

    public final LocalDateTime getUpperBound() {
        return upperBound.get();
    }
    public final void setUpperBound(LocalDateTime date) {
        upperBound.set(date);
    }
    public final ObjectProperty<LocalDateTime> upperBoundProperty() {
        return upperBound;
    }

    public final StringConverter<LocalDateTime> getTickLabelFormatter() {
        return tickLabelFormatter.getValue();
    }
    public final void setTickLabelFormatter(StringConverter<LocalDateTime> value) {
        tickLabelFormatter.setValue(value);
    }
    public final ObjectProperty<StringConverter<LocalDateTime>> tickLabelFormatterProperty() {
        return tickLabelFormatter;
    }

    @Override public double toNumericValue(final LocalDateTime DATE) {
        return toMillis(DATE);
    }
    @Override public LocalDateTime toRealValue(final double VALUE) {
        return toLocalDateTime((long) VALUE);
    }

    private long toMillis(final LocalDateTime DATE_TIME) {
		ZoneOffset offset = DATE_TIME.atZone(ZoneId.systemDefault()).getOffset();
		return DATE_TIME.toInstant(offset).getEpochSecond() * 1000l;		
	}
    
    private LocalDateTime toLocalDateTime(final long MILLIS) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(MILLIS), ZoneId.systemDefault());
    }
}
