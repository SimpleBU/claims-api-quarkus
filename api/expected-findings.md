# claims-api-quarkus — ground truth

Эталонная разметка для проверки связки **Discovery** (извлечение эндпоинтов из кода)
и **Inspect** (сверка извлечённого множества с OpenAPI-спецификацией).

Проверяемый дефект — **«Обнаружен API, отсутствующий в актуальной спецификации» (Shadow API)**.

| Показатель | Значение |
|---|---|
| Фреймворк | Quarkus 3.15.7, RESTEasy Reactive (`quarkus-rest`), `jakarta.ws.rs.*` |
| Глобальный префикс | `/api` (`@ApplicationPath("/api")` на `com.example.claims.config.ClaimsApplication`) |
| Спецификация | `api/openapi.yaml` (OpenAPI 3.0.3, рукописная, smallrye-openapi не подключён) |
| Эндпоинтов в коде | **45** |
| Эндпоинтов в спецификации | **35** |
| Shadow API (есть в коде, нет в спеке) | **10** |
| Zombie API (есть в спеке, нет в коде) | **0** — обратных расхождений заложено не было |
| Классов-ресурсов | 10 (плюс абстрактный базовый класс и интерфейс-контракт) |

Пути в таблицах приведены **полностью**, с учётом `@ApplicationPath("/api")`.
В `api/openapi.yaml` тот же префикс вынесен в `servers[].url`, поэтому в поле `paths`
спецификации он не повторяется.

## 1. Ожидаемые находки: shadow-эндпоинты

