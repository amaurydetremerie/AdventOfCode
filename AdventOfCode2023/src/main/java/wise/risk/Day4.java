package wise.risk;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {

    public static void answer() throws IOException, URISyntaxException {
        System.out.println("The answer for the first test game is " + First.test());
        System.out.println("The answer for the first game is " + First.calculate());
        System.out.println("The answer for the second test game is " + Second.test());
        System.out.println("The answer for the second game is " + Second.calculate());
    }

    private static Game toGame(String s) {
        String[] split = s.split(Pattern.quote("|"));
        return new Game(split[0], split[1]);
    }

    private static class First {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day4/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day4/firstTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            return String.valueOf(challenge.stream()
                    .map(StringUtils::normalizeSpace)
                    .map(Day4::toGame)
                    .map(Day4.Game::getWinningNumbers)
                    .filter(l -> !l.isEmpty())
                    .map(s -> Math.pow(2, s.size()-1D))
                    .map(Double::intValue)
                    .reduce(Integer::sum)
                    .orElse(0));
        }

    }

    private static class Second {

        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day4/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day4/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            List<Integer> played = new ArrayList<>();
            Map<Integer, Game> cards = challenge.stream()
                    .map(Day4::toGame)
                    .collect(Collectors.toMap(Game::getId, Function.identity()));
            List<Integer> toPlay = new ArrayList<>(cards.keySet());
            while (!toPlay.isEmpty()) {
                int game = toPlay.remove(0);
                toPlay.addAll(IntStream.range(game, game+cards.get(game).getWinningNumbers().size()).map(i -> i+1).boxed().collect(Collectors.toList()));
                played.add(game);
            }
            return String.valueOf(played.size());
        }
    }

    private static class Game {
        List<Integer> winningNumbers;
        List<Integer> myNumbers;
        Integer id;
        public Game(String winningNumbers, String myNumbers) {
            String[] split = winningNumbers.split(":");
            this.id = Integer.parseInt(Arrays.stream(split[0].trim().split(" ")).filter(s -> !s.isBlank()).reduce((a,b) -> b).orElse(""));
            this.winningNumbers = Arrays.stream(split[1].trim().split(" ")).filter(s -> !s.isBlank()).map(Integer::parseInt).collect(Collectors.toList());
            this.myNumbers = Arrays.stream(myNumbers.trim().split(" ")).filter(s -> !s.isBlank()).map(Integer::parseInt).collect(Collectors.toList());
        }

        public List<Integer> getWinningNumbers() {
            return myNumbers.stream().filter(n -> winningNumbers.contains(n)).collect(Collectors.toList());
        }

        public Integer getId() {
            return this.id;
        }
    }
}
