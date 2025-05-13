package org.streams;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StreamsSamples {

    final User john = new User("john", 1, false, Genre.M, List.of("john@email.com"));
    final User mary = new User("mary", 2, true, Genre.F, List.of("mary@email.com", "mary2@email.com"));
    final User mane = new User("mane", 3, false, Genre.M,
            List.of("mane@email.com", "mane2@email.com", "mane3@email.com"));
    final User badUser = new User(null, 0, true, Genre.M, null);

    public static void main(String[] args) {
        // iterateSample();

        // filterSample();

        // mapSample();

        // streamOfNullableSample();

        // mapToLongIntDoubleSample();

        // flatMapSample();

        // mapMultiSample();

        // removeDuplicatasSample();

        // sortsOrderSample();

        // limitIterationsSample();

        // skipFirstElementsSample();

        // takeWhileSample();

        // dropWhileSample();

        // peekSample();

        // collectToListSample();

        // collectToSetSample();

        // collectToMapSample();

        // groupingBySample();

        // partitioningBySample();

        // collectorsJoiningSample();

        // collectorsCountingSample();

        // reduceSample();

        //reduceSample2();

        // anyMatchSample();

        // allMatchSample();

        // noneMatchSample();

    }

    private static void noneMatchSample() {
        Predicate<? super User> predicate = User::isActive;
        System.out.println(Stream.of(john, mary, mane).noneMatch(predicate));
    }

    private static void allMatchSample() {
        Predicate<? super User> predicate = User::isActive;
        System.out.println(Stream.of(john, mary, mane).allMatch(predicate));
    }

    private static void anyMatchSample() {
        Predicate<? super User> predicate = User::isActive;
        System.out.println(Stream.of(john, mary, mane).anyMatch(predicate));
    }

    private static void reduceSample2() {
        BinaryOperator<String> binaryOperator = (a, b) -> a + ", " + b;
        System.out.println(Stream.of(john, mary, mane)
        .map(User::name)
        .reduce(binaryOperator)
        .orElse("nenhum nome encontrado"));
    }

    private static void reduceSample() {
        BinaryOperator<Integer> binaryOperator = Integer::sum;
        System.out.println(Stream.of(john, mary, mane)
                .map(User::age)
                .reduce(3, binaryOperator));
    }

    private static void collectorsCountingSample() {
        Collector<User, ?, Long> counting = Collectors.counting();
        System.out.println(Stream.of(john, mary, mane).collect(counting));
    }

    private static void collectorsJoiningSample() {
        Collector<CharSequence, ?, String> joining = Collectors.joining(", ");

        Function<? super User, ? extends String> function = User::name;

        System.out.println(Stream.of(john, mary, mane)
                .map(function)
                .collect(joining));
    }

    /**
     * Semelhante ao groupingBy, mas exclusivo para boolean, sempre dá duas chaves:
     * true e false
     */
    private static void partitioningBySample() {
        BiConsumer<? super Boolean, ? super List<User>> biConsumer = (isActive, user) -> System.out
                .println(isActive + " -> " + user);
        Predicate<? super User> predicate = User::isActive;
        Collector<User, ?, Map<Boolean, List<User>>> partitioningBy = Collectors.partitioningBy(predicate);
        Stream.of(john, mary, mane).collect(partitioningBy).forEach(biConsumer);
    }

    private static void groupingBySample() {

        BiConsumer<? super Genre, ? super List<User>> biConsumer = (genre, user) -> System.out
                .println(genre + " -> " + user);
        Function<? super User, ? extends Genre> function = User::genre;
        Collector<User, ?, Map<Genre, List<User>>> groupingBy = Collectors.groupingBy(function);
        Stream.of(john, mary, mane).collect(groupingBy).forEach(biConsumer);
    }

    private static void collectToMapSample() {
        Function<? super User, ? extends String> getUserNameFunction = User::name;
        Function<User, User> identityFunction = Function.identity();
        Collector<User, ?, Map<String, User>> collectorToMap = Collectors.toMap(getUserNameFunction, identityFunction);

        BiConsumer<? super String, ? super User> printActionBiConsumer = (name, user) -> System.out
                .println(name + " -> " + user);
        Stream.of(john, mary, mane).collect(collectorToMap).forEach(printActionBiConsumer);
    }

    private static void collectToSetSample() {
        Collector<User, ?, Set<User>> toSetCollector = Collectors.toSet();
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).collect(toSetCollector).forEach(printActionConsumer);
    }

    private static void collectToListSample() {
        Collector<User, ?, List<User>> toListCollector = Collectors.toList();
        List<User> resultList = Stream.of(john, mary, mane).collect(toListCollector);
        Consumer<? super User> printActionConsumer = System.out::println;
        resultList.forEach(printActionConsumer);
    }

    /**
     * Ele não deve ser usado para transformar dados, mas sim para:
     * Debugging
     * Logging
     * Side effects controlados (ex: métricas, contadores, tracing...)
     * 
     */
    private static void peekSample() {
        Consumer<? super User> loggingConsumer = user -> System.out
                .println("[DEBUG] Processando usuário ativo: " + user.name());
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane)
                .peek(loggingConsumer)
                .forEach(printActionConsumer);
    }

    private static void dropWhileSample() {
        Predicate<? super User> predicate = user -> !user.isActive;
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).dropWhile(predicate).forEach(printActionConsumer);
    }

    private static void takeWhileSample() {
        Predicate<? super User> getFirstNonActiveUsersPredicate = user -> !user.isActive;
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).takeWhile(getFirstNonActiveUsersPredicate).forEach(printActionConsumer);
    }

    private static void skipFirstElementsSample() {
        int nFirstElementsToDiscard = 2;
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).skip(nFirstElementsToDiscard).forEach(printActionConsumer);
    }

    private static void limitIterationsSample() {
        int maxSize = 1;
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).limit(maxSize).forEach(printActionConsumer);
    }

    private static void sortsOrderSample() {
        Function<? super User, ? extends Integer> function = User::age;
        Comparator<User> comparingRule = Comparator.comparing(function);
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(mary, john, mane).sorted(comparingRule).forEach(printActionConsumer);
    }

    private static void removeDuplicatasSample() {
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mary, mane, mary, mane).distinct().forEach(printActionConsumer);
    }

    private static void mapMultiSample() {
        BiConsumer<? super User, ? super Consumer<Object>> flatAllEmailsInOneBiConsumer = (user, downstream) -> {
            List<String> emails = user.emails();
            if (emails != null) {
                emails.forEach(downstream); // desdobra cada email direto na stream
            }
        };

        Consumer<? super Object> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane)
                .mapMulti(flatAllEmailsInOneBiConsumer)
                .forEach(printActionConsumer);
    }

    private static void flatMapSample() {
        Function<? super User, ? extends Stream<? extends String>> flatAllEmailsInOneListfunction = user -> user.emails
                .stream();
        Consumer<? super String> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).flatMap(flatAllEmailsInOneListfunction).forEach(printActionConsumer);
    }

    private static void mapToLongIntDoubleSample() {
        ToLongFunction<? super User> getUsersAgeAsLongFunction = user -> user.age;
        LongConsumer printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).mapToLong(getUsersAgeAsLongFunction).forEach(printActionConsumer);
    }

    private static void streamOfNullableSample() {
        Function<? super User, ? extends Stream<? extends String>> safeGetUserNamefunction = user -> Stream
                .ofNullable(user.name());
        Consumer<? super String> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane, badUser)
                .flatMap(safeGetUserNamefunction) // transforma null em stream vazia
                .forEach(printActionConsumer);
    }

    private static void mapSample() {
        Consumer<? super Integer> printActionConsumer = System.out::println;
        Function<? super User, ? extends Integer> getUsersAgeFunction = user -> user.age;
        Stream.of(john, mary, mane).map(getUsersAgeFunction).forEach(printActionConsumer);
    }

    private static void filterSample() {
        Predicate<? super User> condition = User::isActive;
        Consumer<? super User> printActionConsumer = System.out::println;
        Stream.of(john, mary, mane).filter(condition).forEach(printActionConsumer);
    }

    private static void iterateSample() {
        int initialPoint = 1;
        final Predicate<? super Integer> condition = n -> n <= 5;
        final UnaryOperator<Integer> operation = n -> n * 2;
        final Stream<Integer> squares = Stream.iterate(initialPoint, condition, operation);
        final Consumer<? super Integer> printActionConsumer = System.out::println;

        squares.forEach(printActionConsumer); // 1, 2, 4 (stops when next 8 > 5)
    }

    public record User(String name, int age, boolean isActive, Genre genre, List<String> emails) {
    }

    public enum Genre {
        M, F
    }

}
