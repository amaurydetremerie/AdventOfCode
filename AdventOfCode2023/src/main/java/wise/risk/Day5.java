package wise.risk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day5 {

    public static void answer() throws IOException, URISyntaxException {
        System.out.println("The answer for the first test game is " + First.test());
        System.out.println("The answer for the first game is " + First.calculate());
        System.out.println("The answer for the second test game is " + Second.test());
        System.out.println("The answer for the second game is " + Second.calculate());
    }

    private static Long calculateForSeeds(List<String> challenge, List<Long> seeds) {
        List<String> mapsLines = parseChallenge(challenge);
        List<String> seedToSoil = createList(mapsLines.get(0));
        List<String> soilToFertilizer = createList(mapsLines.get(1));
        List<String> fertilizerToWater = createList(mapsLines.get(2));
        List<String> waterToLight = createList(mapsLines.get(3));
        List<String> lightToTemperature = createList(mapsLines.get(4));
        List<String> temperatureToHumidity = createList(mapsLines.get(5));
        List<String> humidityToLocation = createList(mapsLines.get(6));
        return seeds.stream()
                .parallel()
                .map(i -> findBinding(i, seedToSoil))
                .map(i -> findBinding(i, soilToFertilizer))
                .map(i -> findBinding(i, fertilizerToWater))
                .map(i -> findBinding(i, waterToLight))
                .map(i -> findBinding(i, lightToTemperature))
                .map(i -> findBinding(i, temperatureToHumidity))
                .map(i -> findBinding(i, humidityToLocation))
                .reduce(Long::min).orElse(0L);
    }

    private static long findBinding(long previous, List<String> bindings) {
        AtomicLong bind = new AtomicLong(previous);
        bindings.stream().map(s -> s.split(" "))
                .parallel()
                .map(a -> Arrays.stream(a).parallel().map(Long::parseLong).collect(Collectors.toList()))
                .filter(l -> previous >= l.get(1) && previous < l.get(1) + l.get(2))
                .findFirst().ifPresent(l -> bind.set(l.get(0) - l.get(1) + previous));
        return bind.get();
    }

    private static List<String> createList(String mapLine) {
        return Arrays.stream(mapLine.split(Pattern.quote("|")))
                .collect(Collectors.toList());
    }

    private static String[] getMapsLines(List<String> challenge) {
        return removeUnecessaryLines(challenge).split("\n");
    }

    private static String removeUnecessaryLines(List<String> challenge) {
        return delimitMapsValues(challenge).replaceAll("\n\n", "\n");
    }

    private static String delimitMapsValues(List<String> challenge) {
        return replaceChars(delimitMaps(challenge), Pattern.quote("|\n"), 0, '\n');
    }

    private static String delimitMaps(List<String> challenge) {
        return replaceChars(removeTitlesOfMaps(challenge), "(?:\\d)(\n)", 1, '|');
    }

    private static String removeTitlesOfMaps(List<String> challenge) {
        return unionOfLines(challenge).replaceAll("[a-zA-Z\\-]+ map:", "");
    }

    private static String unionOfLines(List<String> challenge) {
        return challenge.stream().filter(s1 -> !s1.isBlank()).collect(Collectors.joining("\n"));
    }

    private static String replaceChars(String input, String regex, int group, char ch) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder(input);
        while (matcher.find()) {
            sb.setCharAt(matcher.start(group), ch);
        }
        return sb.toString();
    }

    private static class First {
        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day5/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day5/firstTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            List<Long> seeds = Arrays.stream(challenge.get(0).split(":")[1].trim().split(" ")).map(Long::parseLong).collect(Collectors.toList());
            return String.valueOf(Day5.calculateForSeeds(challenge.subList(1, challenge.size()), seeds));
        }

    }

    private static class Second {

        public static String calculate() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day5/challenge.txt");
            return calculate(challenge);
        }

        public static String test() throws IOException, URISyntaxException {
            List<String> challenge = Utils.openFile("day5/secondTest.txt");
            return calculate(challenge);
        }

        private static String calculate(List<String> challenge) {
            List<String> seeds = Arrays.stream(challenge.get(0).split(":")[1].trim().split(" ")).collect(Collectors.toList());
            return String.valueOf(IntStream.iterate(0, i -> i + 2)
                    .limit((seeds.size()) / 2).boxed()
                    .map(i -> getSeedsAndMultiplier(seeds, i))
                    .parallel()
                    .map(Second::parseSeedsAndMultiplier)
                    .map(parsed -> Second.findBindings(challenge.subList(1, challenge.size()), parsed))
                    .reduce(Long::min).orElse(0L));
        }

        private static long findBindings(List<String> challenge, List<Long> parsed) {
            List<String> mapsLines = parseChallenge(challenge);
            List<String> seedToSoil = createList(mapsLines.get(0));
            List<String> soilToFertilizer = createList(mapsLines.get(1));
            List<String> fertilizerToWater = createList(mapsLines.get(2));
            List<String> waterToLight = createList(mapsLines.get(3));
            List<String> lightToTemperature = createList(mapsLines.get(4));
            List<String> temperatureToHumidity = createList(mapsLines.get(5));
            List<String> humidityToLocation = createList(mapsLines.get(6));
            return LongStream.range(parsed.get(0), parsed.get(0) + parsed.get(1))
                    .boxed()
                    .parallel()
                    .map(i -> findBindings(i, seedToSoil, soilToFertilizer, fertilizerToWater, waterToLight,
                            lightToTemperature, temperatureToHumidity, humidityToLocation))
                    .reduce(Long::min).orElse(Long.MAX_VALUE);
        }

        private static long findBindings(long previous, List<String> seedToSoil, List<String> soilToFertilizer,
                                         List<String> fertilizerToWater, List<String> waterToLight,
                                         List<String> lightToTemperature, List<String> temperatureToHumidity,
                                         List<String> humidityToLocation) {
            return findBinding(
                    findBinding(
                            findBinding(
                                    findBinding(
                                            findBinding(
                                                    findBinding(
                                                            findBinding(
                                                                    previous,
                                                                    seedToSoil),
                                                            soilToFertilizer),
                                                    fertilizerToWater),
                                            waterToLight),
                                    lightToTemperature),
                            temperatureToHumidity),
                    humidityToLocation);
        }


        private static String getSeedsAndMultiplier(List<String> seeds, Integer i) {
            return seeds.get(i) + " " + seeds.get(i + 1);
        }

        private static List<Long> parseSeedsAndMultiplier(String s) {
            return Arrays.stream(s.split(" ")).map(Long::parseLong).collect(Collectors.toList());
        }
    }

    private static List<String> parseChallenge(List<String> challenge) {
        return Arrays.stream(getMapsLines(challenge)).filter(s -> !s.isBlank()).collect(Collectors.toList());
    }
}
