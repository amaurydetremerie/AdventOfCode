package wise.risk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day1 {

    public static void answer() throws IOException, URISyntaxException {
        System.out.println("The answer for the first test game is " + First.test());
        System.out.println("The answer for the first game is " + First.calculate());
        System.out.println("The answer for the second test game is " + Second.test());
        System.out.println("The answer for the second game is " + Second.calculate());
    }

    private static class First {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day1/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day1/firstTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return challenge.stream()
                    .map(s -> getFirstNumber(s) + getLastNumber(s))
                    .reduce(Integer::sum)
                    .map(String::valueOf)
                    .orElse(null);
        }

        private static int getLastNumber(String line) {
            return getStream(line)
                    .reduce((first, second) -> second)
                    .orElse(0);
        }

        private static IntStream getStream(String line) {
            return line.chars()
                    .filter(Character::isDigit)
                    .map(Character::getNumericValue);
        }

        private static int getFirstNumber(String line) {
            return 10 * getStream(line)
                    .findFirst()
                    .orElse(0);
        }
    }

    private static class Second {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day1/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day1/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return challenge.stream()
                    .map(s -> getFirstNumber(s) + getLastNumber(s))
                    //.peek(System.out::println)
                    .reduce(Integer::sum)
                    .map(String::valueOf)
                    .orElse(null);
        }

        private static int getLastNumber(String line) {
            return findLastInLine(line).digitValue;
        }

        private static int getFirstNumber(String line) {
            return 10*findFirstInLine(line).digitValue;
        }

        private static Numbers findLastInLine(String line) {
            Numbers digit = getLastDigitNumber(line);
            Numbers string = getLastStringNumber(line);
            return line.lastIndexOf("" + digit.digitValue) > line.lastIndexOf(string.stringValue) ? digit : string;
        }

        private static Numbers findFirstInLine(String line) {
            Numbers digit = getFirstDigitNumber(line);
            Numbers string = getFirstStringNumber(line);
            return verifyDigit(digit, string, line.indexOf("" + digit.digitValue), line.indexOf(string.stringValue));
        }

        private static Numbers verifyDigit(Numbers digit, Numbers string, int digitIndex, int stringIndex) {
            return digitIndex == -1 ? string : verifyString(digit, string, digitIndex, stringIndex);
        }

        private static Numbers verifyString(Numbers digit, Numbers string, int digitIndex, int stringIndex) {
            return stringIndex == -1 ? digit : findFirstInLine(digit, string, digitIndex, stringIndex);
        }

        private static Numbers findFirstInLine(Numbers digit, Numbers string, int digitIndex, int stringIndex) {
            return digitIndex < stringIndex ? digit : string;
        }

        private static Numbers getLastStringNumber(String line) {
            return getLastStringNumber(line, Numbers.ONE, new HashMap<>())
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() != -1)
                    .reduce((a,b) -> a.getValue() > b.getValue() ? a : b)
                    .map(Map.Entry::getKey)
                    .orElse(Numbers.ZERO);
        }

        private static Map<Numbers, Integer> getLastStringNumber(String line, Numbers numbers, Map<Numbers, Integer> map) {
            map.put(numbers, line.lastIndexOf(numbers.stringValue));
            if (numbers == Numbers.NINE)
                return map;
            return getLastStringNumber(line, Numbers.getNext(numbers), map);
        }

        private static Numbers getFirstStringNumber(String line) {
            return getFirstStringNumber(line, Numbers.ONE, new HashMap<>())
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() != -1)
                    .reduce((a,b) -> a.getValue() < b.getValue() ? a : b)
                    .map(Map.Entry::getKey)
                    .orElse(Numbers.ZERO);
        }

        private static Map<Numbers, Integer> getFirstStringNumber(String line, Numbers numbers, Map<Numbers, Integer> map) {
            map.put(numbers, line.indexOf(numbers.stringValue));
            if (numbers == Numbers.NINE)
                return map;
            return getFirstStringNumber(line, Numbers.getNext(numbers), map);
        }

        private static Numbers getLastDigitNumber(String line) {
            return getStream(line)
                    .mapToObj(Numbers::digitValueOf)
                    .reduce((first, second) -> second)
                    .orElse(Numbers.ZERO);
        }

        private static Numbers getFirstDigitNumber(String line) {
            return getStream(line)
                    .mapToObj(Numbers::digitValueOf)
                    .findFirst()
                    .orElse(Numbers.ZERO);
        }

        private static IntStream getStream(String line) {
            return line.chars()
                    .filter(Character::isDigit)
                    .map(Character::getNumericValue);
        }
    }

    private enum Numbers {
        ZERO("zero", 0),
        ONE("one", 1),
        TWO("two", 2),
        THREE("three", 3),
        FOUR("four", 4),
        FIVE("five", 5),
        SIX("six", 6),
        SEVEN("seven", 7),
        EIGHT("eight", 8),
        NINE("nine", 9);

        final String stringValue;
        final int digitValue;

        Numbers(String stringValue, int digitValue) {
            this.stringValue = stringValue;
            this.digitValue = digitValue;
        }

        public static Numbers getNext(Numbers value) {
            switch (value) {
                case ONE:
                    return TWO;
                case TWO:
                    return THREE;
                case THREE:
                    return FOUR;
                case FOUR:
                    return FIVE;
                case FIVE:
                    return SIX;
                case SIX:
                    return SEVEN;
                case SEVEN:
                    return EIGHT;
                case EIGHT:
                    return NINE;
                default:
                    return ZERO;
            }
        }

        public static Numbers stringValueOf(String value) {
            switch (value) {
                case "one":
                    return ONE;
                case "two":
                    return TWO;
                case "three":
                    return THREE;
                case "four":
                    return FOUR;
                case "five":
                    return FIVE;
                case "six":
                    return SIX;
                case "seven":
                    return SEVEN;
                case "eight":
                    return EIGHT;
                case "nine":
                    return NINE;
                default:
                    return ZERO;
            }
        }

        public static Numbers digitValueOf(int value) {
            switch (value) {
                case 1:
                    return ONE;
                case 2:
                    return TWO;
                case 3:
                    return THREE;
                case 4:
                    return FOUR;
                case 5:
                    return FIVE;
                case 6:
                    return SIX;
                case 7:
                    return SEVEN;
                case 8:
                    return EIGHT;
                case 9:
                    return NINE;
                default:
                    return ZERO;
            }
        }
    }
}
