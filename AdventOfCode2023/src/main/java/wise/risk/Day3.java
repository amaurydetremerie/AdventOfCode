package wise.risk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day3 {

    public static void answer() throws IOException, URISyntaxException {
        System.out.println("The answer for the first test game is " + First.test());
        System.out.println("The answer for the first game is " + First.calculate());
        System.out.println("The answer for the second test game is " + Second.test());
        System.out.println("The answer for the second game is " + Second.calculate());
    }

    private static Line getLine(int index, List<String> challenge) {
        return new Line(getFromList(index - 1, challenge),getFromList(index, challenge),getFromList(index + 1, challenge));
    }

    private static String getFromList(int index, List<String> challenge) {
        try {
            return challenge.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static class First {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/firstTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return String.valueOf(IntStream.range(0, challenge.size())
                    .mapToObj(i -> getLine(i, challenge))
                    .map(First::findPieces)
                    .flatMap(List::stream)
                    .mapToInt(Integer::intValue)
                    .sum());
        }

        private static List<Integer> findPieces(Line line) {
            return Arrays.stream(line.actual.split(Pattern.quote(".")))
                    .map(First::cutSpecials)
                    .flatMap(List::stream)
                    .filter(s -> !s.isBlank())
                    .filter(s -> checkIfPiece(s, line))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        private static boolean checkIfPiece(String s, Line line) {
            Pattern pattern = Pattern.compile("(^|\\D)(" + s + ")(\\D|$)");
            Matcher matcher = pattern.matcher(line.actual);
            matcher.find();
            return verifyLine(line.prev, matcher.start(), matcher.end()) ||
                    verifyLine(line.next, matcher.start(), matcher.end()) ||
                    checkIfSpecials(line.actual, matcher.start(), matcher.end());
        }

        private static boolean verifyLine(String line, int start, int end) {
            return line != null && checkIfSpecials(line, start, end);
        }

        private static boolean checkIfSpecials(String line, int start, int end) {
            return line.substring(start, end).replace(".", "").chars().anyMatch(c -> !Character.isDigit(c));
        }

        private static List<String> cutSpecials(String s) {
            String specialsChar = s.chars()
                    .mapToObj(c -> (char) c)
                    .filter(c -> !Character.isDigit(c))
                    .map(String::valueOf)
                    .findFirst().orElse(null);
            if(specialsChar == null) {
                return List.of(s);
            }
            return Arrays.stream(s.split(Pattern.quote(specialsChar)))
                    .map(First::cutSpecials)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

    }

    private static class Second {

        private static final String RIGHT = "RIGHT";
        private static final String LEFT = "LEFT";
        private static final String BOTH = "BOTH";

        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return String.valueOf(IntStream.range(0, challenge.size())
                    .mapToObj(i -> getLine(i, challenge))
                    .filter(Second::hasGear)
                    .map(Second::getGear)
                    .flatMap(List::stream)
                    .filter(Second::isGear)
                    .map(Second::getGearValue)
                    .reduce(Integer::sum)
                    .orElse(0));
        }

        private static boolean isGear(List<Integer> gearValues) {
            return gearValues.size() == 2;
        }

        private static List<List<Integer>> getGear(Line line) {
            List<List<Integer>> result = new ArrayList<>();
            Pattern pattern = Pattern.compile(Pattern.quote("*"));
            Matcher matcher = pattern.matcher(line.actual);
            while (matcher.find()) {
                result.add(findGear(line, matcher));
            }
            return result;
        }

        private static List<Integer> findGear(Line line, Matcher matcher) {
            if(matcher.start() == 0)
                return findLimitLeft(line);
            if(matcher.end() == line.actual.length())
                return findLimitRight(line);
            return findCentered(line, matcher.start());
        }

        private static List<Integer> findCentered(Line line, int start) {
            List<Integer> result = new ArrayList<>();
            verifyCenter(line.actual, start, result);
            verifyBoth(line.prev, start, result);
            verifyBoth(line.next, start, result);
            return result;
        }

        private static void verifyBoth(String line, int start, List<Integer> result) {
            if(verifyLine(line, start))
                result.add(getNumber(BOTH, line, start));
            else{
                if(verifyLine(line, start - 1))
                    result.add(getNumber(LEFT, line, start - 1));
                if(verifyLine(line, start + 1))
                    result.add(getNumber(RIGHT, line, start + 1));
            }
        }

        private static void verifyCenter(String line, int start, List<Integer> result) {
            if(verifyLine(line, start - 1))
                result.add(getNumber(LEFT, line, start -1));
            if(verifyLine(line, start + 1))
                result.add(getNumber(RIGHT, line, start +1));
        }

        private static boolean verifyLine(String line, int start) {
            return line != null && Character.isDigit(line.charAt(start));
        }

        private static Integer getNumber(String direction, String line, int start) {
            if(direction.equals(LEFT)) {
                return Integer.valueOf(findLeft(line, start));
            }
            if(direction.equals(RIGHT)) {
                return Integer.valueOf(findRight(line, start));
            }
            StringBuilder find = new StringBuilder();
            return Integer.valueOf(find.append(findLeft(line,start-1))
                    .append(line.charAt(start))
                    .append(findRight(line, start+1))
                    .toString());
        }

        private static String findRight(String line, int start) {
            StringBuilder find = new StringBuilder();
            int i = 0;
            while (start+i != line.length() && Character.isDigit(line.charAt(start+i))) {
                find.append(line.charAt(start+i));
                i++;
            }
            return find.toString();
        }

        private static String findLeft(String line, int start) {
            StringBuilder find = new StringBuilder();
            int i = 0;
            while (start-i >= 0 && Character.isDigit(line.charAt(start-i))) {
                find.append(line.charAt(start-i));
                i++;
            }
            return find.reverse().toString();
        }

        private static List<Integer> findLimitRight(Line line) {
            List<Integer> result = new ArrayList<>();
            if(verifyLine(line.actual, line.actual.length() - 2))
                result.add(getNumber(LEFT, line.actual, line.actual.length() - 2));
            verifyLimitRight(line.prev, result);
            verifyLimitRight(line.next, result);
            return result;
        }

        private static void verifyLimitRight(String line, List<Integer> result) {
            if(verifyLine(line, line.length() - 1))
                result.add(getNumber(LEFT, line, line.length() - 1));
            else if(verifyLine(line, line.length() - 2))
                result.add(getNumber(LEFT, line, line.length() - 2));
        }

        private static List<Integer> findLimitLeft(Line line) {
            List<Integer> result = new ArrayList<>();
            if(verifyLine(line.actual, 1))
                result.add(getNumber(RIGHT, line.actual, 1));
            verifyLimitLeft(line.prev, result);
            verifyLimitLeft(line.next, result);
            return result;
        }

        private static void verifyLimitLeft(String line, List<Integer> result) {
            if(verifyLine(line, 0))
                result.add(getNumber(RIGHT, line, 0));
            else if(verifyLine(line, 1))
                result.add(getNumber(RIGHT, line, 1));
        }

        private static int getGearValue(List<Integer> gear) {
            return gear.stream().reduce((a,b) -> a*b).orElse(0);
        }

        private static boolean hasGear(Line line) {
            Pattern pattern = Pattern.compile(Pattern.quote("*"));
            Matcher matcher = pattern.matcher(line.actual);
            return matcher.find();
        }
    }

    private static class Line {
        final String prev;
        final String actual;
        final String next;

        public Line(String prev, String actual, String next) {
            this.prev = prev;
            this.actual = actual;
            this.next = next;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if(prev != null)
                sb.append(prev).append("\n");
            sb.append(actual);
            if (next != null)
                sb.append("\n").append(next);
            return sb.toString();
        }
    }
}
