# Лабораторная работа №2

Автор: Гунько Евгений Алексеевич, ИСУ 502010, группа 3210, вариант `12657`.

## Описание

Веб-приложение, определяющее попадание точки в область на координатной плоскости. Страница из ЛР №1 доработана: проверка выполняется на сервере (FastCGI на Java), а результаты хранятся на сервере и сохраняются между запросами.

Пользователь задаёт `X`, `Y` и `R` в форме или двойным кликом по графику. Параметры уходят на сервер AJAX-запросом (`fetch`, метод `GET`). Сервер проверяет попадание и возвращает результат вместе со временем проверки и временем работы скрипта. Форма дополнительно валидирует `X`, `Y`, `R` на сервере.

## Архитектура

```text
Браузер ──GET /api/...──► Apache httpd ──FastCGI──► orchestrator (Java, FastCGI)
 (статика: html/css/js)   (статика + proxy_fcgi)         │
                                                         ├─► validator (Spring, :7001) — валидация и проверка попадания
                                                         └─► writer    (Spring, :7002) ──► Redis — хранение точек
```

| Компонент | Что делает |
|-----------|------------|
| Apache httpd | Отдаёт статику (`index.html`, `src/`, `public/`), запросы `/api/...` перенаправляет на FastCGI |
| `orchestrator` | FastCGI-сервер на Java (`fastcgi-lib.jar`). Принимает запросы, обращается к validator и writer, формирует JSON-ответ |
| `validator` | Spring-сервис: `/api/validate` проверяет допустимые значения, `/api/check` проверяет попадание в область |
| `writer` | Spring-сервис: `/write` добавляет точку в Redis, `/read` отдаёт все точки, `/clear` очищает список |
| Redis | Хранит список `points`, по одной строке JSON на точку |

## API (orchestrator)

| Запрос | Описание |
|--------|----------|
| `GET /api/validate?x=&y=&r=` | Форма. Валидация X, Y, R, проверка попадания, запись точки |
| `GET /api/check?x=&y=&global_r=` | Клик по графику. Проверка попадания и запись точки (диапазоны не проверяются) |
| `GET /api/points/get` | Все сохранённые точки (JSON-массив) |
| `POST /api/points/clear` | Удаляет все точки |

Ответ на `validate` и `check` — одна точка в JSON (`Content-Type: application/json`):

```json
[x, y, r, "Попала", 1730000000000, 842]
```

Поля: `X`, `Y`, `R`, результат («Попала» / «Не попала»), время проверки (мс, Unix) и время работы скрипта (мкс). Время работы считается от начала обработки запроса до получения результата проверки, без учёта записи в Redis.

Ошибки возвращаются как `text/plain`:

| Код | Когда |
|-----|-------|
| 400 | Некорректные параметры или данные не прошли валидацию |
| 405 | Неверный HTTP-метод |
| 404 | Неизвестный адрес |
| 500 | Недоступен validator или writer, либо внутренняя ошибка. Текст показывается пользователю над формой |

## Допустимые значения

| Параметр | Значения |
|----------|----------|
| `X` | целое от `-4` до `4`, выбор из списка |
| `Y` | число от `-5` до `5` |
| `R` | целое от `1` до `5`, чекбоксы (выбран всегда ровно один) |

Валидация выполняется на клиенте и повторяется на сервере (`validator`).

## Функциональность страницы

- Форма проверки точки (`GET`-запрос по AJAX, без перезагрузки страницы).
- Кнопка «Очистить» удаляет точки на сервере и в таблице.
- Таблица результатов: `X`, `Y`, `R`, состояние, время проверки, время работы скрипта. Сортировка по времени проверки.
- График на `Canvas`: четверть круга, прямоугольник и треугольник. Масштаб колесом мыши, перемещение мышью, добавление точки двойным кликом.
- При загрузке страницы все точки запрашиваются с сервера, поэтому таблица сохраняется между визитами и перезагрузками.
- Адаптивная вёрстка под узкие экраны.

## Технологии

HTML5, CSS3 (media queries), JavaScript (ES Modules, `fetch`), Canvas API, Decimal.js, Java 25, `fastcgi-lib.jar`, Spring Boot 3.5, Redis, Apache httpd.

## Структура проекта

```text
Lab2/
├── frontend/
│   ├── index.html
│   ├── public/               # изображения
│   └── src/
│       ├── css/main.css
│       └── js/
│           ├── main.js       # обработчики формы, кнопок, canvas
│           ├── canvas.js     # отрисовка, зум, перетаскивание, клик
│           ├── points.js     # запросы check / validate
│           ├── storage.js    # запросы get / clear
│           ├── table.js      # таблица результатов
│           └── validation.js # клиентская валидация, сообщения об ошибках
├── backend/
│   ├── libs/fastcgi-lib.jar
│   ├── orchestrator/         # FastCGI-сервер
│   ├── validator/            # Spring, :7001
│   ├── writer/               # Spring, :7002, Redis
│   └── Dockerfile            # сборка и образы для трёх Java-сервисов
├── compose.yaml              # запуск всех контейнеров
└── deploy/
    ├── web/                  # Dockerfile и httpd.conf для контейнера web
    └── orchestrator.conf     # конфиг локального Apache httpd (запуск без Docker)
```

