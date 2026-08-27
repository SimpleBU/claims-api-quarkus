# claims-api-quarkus

Тестовый стенд для проверки связки **Discovery** (извлечение HTTP-эндпоинтов из исходников)
и **Inspect** (сверка извлечённого множества с OpenAPI-спецификацией).

Проверяемый дефект — **«Обнаружен API, отсутствующий в актуальной спецификации» (Shadow API)**.

Доменная область — страховые выплаты: полисы, страховые случаи, документы,
экспертиза, выплаты, клиенты, тарифы, внутренние интеграции.

## Что заложено

| | |
|---|---|
| Quarkus | 3.15.7, RESTEasy Reactive (`quarkus-rest`, `quarkus-rest-jackson`) |
| Java | 17 (`maven.compiler.release=17`) |
| Зависимости | `quarkus-rest`, `quarkus-rest-jackson`, `quarkus-arc`, `quarkus-hibernate-validator` |
| Хранилище | in-memory `ConcurrentHashMap` в `@ApplicationScoped`-бинах, данные-заглушки |
| Сигнатуры | только синхронные, возвращают DTO или `Response`; реактивных типов нет |
| Глобальный префикс | `/api` (`@ApplicationPath("/api")`) |
| Эндпоинтов в коде | **45** в 10 классах-ресурсах |
| Эндпоинтов в спецификации | **35** |
| Shadow API | **10** |
| Zombie API | **0** |

Специально **не** подключены: smallrye-openapi, swagger-annotations, Panache, любая БД,
Lombok, MapStruct. Спецификация написана руками и лежит в `api/openapi.yaml`;
на сборке она не генерируется и рантайм её не перетирает.

## Разнообразие способов объявления эндпоинтов

Стенд намеренно собран так, чтобы простой построчный парсер аннотаций дал неполный результат.

| Приём | Где |
|---|---|
| Class-level `@Path` + method-level `@Path("/{id}")` | `PolicyResource` |
| Класс `@Path("/")`, путь целиком на методах | `ReferenceResource` |
| **Sub-resource locator**: метод без глагола возвращает другой ресурс-класс | `ClaimResource#documents` → `DocumentResource` |
| Аннотации в **абстрактном базовом классе** | `AbstractCatalogResource` → `TariffResource` |
| Аннотации **только на интерфейсе**, класс-реализация чистый | `CustomerApi` → `CustomerResource` |
| **Кастомный HTTP-глагол** через `@HttpMethod("PURGE")` | `config/PURGE.java` → `InternalResource` |
| **Regex** в `@Path`: `{claimNumber: CLM-[0-9]{8}}` | `ClaimResource` |
| Явные `@HEAD` и `@OPTIONS` | `PolicyResource` |
| `@Consumes(MULTIPART_FORM_DATA)` и `@Produces("application/octet-stream")` | `ExpertiseResource`, `DocumentResource` |
| `@BeanParam` вместо перечисления параметров в сигнатуре | `ClaimResource#search` ← `ClaimFilter` |
| `@ApplicationPath("/api")` как глобальный префикс | `config/ClaimsApplication` |
| Возврат `Response` с ручным `status()` **и** возврат DTO напрямую | `ExpertiseResource` |
| Базовый путь ресурса из **константы класса** | `PayoutResource.BASE` |

Ловушки на false positive: два `@Provider ExceptionMapper`, `ContainerRequestFilter`,
`ContainerResponseFilter`, класс `@ApplicationPath`, файл объявления `@PURGE`
и сам метод-локатор. Эндпоинтами они не являются.

## Структура

```
claims-api-quarkus/
├── pom.xml                                  quarkus-bom 3.15.7 + quarkus-maven-plugin
├── api/
│   ├── openapi.yaml                         OpenAPI 3.0.3, рукописная, 35 операций
│   ├── endpoint-registry.json               машиночитаемый реестр всех 45 эндпоинтов кода
│   ├── expected-findings.json               ground truth: 10 shadow-эндпоинтов
│   └── expected-findings.md                 полная таблица + разбор ловушек
├── tools/verify.py                          сверка спеки, реестра и ground truth
└── src/main/java/com/example/claims/
    ├── resource/     10 ресурсов + абстрактный базовый класс + интерфейс-контракт
    ├── dto/          DTO-записи с jakarta-валидацией, enum'ы, sealed-иерархия PayoutDestination
    ├── service/      in-memory сервисы @ApplicationScoped
    ├── model/        внутренние типы (аудит, генераторы id и номеров дел)
    ├── config/       @ApplicationPath, @PURGE, фильтры запроса и ответа
    └── exception/    доменные исключения + @Provider ExceptionMapper
```

## Сборка и проверка

```bash
mvn -q clean package -DskipTests
```

```bash
python3 tools/verify.py
```

```bash
npx --yes @redocly/cli lint api/openapi.yaml
```

Линт проходит с **0 ошибок**. Два предупреждения `no-server-example.com` относятся
к `localhost` и `example.com` в `servers` и для тестового стенда ожидаемы.

`verify.py` требует PyYAML и проверяет, что:

* каждая операция спецификации присутствует в реестре с `inSpec = true` (нет zombie API);
* каждый эндпоинт реестра с `inSpec = true` действительно описан в спецификации;
* число shadow-эндпоинтов в `expected-findings.json` равно `totalEndpointsInCode - totalEndpointsInSpec`;
* ни один shadow-эндпоинт не описан в спецификации;
* в `src/` нет комментариев, выдающих shadow-эндпоинты;
* у каждой операции спецификации есть `operationId`, `tags`, ответ 2xx и минимум два кода ошибок.

Приложение можно запустить (`java -jar target/quarkus-app/quarkus-run.jar`), но для анализа
это не требуется — стенд статический.

## Ожидаемый результат анализатора

10 находок «Shadow API». Полный список с указанием файла, строки, стиля объявления,
категории и сложности — в [api/expected-findings.md](api/expected-findings.md)
и [api/expected-findings.json](api/expected-findings.json).
