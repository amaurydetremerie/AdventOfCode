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

    private static class Second {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day3/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return null;
        }
    }

}
