package wise.risk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day2 {

    public static void answer() throws IOException, URISyntaxException {
        System.out.println("The answer for the first test game is " + First.test());
        System.out.println("The answer for the first game is " + First.calculate());
        System.out.println("The answer for the second test game is " + Second.test());
        System.out.println("The answer for the second game is " + Second.calculate());
    }

    private static List<String> extractTurn(String move) {
        return Arrays.asList(move.trim().split(";"));
    }

    private static List<String> extractPlay(String move) {
        return Arrays.asList(move.trim().split(","));
    }

    private static Map<Color, Integer> extractPick(List<String> play) {
        return play.stream()
                .map(String::trim)
                .collect(Collectors.toMap(Day2::getColor, Day2::getNumber));
    }

    private static Color getColor(String s) {
        return Color.valueFrom(s.split(" ")[1]);
    }

    private static Integer getNumber(String s) {
        return Integer.valueOf(s.split(" ")[0]);
    }

    private static class First {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day2/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day2/firstTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return String.valueOf(challenge.stream()
                    .filter(s -> isPossible(s.split(":")[1]))
                    .map(s -> extractId(s.split(":")[0]))
                    .reduce(Integer::sum)
                    .orElse(0));
        }

        private static Integer extractId(String id) {
            return Integer.valueOf(id.replace("Game ", ""));
        }

        private static boolean isPossible(String move) {
            return extractTurn(move).stream()
                    .map(Day2::extractPlay)
                    .map(Day2::extractPick)
                    .allMatch(First::inLimit);
        }

        private static boolean inLimit(Map<Color, Integer> game) {
            return game.getOrDefault(Color.RED, 0) <= Color.RED.limit &&
                    game.getOrDefault(Color.BLUE, 0) <= Color.BLUE.limit &&
                    game.getOrDefault(Color.GREEN, 0) <= Color.GREEN.limit;
        }
    }

    private static class Second {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day2/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day2/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return String.valueOf(challenge.stream()
                    .map(Second::ExtractMaxPicks)
                    .map(Second::multiplyPick)
                    .reduce(Integer::sum)
                    .orElse(0));
        }

        private static Integer multiplyPick(Map<Color, Integer> picks) {
            return picks.getOrDefault(Color.RED, 1) *
                    picks.getOrDefault(Color.BLUE, 1) *
                    picks.getOrDefault(Color.GREEN, 1);
        }

        private static Map<Color, Integer> ExtractMaxPicks(String move) {
            return extractTurn(move.split(":")[1]).stream()
                    .map(Day2::extractPlay)
                    .map(Day2::extractPick)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(
                            HashMap::new,
                            (map, entry) -> map.merge(entry.getKey(), entry.getValue(), Integer::max),
                            HashMap::putAll
                    );
        }
    }

    private enum Color {
        RED("red", 12),
        GREEN("green", 13),
        BLUE("blue", 14);

        int limit;
        String color;

        Color(String color, int limit) {
            this.color = color;
            this.limit = limit;
        }

        public static Color valueFrom(String color) {
            return Color.valueOf(color.toUpperCase());
        }
    }
}
