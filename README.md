# gsk-16-readings

REST API сервис для ежемесячной передаче показаний эл.счетчиков абонентов ГСК.

Сборка
------
Для сборки дистрибутива к корневом каталоге репа запускается команда:

```batch
mvn clean package -Dmaven.test.skip=true
```

После сборки в под-каталоге `/target/` появится файл `readings-0.0.1-SNAPSHOT.jar`

Запуск
------
Взять файл `readings-0.0.1-SNAPSHOT.jar`, сформированный на этапе сборки и запустить приложение:

```batch
java -Dspring.profiles.active=dev -jar readings-0.0.1-SNAPSHOT.jar
```

Тюнинг
------
По умолчанию приложение использует файл конфигурации `application-dev.properties`. В нём лежат все настройки программы,
включая реквизиты доступа к базе данных. Запустить приложение с использованием альтернативного конфигурационного (здесь
-- `application-<env>>.properties`) файла можно так:

```batch
java -Dspring.profiles.active=<env> -jar readings-0.0.1-SNAPSHOT.jar
```

Проверка
--------
При нормальном запуске приложение должно корректно реагировать на запрос

```batch
curl --location --request GET 'http://localhost:8080/swagger-ui.html'
```

т.е. оно должно выдать 200 OK и ендпоинты Swagger-а