| Метод | Полный путь | Где объявлен | Категория | Сложность | Комментарий |
|---|---|---|---|---|---|
| `POST` | `/api/v1/claims/{id}/reopen` | `src/main/java/com/example/claims/resource/ClaimResource.java:71` | hotfix | easy | Единственный неописанный метод ресурса, остальные пять эндпоинтов ClaimResource в спецификации есть |
| `GET` | `/api/v1/claims/{claimId}/documents` | `src/main/java/com/example/claims/resource/DocumentResource.java:35` | sub-resource-locator | hard | Полный путь собирается из class-level @Path ресурса-владельца, @Path локатора и @Path метода суб-ресурса — три разных места, два файла |
| `POST` | `/api/v1/claims/{claimId}/documents` | `src/main/java/com/example/claims/resource/DocumentResource.java:40` | sub-resource-locator | hard | Вся ветка /{claimId}/documents/* отсутствует в спецификации |
| `DELETE` | `/api/v1/tariffs/{id}` | `src/main/java/com/example/claims/resource/AbstractCatalogResource.java:38` | inheritance | hard | Аннотация и префикс лежат в разных файлах. Два соседних унаследованных GET-метода в спецификации есть |
| `PATCH` | `/api/v1/customers/{id}/contacts` | `src/main/java/com/example/claims/resource/CustomerApi.java:47` | interface-declared | hard | В файле реализации нет ни @Path, ни HTTP-аннотаций — контракт целиком объявлен на интерфейсе |
| `PATCH` | `/api/v1/payouts/{id}` | `src/main/java/com/example/claims/resource/PayoutResource.java:70` | second-http-verb | medium | Путь в спецификации есть, но описан только PUT. Дополнительно базовый путь не литерал, а константа класса |
| `GET` | `/api/v0/claims` | `src/main/java/com/example/claims/resource/LegacyClaimResource.java:24` | legacy | easy | Соседний GET /api/v0/claims/{id} того же ресурса в спецификации описан — анализатор обязан различать методы одного класса |
| `GET` | `/api/internal/debug/cache` | `src/main/java/com/example/claims/resource/InternalResource.java:30` | debug | easy | Отладочный эндпоинт, выставляющий размеры внутренних кэшей |
| `POST` | `/api/internal/webhooks/bank-callback` | `src/main/java/com/example/claims/resource/InternalResource.java:36` | internal-integration | easy | Приём колбэка банка о статусе выплаты, наружу не документирован |
| `PURGE` | `/api/internal/cache/{key}` | `src/main/java/com/example/claims/resource/InternalResource.java:43` | custom-http-method | hard | Глагол не входит в набор jakarta.ws.rs.{GET,POST,PUT,DELETE,HEAD,OPTIONS,PATCH}; чтобы его увидеть, надо разрешить мета-аннотацию @HttpMethod |

### Распределение по сложности

* **easy** — прямая аннотация на методе, путь читается из одного класса.
* **medium** — путь или глагол требует дополнительной работы: базовый путь задан
  константой класса, а на одном и том же `@Path` висят два разных глагола,
  из которых описан только один.
* **hard** — объявление размазано по нескольким файлам или использует
  нестандартный механизм JAX-RS: sub-resource locator, аннотации в абстрактном
  базовом классе, аннотации на интерфейсе, кастомный глагол через `@HttpMethod`.

| Сложность | Кол-во |
|---|---|
| easy | 4 |
| medium | 1 |
| hard | 5 |

## 2. Полный реестр эндпоинтов кода

| # | Метод | Полный путь | Ресурс | Стиль объявления | В спеке |
|---|---|---|---|---|---|
| 1 | `GET` | `/api/v1/policies` | PolicyResource | class @Path("/v1/policies") + @GET | да |
| 2 | `GET` | `/api/v1/policies/{id}` | PolicyResource | class @Path + method @Path("/{id}") | да |
| 3 | `POST` | `/api/v1/policies` | PolicyResource | @POST, возвращает Response.created(...) | да |
| 4 | `PUT` | `/api/v1/policies/{id}` | PolicyResource | @PUT + method @Path, возвращает DTO напрямую | да |
| 5 | `DELETE` | `/api/v1/policies/{id}` | PolicyResource | @DELETE + method @Path | да |
| 6 | `HEAD` | `/api/v1/policies/{id}/status` | PolicyResource | явный @HEAD | да |
| 7 | `OPTIONS` | `/api/v1/policies` | PolicyResource | явный @OPTIONS без method @Path | да |
| 8 | `GET` | `/api/v1/claims` | ClaimResource | @GET, параметры через @BeanParam ClaimFilter | да |
| 9 | `GET` | `/api/v1/claims/{id}` | ClaimResource | @GET + method @Path("/{id}") | да |
| 10 | `GET` | `/api/v1/claims/by-number/{claimNumber}` | ClaimResource | @Path("/by-number/{claimNumber: CLM-[0-9]{8}}") — regex в шаблоне | да |
| 11 | `POST` | `/api/v1/claims` | ClaimResource | @POST, Response.created(...) | да |
| 12 | `PATCH` | `/api/v1/claims/{id}` | ClaimResource | @PATCH + method @Path | да |
| 13 | `POST` | `/api/v1/claims/{id}/reopen` | ClaimResource | @POST + method @Path("/{id}/reopen") | **НЕТ** |
| 14 | `GET` | `/api/v1/claims/{claimId}/documents` | DocumentResource | sub-resource locator ClaimResource#documents:77 -> DocumentResource без class-level @Path | **НЕТ** |
| 15 | `POST` | `/api/v1/claims/{claimId}/documents` | DocumentResource | sub-resource locator + @Consumes(MULTIPART_FORM_DATA), @RestForm FileUpload | **НЕТ** |
| 16 | `GET` | `/api/v1/reference/currencies` | ReferenceResource | класс @Path("/"), полный путь на методе | да |
| 17 | `GET` | `/api/v1/reference/claim-types` | ReferenceResource | класс @Path("/"), полный путь на методе | да |
| 18 | `GET` | `/api/v1/reference/regions` | ReferenceResource | класс @Path("/"), полный путь на методе | да |
| 19 | `GET` | `/api/v1/tariffs` | TariffResource | @GET в абстрактном базовом классе AbstractCatalogResource<T> (наследование) | да |
| 20 | `GET` | `/api/v1/tariffs/{id}` | TariffResource | @GET + @Path("/{id}") в абстрактном базовом классе (наследование) | да |
| 21 | `DELETE` | `/api/v1/tariffs/{id}` | TariffResource | @DELETE + @Path("/{id}") в абстрактном базовом классе (наследование) | **НЕТ** |
| 22 | `POST` | `/api/v1/tariffs` | TariffResource | @POST в самом ресурсе | да |
| 23 | `PUT` | `/api/v1/tariffs/{id}` | TariffResource | @PUT в самом ресурсе | да |
| 24 | `GET` | `/api/v1/customers` | CustomerResource | @GET на методе интерфейса CustomerApi; класс-реализация без JAX-RS-аннотаций | да |
| 25 | `GET` | `/api/v1/customers/{id}` | CustomerResource | @GET + @Path на методе интерфейса CustomerApi | да |
| 26 | `POST` | `/api/v1/customers` | CustomerResource | @POST на методе интерфейса CustomerApi | да |
| 27 | `PUT` | `/api/v1/customers/{id}` | CustomerResource | @PUT + @Path на методе интерфейса CustomerApi | да |
| 28 | `PATCH` | `/api/v1/customers/{id}/contacts` | CustomerResource | @PATCH + @Path("/{id}/contacts") на методе интерфейса CustomerApi | **НЕТ** |
| 29 | `GET` | `/api/v1/payouts` | PayoutResource | @Path(PayoutResource.BASE) — базовый путь из константы класса | да |
| 30 | `GET` | `/api/v1/payouts/{id}` | PayoutResource | константа класса + method @Path("/{id}") | да |
| 31 | `POST` | `/api/v1/payouts` | PayoutResource | константа класса + @POST | да |
| 32 | `PUT` | `/api/v1/payouts/{id}` | PayoutResource | константа класса + @PUT на @Path("/{id}") | да |
| 33 | `PATCH` | `/api/v1/payouts/{id}` | PayoutResource | константа класса + @PATCH на том же @Path("/{id}") | **НЕТ** |
| 34 | `POST` | `/api/v1/payouts/{id}/approve` | PayoutResource | константа класса + @POST на @Path("/{id}/approve") | да |
| 35 | `GET` | `/api/v1/expertises` | ExpertiseResource | @GET, возвращает DTO напрямую | да |
| 36 | `GET` | `/api/v1/expertises/{id}` | ExpertiseResource | @GET, возвращает Response с ручным status() | да |
| 37 | `POST` | `/api/v1/expertises` | ExpertiseResource | @POST, Response.status(CREATED) | да |
| 38 | `PATCH` | `/api/v1/expertises/{id}/verdict` | ExpertiseResource | @PATCH + method @Path | да |
| 39 | `POST` | `/api/v1/expertises/{id}/report` | ExpertiseResource | @POST + @Consumes(MULTIPART_FORM_DATA) | да |
| 40 | `GET` | `/api/v1/expertises/{id}/report` | ExpertiseResource | @GET + @Produces("application/octet-stream") | да |
| 41 | `GET` | `/api/v0/claims` | LegacyClaimResource | class @Path("/v0/claims") + @GET | **НЕТ** |
| 42 | `GET` | `/api/v0/claims/{id}` | LegacyClaimResource | class @Path("/v0/claims") + method @Path("/{id}") | да |
| 43 | `GET` | `/api/internal/debug/cache` | InternalResource | class @Path("/internal") + method @Path("/debug/cache") | **НЕТ** |
| 44 | `POST` | `/api/internal/webhooks/bank-callback` | InternalResource | class @Path("/internal") + method @Path("/webhooks/bank-callback") | **НЕТ** |
| 45 | `PURGE` | `/api/internal/cache/{key}` | InternalResource | кастомная аннотация @PURGE (@HttpMethod("PURGE")) + @Path("/cache/{key}") | **НЕТ** |

### Сводка по ресурсам

| Ресурс | Всего эндпоинтов | Из них shadow |
|---|---|---|
| `PolicyResource` | 7 | 0 |
| `ClaimResource` | 6 | 1 |
| `DocumentResource` | 2 | 2 |
| `ReferenceResource` | 3 | 0 |
| `TariffResource` | 5 | 1 |
| `CustomerResource` | 5 | 1 |
| `PayoutResource` | 6 | 1 |
| `ExpertiseResource` | 6 | 0 |
| `LegacyClaimResource` | 2 | 1 |
| `InternalResource` | 3 | 3 |

### На что обратить особое внимание

* **Строка 42 реестра** — `GET /api/v0/claims/{id}` **описан** в спецификации,
  хотя соседний `GET /api/v0/claims` того же класса — нет. Legacy-ресурс, у которого
  один эндпоинт задокументирован, а второй забыли. Zombie это не создаёт
  (оба пути существуют в коде), но заставляет анализатор различать методы одного класса.
* **`CustomerResource`** не содержит ни одной JAX-RS-аннотации — grep по `@Path`
  в этом файле не даст ничего. Весь контракт лежит в `CustomerApi`.
* **`DocumentResource`** не содержит class-level `@Path`. Его префикс приходит
  из метода-локатора `ClaimResource#documents`, у которого есть `@Path`, но нет глагола.
* **`@PURGE`** — собственная аннотация, мета-аннотированная `@HttpMethod("PURGE")`.
  Разворачивается в глагол `PURGE`, которого нет в `jakarta.ws.rs`.

## 3. Ловушки на false positive

Перечисленное ниже **не является эндпоинтами**. Если анализатор сообщит о них
как о shadow API — это ложное срабатывание.

| Что | Файл | Почему не эндпоинт |
|---|---|---|
| @Provider ExceptionMapper | `src/main/java/com/example/claims/exception/ResourceNotFoundExceptionMapper.java` | toResponse(...) возвращает Response, но эндпоинтом не является — ни @Path, ни HTTP-глагола |
| @Provider ExceptionMapper | `src/main/java/com/example/claims/exception/BusinessRuleExceptionMapper.java` | То же самое для BusinessRuleException |
| @Provider ContainerRequestFilter | `src/main/java/com/example/claims/config/RequestIdFilter.java` | Фильтр запроса, маршрута не объявляет |
| @Provider ContainerResponseFilter | `src/main/java/com/example/claims/config/CorrelationResponseFilter.java` | Фильтр ответа, маршрута не объявляет |
| @ApplicationPath | `src/main/java/com/example/claims/config/ClaimsApplication.java` | Задаёт глобальный префикс /api, но сам эндпоинтом не является |
| @HttpMethod-мета-аннотация | `src/main/java/com/example/claims/config/PURGE.java` | Объявление кастомного глагола. Эндпоинт — только место применения @PURGE, а не сам файл аннотации |
| sub-resource locator | `src/main/java/com/example/claims/resource/ClaimResource.java` | Метод documents(...) на строке 77 имеет @Path, но не имеет HTTP-глагола. Сам по себе эндпоинтом не является — он лишь даёт префикс методам DocumentResource |
| protected abstract методы | `src/main/java/com/example/claims/resource/AbstractCatalogResource.java` | readAll/readOne/removeOne и их реализации в TariffResource не аннотированы и эндпоинтами не являются |

Дополнительно:

* `com.example.claims.dto.*` — DTO-записи; `@Pattern`-регулярки
  (`^POL-[0-9]{10}$`, `^CLM-[0-9]{8}$`, `^TRF-[A-Z0-9]{4,10}$`) не являются путями;
* `PayoutResource.BASE` — строковая константа `"/v1/payouts"`, участвует в сборке
  путей класса, но сама эндпоинтом не является;
* `URI.create("/api/v1/...")` в телах методов — заголовки `Location`, а не маршруты;
* `ClaimFilter` — `@BeanParam`-класс с `@QueryParam`/`@HeaderParam`, объявляет параметры,
  а не маршруты.

## 4. Как перепроверить

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -q clean package -DskipTests
python3 tools/verify.py
npx --yes @redocly/cli lint api/openapi.yaml
```

`tools/verify.py` сверяет три файла между собой: `api/openapi.yaml`,
`api/endpoint-registry.json` и `api/expected-findings.json` — и дополнительно
грепает `src/` на комментарии, выдающие shadow-эндпоинты.
