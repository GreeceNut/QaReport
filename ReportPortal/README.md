
# Установка на проект ReportPortal с Docker
## Примеры отчета ReportPortal

 <details>
 <summary> Пример 1 </summary>
<img src=https://github.com/GreeceNut/QaReport/assets/148546011/1109242e-1005-47f8-a516-ccd0aaa9c3ce>
</details>
 <details>
 <summary> Пример 2 </summary>
<img src=https://github.com/GreeceNut/QaReport/assets/148546011/5dc461ca-7aed-4cd8-8a0a-f09f0e463282)>
</details>
 <details>
 <summary> Пример 3 </summary>
<img src=https://github.com/GreeceNut/QaReport/assets/148546011/f642fc08-869a-41a2-a0fa-2280c9acb691)>

</details>


## Установить docker
Сперва необходимо скачать и установить Docker. Скачать для своей системы можно вот [здесь](https://docs.docker.com/get-docker/). 
> [!IMPORTANT]
> - Рекомендуемые требования для использования Docker: 2 CPU 6 GB RAM
> - ОС: MAC |Windows |Linux
> - Для пользователей Windows Docker требует 64-разрядная версия Windows 10 (или выше) и Microsoft Hyper-V
> - Разработчик рекомендует выполнять развертывание в среде базе Linux

## Настройка и развертывание ReporPortal
1. Необходимо произвести загрузку последней версии файла Docker compose с [GitHub](https://github.com/reportportal/reportportal/blob/master/docker-compose.yml) для ReportPortal и поместить его в свой проект
2. Запустить приложение, испольуя следующую команду: `docker-compose -p reportportal up -d --force-recreate`
Где:
`-p reportportal` — добавляет префикс проекта 'reportportal' ко всем контейнерам
`up`  — создает и запускает контейнеры
`-d` — режим daemon
`--force-recreate` — повторно создает контейнеры

## Запуск ReportPortal
При запуске ReportPortal на отдельном хостинге используйте `http://IP_ADDRESS:8080` в противном случае откройте [ReportPortal](http://localhost:8080/ui/) и войдти в систему.
### При входе используйте следующие данные
**Для доступа пользователя**
| Вход  | Пароль |
| --- | --- |
| `default`  | `1q2w3e` |

**Для доступа администратора**
| Вход  | Пароль  |
| --- | --- |
| `superadmin` | `erebus`  |

## Интеграция ReportPortal
В данном случае мы рассмотрим один из методов интеграции ReportPortal для Junit 5 tests через gradle. Более подробную информацию можно найти [здесь](https://github.com/reportportal/agent-java-junit5). 
### Добавить тесты с логами
В классе Теста добавляется метод Logger'a, для того, чтобы протокалировать каждый шаг теста
Например:
```
package com.mycompany.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

public class MyTests {

    private static final Logger LOGGER = LogManager.getLogger(MyTests.class);

    @Test
    void testMySimpleTest() {
        LOGGER.info("Hello from my simple test");
    }
}
```
В данном случае в логах ReportPortal будет отображен тест `testMySimpleTest()`  с комментарием: `"Hello from my simple test"`

### Добавить log4j2.xml в папку reources
Этот файл может выглядеть так:
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </Console>
        <ReportPortalLog4j2Appender name="ReportPortalAppender">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </ReportPortalLog4j2Appender>
    </Appenders>
    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="ConsoleAppender"/>
            <AppenderRef ref="ReportPortalAppender"/>
        </Root>
    </Loggers>
</Configuration>
```
Для того, чтобы логи отображали кириллицу, следует сделать дополнение к `ReportPortalLog4j2Appender`  — `charset="UTF-8"`, то есть:
```
        <ReportPortalLog4j2Appender name="ReportPortalAppender">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"  charset="UTF-8"/>
        </ReportPortalLog4j2Appender>
```
## Настройка ReportPortal

Открыть ReportPortal в браузере (По умолчанию — это _http://localhost:8080_)
Залогиниться как Админ (см. Запуск ReportPortal) и создать проект 
![image](https://github.com/GreeceNut/QaReport/assets/148546011/ba74ba6b-7895-42b0-8959-400adbb7bac3)
Далее следует добавить нового пользователя в проект:
Administrative -> My Test Project -> Members -> Add user
![image](https://github.com/GreeceNut/QaReport/assets/148546011/2560583f-cc6f-480b-9770-d8b9c84bbcba)

## Прикрепить ReportPortal к тестам
### Добавить `reportportal.properties` 
Для привязки ReportPortal к проекту необходимо создать файл `reportportal.properties` в `resources`. Данные для этого файла следует брать из профиля новосозданного юзера — кликнуть по иконке юзера в левом нижнем углу -> Profile. _Например:_
![image](https://github.com/GreeceNut/QaReport/assets/148546011/aacb41d5-b2dc-44de-a330-e6afc434990f)
Далее копируйте данные из профиля
_Например:_
```
rp.endpoint = http://localhost:8080
rp.uuid = d50810f1-ace9-44fc-b1ba-a0077fb3cc44
rp.launch = jack_TEST_EXAMPLE
rp.project = my_test_project
rp.enable = true
```
> [!NOTE]
> Больше деталей про `reportportal.properties` смотреть [здесь](https://github.com/reportportal/client-java)

### Зерегистрировать ReportPortal agent в JUnit 5

Есть два варинта подключения расширения ReportPortal в своих тестах:
1. Указание аннотации @ExtendWith
2. По расположению службы

## Подключение с помощью аннотации 

Каждый тест помеченный @ExtendWith будет репортером для ReportPoral. Это наследуемая аннотация, это означает, что вы можете поместить ее в суперкласс, и все дочерние классы также будут использовать указанное расширение.
_Например:_
```
import com.epam.reportportal.junit5.ReportPortalExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(ReportPortalExtension.class)
public class EnumParametersTest {

	public enum TestParams {
		ONE,
		TWO
	}

	@ParameterizedTest
	@EnumSource(TestParams.class)
	public void testParameters(TestParams param) {
		System.out.println("Test: " + param.name());
	}
}
```
## Подключение через расположение службы

Создать файл с именем `org.junit.jupiter.api.extension.Extension` в `src/test/resources/META-INF/services` папке со следующим содержимым:
```
com.epam.reportportal.junit5.ReportPortalExtension
```

## Настройка `build.gradle` 

Пример полного файла `build.gradle`:

```
[build.gradle]
apply plugin: 'java'
sourceCompatibility = 1.8
targetCompatibility = 1.8

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'com.epam.reportportal:logger-java-log4j:5.2.2'
    implementation 'org.apache.logging.log4j:log4j-api:2.17.1'
    implementation 'org.apache.logging.log4j:log4j-core:2.17.1'
    implementation 'com.epam.reportportal:agent-java-junit5:5.3.2'
}

test {
    testLogging.showStandardStreams = true
    useJUnitPlatform()
    systemProperty 'junit.jupiter.extensions.autodetection.enabled', true
}
```
> [!NOTE]
> Репорт будет сгенерирован только в том случае, если тесты будут запущены с помощью Gradle (например, gradle clean test)


## Просмотр отчета

При правильном подключение агента ReportPortal JUnit 5 и запуска тестов, в ReportPortal появится возможность посмотреть отчеты. Для этого следует перейти на ReportPortal и обнаружить на _левой панели_ вкладку _Launches_:
![image](https://github.com/GreeceNut/QaReport/assets/148546011/7927ae32-3978-4486-a2a0-29aa9eae656b)
![image](https://github.com/GreeceNut/QaReport/assets/148546011/d19a4a13-3a12-4f17-b849-cdec3ef5f21b)
