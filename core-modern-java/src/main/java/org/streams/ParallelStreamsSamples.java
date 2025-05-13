package org.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public interface ParallelStreamsSamples {


    final User john = new User("john", 1, false, Gender.M, List.of("john@email.com"));
    final User mary = new User("mary", 2, true, Gender.F, List.of("mary@email.com", "mary2@email.com"));
    final User mane = new User("mane", 3, false, Gender.M,
            List.of("mane@email.com", "mane2@email.com", "mane3@email.com"));
    final User badUser = new User(null, 0, true, Gender.M, null);

    public static void main(String[] args) {

        //goodSample(10_000);
        badSample(10);

    }
    public static void goodSample(int quantity){
        process(quantity);
    }

    /**
     * Exemplo de mau uso pq o custo de gerar essas streams, em um ambiente produtivo é IMPREVISIVEL
     * @param quantity
     */
    public static void badSample(int quantity){
        process(quantity);
    }


    private static void process(int quantity) {
        List<String> emails = EmailFactory.generateRandomEmails(quantity);

        System.out.println("Validando e-mails com stream sequencial...");
        long startSeq = System.nanoTime();
        long validEmailsSeq = emails.stream()
                .filter(ParallelStreamsSamples::isValidEmail)
                .count();
        long durationSeq = System.nanoTime() - startSeq;
        System.out.println("Total válidos (sequencial): " + validEmailsSeq);
        System.out.println("Tempo (ms): " + durationSeq / quantity);

        System.out.println("\nValidando e-mails com parallelStream...");
        long startPar = System.nanoTime();
        long validEmailsPar = emails.parallelStream()
                .filter(ParallelStreamsSamples::isValidEmail)
                .count();
        long durationPar = System.nanoTime() - startPar;
        System.out.println("Total válidos (paralelo): " + validEmailsPar);
        System.out.println("Tempo (ms): " + durationPar / quantity);

        System.out.println(String.format("\nGanho de performance: %.2fx", (double) durationSeq / durationPar));
    }

    /**
     * Simula uma validação de e-mail relativamente custosa, para fins de benchmark.
     */
    static boolean isValidEmail(String email) {
        try {
            Thread.sleep(0, 500_000); // 0.5ms artificial delay por e-mail (~0.5s para 1000)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return email != null && email.contains("@") && email.length() > 5;
    }

    public record User(String name, int age, boolean isActive, Gender gender, List<String> emails) {
    }

    public enum Gender {
        M, F
    }

    /**
     * Gera uma lista de e-mails aleatórios com tamanho especificado.
     */
    class EmailFactory {
        private static final String[] domains = {"example.com", "email.com", "mail.org", "site.net"};

        public static List<String> generateRandomEmails(int quantity) {
            Random random = ThreadLocalRandom.current();
            return new ArrayList<>(quantity) {{
                for (int i = 0; i < quantity; i++) {
                    String name = "user" + random.nextInt(1_000_000);
                    String domain = domains[random.nextInt(domains.length)];
                    add(name + "@" + domain);
                }
            }};
        }
    }
}
