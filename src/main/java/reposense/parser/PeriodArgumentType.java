package reposense.parser;

import java.util.Optional;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted period to an integer.
 */
public class PeriodArgumentType implements ArgumentType<Optional<Integer>> {
    private static final String PARSE_EXCEPTION_MESSAGE_NOT_IN_NUMERIC =
            "Invalid format. Period must be in the format of nd (n days) or nw (n weeks) or ny (n years), "
            + "where n is a number greater than 0.";
    private static final String PARSE_EXCEPTION_MESSAGE_SMALLER_THAN_ZERO =
            "Invalid format. Period must be greater than 0.";
    private static final String PARSE_EXCEPTION_MESSAGE_NUMBER_TOO_LARGE =
            "Invalid format. Input number may be too large.";

    private static final String PARSE_EXCEPTION_MESSAGE_NUMBER_UNRECOGNISED_IDENTIFIER =
            "Invalid format. Unrecognised indentifier.";
    private static final Pattern PERIOD_PATTERN = Pattern.compile("[0-9]+[dwy]");

    private static final String DAY_IDENTIFIER = "d";
    private static final String WEEK_IDENTIFIER = "w";
    private static final String YEAR_IDENTIFIER = "y";

    private static final int NUMBER_OF_DAYS_IN_A_DAY = 1;
    private static final int NUMBER_OF_DAYS_IN_A_WEEK = 7;
    private static final int NUMBER_OF_DAYS_IN_A_YEAR = 365;


    @Override
    public Optional<Integer> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        try {
            return parse(value);
        } catch (ParseException pe) {
            throw new ArgumentParserException(pe.getMessage(), parser);
        }
    }

    /**
     * Parses a {@code isDateWeekOrYear} String and returns an {@link Integer} representing the multiplier for the
     * type of period.
     *
     */
    private static int getMultiplier(String isDateWeekOrYear) throws ParseException {
        int multiplier;
        switch (isDateWeekOrYear) {
            case (DAY_IDENTIFIER):
                multiplier = NUMBER_OF_DAYS_IN_A_DAY;
                break;
            case (WEEK_IDENTIFIER):
                multiplier = NUMBER_OF_DAYS_IN_A_WEEK;
                break;
            case (YEAR_IDENTIFIER):
                multiplier = NUMBER_OF_DAYS_IN_A_YEAR;
                break;
            default:
                // should not reach here
                throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_NUMBER_UNRECOGNISED_IDENTIFIER,
                        isDateWeekOrYear));
        }
        return multiplier;
    }

    /**
     * Parses a {@code period} String and returns an {@link Integer} representing the number of days.
     *
     * @throws ParseException if period format or number is invalid.
     */
    public static Optional<Integer> parse(String period) throws ParseException {
        if (!PERIOD_PATTERN.matcher(period).matches()) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_NOT_IN_NUMERIC, period));
        }

        String isDateWeekOrYear = period.substring(period.length() - 1);
        int multiplier = getMultiplier(isDateWeekOrYear);

        try {
            int convertedValue = Integer.parseInt(period.substring(0, period.length() - 1)) * multiplier;
            if (convertedValue <= 0) {
                throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_SMALLER_THAN_ZERO, period));
            }

            return Optional.of(convertedValue);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_NUMBER_TOO_LARGE, period));
        }
    }
}
