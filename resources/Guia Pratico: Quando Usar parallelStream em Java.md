## 🧵 Guia Prático: Quando Usar `parallelStream()` em Java

Este documento tem como objetivo servir de referência rápida e prática para entender **quando usar ou evitar `parallelStream()`**, com base em cenários reais de uso, benchmarking e limitações conhecidas da API.

---

### ✅ Quando usar `parallelStream()`

Use `parallelStream()` quando TODAS as condições abaixo forem verdadeiras:

1. **Grande volume de dados**: geralmente mais de **10.000 elementos**
2. **Operação CPU-bound**: custo computacional alto por elemento (ex: regex complexa, criptografia, compressão)
3. **Ordem dos elementos não importa**
4. **Não há efeitos colaterais** (sem DB, arquivos, logs dentro da stream)
5. **Você consegue validar o ganho real de performance** com benchmarks

#### Exemplo:

```java
long count = emails.parallelStream()
    .filter(ParallelStreamsSamples::isValidEmail)
    .count();
```

---

### ❌ Quando evitar `parallelStream()`

Evite o uso de `parallelStream()` se alguma das situações abaixo ocorrer:

* A lista é pequena (ex: < 1.000 elementos)
* A operação é leve (ex: `.toUpperCase()`, `.substring()`, etc.)
* Há I/O envolvido (ex: salvar no banco, escrever arquivo, HTTP)
* É importante manter a **ordem dos elementos**
* O código roda dentro de ambientes controlados como **controllers HTTP**, onde já há concorrência gerenciada pelo servidor (Tomcat/Jetty). Adicionar `parallelStream()` pode gerar contenção com o thread pool da aplicação e prejudicar a performance global.
* Você não mediu e apenas "achou" que paralelizar seria melhor

#### Anti-exemplo:

```java
users.parallelStream()
    .forEach(user -> userRepository.save(user)); // Risco de concorrência no banco
```

---

### ⚠️ `parallelStream()` e Spring Framework

No contexto de aplicações Spring (especialmente APIs REST), o uso de `parallelStream()` é **fortemente desaconselhado**. Isso porque:

* Controllers HTTP já operam em threads concorrentes, gerenciadas pelo container (Tomcat, Jetty, etc.)
* `parallelStream()` usa o `ForkJoinPool.commonPool`, que é global e compartilhado
* O uso indiscriminado pode causar **contenção** entre as threads da aplicação e as do paralelismo
* Em cenários de alta concorrência, o paralelismo pode **saturar o pool comum**, gerando lentidão ou comportamento errático

#### ✅ Alternativas em aplicações Spring:

* Use `ExecutorService` com configuração controlada
* Use `@Async` em serviços Spring para executar tarefas paralelas com isolamento
* Use `CompletableFuture.supplyAsync(..., executor)` com injeção de `TaskExecutor`

#### Exemplo com `@Async`:

```java
@Async
public CompletableFuture<List<String>> processEmails(List<String> emails) {
    return CompletableFuture.supplyAsync(() ->
        emails.stream()
              .filter(this::isValid)
              .collect(Collectors.toList())
    );
}
```

---

### 📏 Heurística de decisão

| Volume de dados          | Operação leve | Operação pesada  |
| ------------------------ | ------------- | ---------------- |
| < 1.000 elementos        | ❌ Não usar    | ❌ Não usar       |
| 1.000 - 10.000 elementos | ⚠️ Depende    | ⚠️ Avaliar       |
| > 10.000 elementos       | ⚠️ Avaliar    | ✅ Usar se seguro |

---

### 📊 Como validar corretamente

1. **Meça o tempo** de execução com `System.nanoTime()` ou ferramenta de benchmark (ex: JMH):

```java
long start = System.nanoTime();
process(data.stream());
long end = System.nanoTime();
System.out.println("Duração: " + (end - start)/1_000_000 + "ms");
```

2. **Valide a exatidão do resultado final**

```java
assert isValid(result);
```

3. **Compare sequencial vs paralelo em condições similares**

---

### 🧰 Alternativas ao `parallelStream()`

Para mais controle e previsibilidade, use `ExecutorService`:

```java
ExecutorService executor = Executors.newFixedThreadPool(8);
List<Future<Result>> results = tarefas.stream()
    .map(task -> executor.submit(() -> task.run()))
    .collect(Collectors.toList());
```

---

### 📌 Resumo para copiar e colar

```java
// ✅ Use parallelStream() quando:
// - A lista for grande (10 mil+)
// - A operação for pesada (CPU-bound)
// - A ordem dos dados não importar
// - Você puder medir ganho real em staging

// ❌ Evite quando:
// - A lista for pequena
// - A operação for leve
// - Houver efeitos colaterais (salvar em DB, logs, etc)
// - Estiver dentro de APIs web (ex: Spring controllers) sem controle de concorrência
```

---

> 💡 Dica final: nem todo problema precisa de paralelismo. Sempre meça antes de decidir.