## Запуск в Docker

Всё приложение запускается одной командой (нужны Docker и Docker Compose):

```bash
docker compose up --build -d
```

Приложение откроется на `http://localhost:8888/`. Если порт занят, задайте другой: `WEB_PORT=8080 docker compose up --build -d`.

| Контейнер | Образ | Назначение |
|-----------|-------|------------|
| `web` | `httpd:2.4-alpine` | Apache: статика из `frontend/`, `/api/` проксируется на FastCGI |
| `orchestrator` | Java 25 | FastCGI-сервер, порт 24001 |
| `validator` | Java 25 | Spring, порт 7001 |
| `writer` | Java 25 | Spring, порт 7002 |
| `redis` | `redis:7-alpine` | Хранилище точек (том `redis-data`) |

Наружу открыт только порт `web`, остальные контейнеры видят друг друга по именам. Адреса сервисов передаются через переменные окружения (`VALIDATOR_URL`, `WRITER_URL`, `SPRING_DATA_REDIS_HOST`), заданные в `compose.yaml`.

### Требования

- Docker (проверка: `docker --version`);
- Docker Compose v2 (проверка: `docker compose version`).

Java, Gradle, Redis и Apache на машине ставить не нужно: сборка идёт внутри контейнеров. Все команды выполняются из корня проекта (там, где лежит `compose.yaml`).

### Первый запуск

```bash
docker compose up --build -d
```

Первая сборка занимает несколько минут: скачиваются образы и зависимости Gradle. Флаг `-d` запускает контейнеры в фоне. После запуска откройте `http://localhost:8888/`.

### Управление

| Задача | Команда |
|--------|---------|
| Запустить (образы уже собраны) | `docker compose up -d` |
| Пересобрать и запустить после изменений в коде | `docker compose up --build -d` |
| Пересобрать один сервис | `docker compose up --build -d writer` |
| Статус контейнеров | `docker compose ps` |
| Логи всех сервисов | `docker compose logs -f` |
| Логи одного сервиса | `docker compose logs -f orchestrator` |
| Перезапустить сервис | `docker compose restart writer` |
| Остановить, не удаляя контейнеры | `docker compose stop` |
| Запустить после `stop` | `docker compose start` |
| Остановить и удалить контейнеры и сеть (точки в Redis сохраняются) | `docker compose down` |
| Остановить и дополнительно удалить точки (том `redis-data`) | `docker compose down -v` |
| Удалить ещё и собранные образы | `docker compose down -v --rmi local` |

Изменения во `frontend/` тоже требуют пересборки (`docker compose up --build -d web`): файлы копируются в образ при сборке, а не подключаются из папки.

### Если что-то не работает

- **Порт 8888 занят.** Запустите на другом порту: `WEB_PORT=8080 docker compose up --build -d`.
- **Страница открывается, но запросы к API падают с ошибкой 500.** Проверьте, что все контейнеры работают (`docker compose ps`), и посмотрите логи: `docker compose logs validator writer orchestrator`.
- **Нужно начать с чистого состояния.** Выполните `docker compose down -v`, затем `docker compose up --build -d`.

## Запуск без Docker


Сборка backend (нужна Java 25):

```bash
cd backend
./gradlew :orchestrator:shadowJar :validator:bootJar :writer:bootJar
```

Запуск сервисов:

```bash
redis-server &
java -jar validator/build/libs/validator-1.0-SNAPSHOT.jar &
java -jar writer/build/libs/writer-1.0-SNAPSHOT.jar &
java -DFCGI_PORT=24001 -jar orchestrator/build/libs/orchestrator-1.0-SNAPSHOT-all.jar &
```

Apache httpd отдаёт страницу и проксирует API на одном порту `8888`. Конфиг лежит в `deploy/orchestrator.conf`:

```bash
sudo a2enmod proxy proxy_fcgi
sudo cp deploy/orchestrator.conf /etc/apache2/sites-available/
sudo setfacl -m u:www-data:x /home/evgeniy   # чтобы Apache мог читать папку проекта
sudo a2ensite orchestrator
sudo systemctl reload apache2
```

После этого приложение доступно на `http://localhost:8888/`. Сборка frontend не нужна: это обычные ES-модули, `decimal.js` подключается с CDN. Пути к API относительные (`api/check`), поэтому страница должна открываться с того же сервера, что и API, а не из встроенного сервера IDE.
