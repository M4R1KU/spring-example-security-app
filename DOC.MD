# Modul 183
## Florian Bürgi & Mario Kunz

## Inhalt
1. Bausteine
    1.1. H2
    1.2. Spring
    1.3. Thymeleaf
2. Spring Security Konfiguration
3. Passwort-Persistierung
4. Login und Session Management
5. SSL
6. Systemkommando
7. Brute-Force Schutz
8. Log

## Bausteine
In den folgenden Unterkapiteln wird der verwendete Technologie Stack erklärt. Die Gesamtarchitektur richtet sich nach MVC, daher besteht die Applikation auch aus drei Bausteinen. 
### H2
Als Datenbank wird die H2 Database Engine verwendet.

H2 kann direkt in die Anwendung eingebunden werden und muss, daher nicht auf dem Server installiert werden.
Zudem ist das ganze System sehr schlank und effizient gebaut, was für eine erhöhte Performance sorgt.
H2 besitzt aufgrund der Schlankheit einige Features von anderen Datenbankmanagementsystemen nicht, wie zum Beispiel "Stored Procedures/Functions".
Dies hat aber keinen Einfluss auf das vorliegende Projekt, da auf diese Funktionalitäten verzichtet werden kann.

H2 kann wie schon erwähnt direkt in die Applikation eingebunden werden, dies passiert über die Gradle Konfiguration.
```groovy
runtime('com.h2database:h2')
```
Im Normalmodus entspricht H2 dann einer In-Memory Datenbank, was heisst, dass die Daten nach dem Neustart der Applikation verloren gehen.
Um diesem Verhalten entgegenzuwirken wird im JDBC-String explizit `file` und der Pfad zur Datei (hier im Home-Verzeichnis des Users) angegeben.
```
jdbc:h2:file:~/securityapp
```

### Spring
Spring bzw. Spring Framework ist ein Open-Source Java Framework und kann unteranderem zur Entwicklung von Webapplikation verwendet werden.

Wir verwenden zudem Spring Boot. Spring Boot macht es einfach mit der riesigen Spring Platform zu starten.
Durch sogenannte Starter können verschiedenste Abhängigkeiten zu der Spring Platform (Web, Data, etc.) auf eine einfache Weise realisiert werden.
Die Starter sind immer mit Standardkonfigurationen erstellt, damit eine Spring Boot Applikation theoretisch ohne jeweilige Konfiguration lauffähig wäre.
Dies alles führt dazu, dass Spring Boot Applikationen um einiges eifnacher zum Einrichten sind und man sich auf das wichtige konzentrieren kann. Zudem vermindert man die Konfiguration, indem man nur das nötige bzw. dort wo man vom Standard abweicht konfiguriert.

### Thymeleaf

## Spring Security Konfiguration

Für die Umsetzung der Sicherheits-Anforderungen wird Spring Security verwendet. Spring Security unterstützt out-of-the-box schon sehr viele Sicherheitsmechanismen, wie auch zum Beispiel das erforderliche "salten" von Passwörtern. Auch das Session-Management wird von Haus aus unterstützt und passiert automatisch.

Die Konfigurationseinstellungen werden in den folgenden Unterkapiteln erklärt.

Untenstehend ist die komplette `SecurityConfig` Klasse zu sehen. Dies ist der Ort wo der grösste Teil der Konfiguration vorgenommen wird.

```kotlin
@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun rateLimitingFilter(): FilterRegistrationBean<RateLimitingFilter> {
        val registrationBean = FilterRegistrationBean<RateLimitingFilter>(RateLimitingFilter())
        registrationBean.addUrlPatterns("/do-login/*")
        registrationBean.order = 5
        return registrationBean

    }

    override fun configure(web: WebSecurity?) {
        web!!.ignoring().antMatchers("/css/**")
    }

    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/")
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService)
    }
}
```

### Basis

Damit Spring Security überhaupt aktiviert wird, muss die `@EnableWebSecurity` Annotation an einer Klasse angehängt werden. Diese aktiviert wie schon erwähnt den ganzen Spring Security Stack und markiert unter anderem die aktuelle Klasse als Konfigurationsklasse, was uns dann ermöglicht mit `@Bean` eigene Beans für die Dependency Injection zu erstellen.

Nebst dem Anfügen der `@EnableWebSecurity` Annotation erbt die aktuelle Klasse auch noch von der Klasse `WebSecurityConfigurerAdapter`, welche es uns ermöglicht das Standardverhalten vom Security Stack zu konfigurieren.
Die Einzelheiten dazu werden noch erläutert.

### Beans

Unsere `SecurityConfig` definiert zwei Beans. Zum einen haben wir einen `PasswordEncoder` und eine `FilterRegistrationBean`.

#### PasswordEncoder

Damit die Passwörter beim Speichern gehasht werden und auch richtig verglichen werden (beim Login) definieren den `BCryptPasswordEncoder` als unseren PasswordEncoder. Unteranderem unterstützt dieser Encoder das "salten" von Passwörtern, was aber im Kapitel Passwort-Persistierung noch genauer erklärt wird.

#### FilterRegistrationBean

Die `FilterRegistrationBean`  ist eigentlich nur eine Wrapper-Klasse für einen standardmässigen Filter.

Was ist ein Filter überhaupt? Spring Web enthält eine Implementation des Chain-of-Responsibility-Patterns, welches jede Request, welche zum Server kommt durch eine Reihe von Filtern laufen lässt. Die `FilterRegistrationBean` ist ein Weg um einen Filter zu registrieren. Zudem bietet es uns die Möglichkeit den Filter nur auf bestimmte URL's zu setzen. Wir setzen zudem noch den Standort in der Filter-Chain damit wir sichergehen, dass unser Filter vor der `springSecurityFilterChain` läuft, da diese jenachdem Weiterleitungen durchführt und somit unseren eigenen Filter aus der Chain kickt.

Der Filter, den wir Registrieren, ist der `RateLimitingFilter`, welcher die Anzahl Aufrufe pro Zeit für einen bestimmten Anfrager limitiert.

```kotlin
override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (request is HttpServletRequest) {
        var remoteAddr = request.getHeader("X-FORWARDED_FOR")
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.remoteAddr
        }
        val lastAccess = IP_ACCESS_MAP[remoteAddr]
        if (lastAccess != null && 
            !lastAccess.isBefore(LocalDateTime.now().minus(500, ChronoUnit.MILLIS))) {
            throw RateLimitExceededException("Remote with address $remoteAddr has exceeded its rate limit of 1 request per 500 milliseconds")
        }
        IP_ACCESS_MAP[remoteAddr] = LocalDateTime.now()
    }
    chain.doFilter(request, response)
}
```

## Login und Session Management

Das Login wird wie bereits erwähnt durch eine Standard-Spring Funktionalität abgehandelt. 
Die SecurityConfig erzewingt, dass der Benutzer authentifiziert sein muss um auf um auf die verschiedenen Masken zugreifen kann.
Lediglich der Zugriff auf die Login-Maske bleibt gestattet.
Versucht ein nicht authentifizierter Benutzer auf eine der "gesicherten" Masken zuzugreifen, so wird er durch einen Filter an die Loginseite weitergeleitet.

Meldet sich ein User erfolgreich an so wird eine (neue) JSESSIONID erstellt und in einem Cookie gespeichert.
Das Cookie wird lediglich über HTTPS übertragen um Session Hijacking zu erschweren.

## Registrierung

Damit ein Benutzer einen anderen Benutzer registrieren kann, muss er eingeloggt sein.
Die Eingaben werden dahingehend validiert, dass der Benutzername eindeutig sein muss, das Passwort gewissen Komplexitätsanforderungen entspricht und die beiden Passwort-Felder übereinstimmen.
Die Daten werden via HTTPS übertragen und das Passwort wie beschrieben gehashed.

### Passwort-Persistierung

Die im Kapittel PasswordEncoder beschribene Klasse `BCryptPasswordEncoder`  wird verwendet um aus dem Plaintextpasswort des Users einen sicheren Hash zu generieren.

Der `BCryptPasswordEncoder`  generiert einen Hash, welcher wie folgt aussehen könnte:

`$2a$10$2TjFmQuJ56Qo0hPoLGjLP.sQ1x/tt/Z19o7FilL1toX/RMLPobXAu  `

Die `$` Zeichen trennen die drei Haupteile der 4 Passwortbestandteile:

1. Der Hash-Algorythmus: 

   ```
   1	= 	md5
   2a 	= 	bcrypt
   5	= 	sha-256   
   ```

2. Der Kostenfaktor der Passwortberechnung (Teil des Brute-Force Schutzes)

3. Der Base-64 encoded String aus Salt und Passwort-Hash

   1. Die ersten 22 Zeichen stehen für das Salt
   2. Der Rest für den Hash des eigentlichen Passworts

   Dabei gilt es zu beachten, dass der Base-64 String auf einem spezeillen Zeichensatz basiert.
   Der verwendete Hash-Alogrithmus ist bewusst langsam und die Berechnungskosten erhöhen sich wiederholten berechnungen (Blowfish).

## SSL
Spring Boot unterstüzt das Einbinden von SSL Zertifikaten via die Konfigurationsdatei `application.yml`.
Alle vom Framework erstellten Webservice-Endpoints basieren dann auf mit dem Zertifikat abgsicherten WebSockets.

Beispiel Konfiguration:

```yaml
server:
  port: 8443
  ssl:
    key-store: school_security_application_keystore.jks
    key-store-password: **store_pw** 
    keyAlias: school_security_certificate
    key-store-type: JKS
    key-password: **key_pw**
```

Alternativ könnte über eine Verschlüsselung auf Reverse-Proxy Ebene nachgedacht werden.

## Systemkommando

Für die Ausführung von Systemkommandos bietet sich im Java Umfeld die Verwendung des ProcessBuilders an. 
Die Verwendung dieses Builders bietet zwei wesentliche Vorteile:
1. Die Plattformunabhängigkeit bleibt bestehen. Solange das übergebene Kommando im aktuellen Systemkontext ausführbar ist, sind keine weiteren Massnahmen nötig.
2. Trennung von Kommando und Argumenten
Die ProcessBuilder-Bibliothek trennt beim erstellen eines Prozesses automatisch zwischen den dem Kommando und den dazugehörigen Argumenten.
Dadurch wird sichergestellt, dass via die Parametereingabe kein Übergriff in die Kontrollebene geschehen kann.

Um das System zusätzlich abzusichern, werden alle Parameter durch eine Escaping-Libraray in "ungefährlichen" Text übersetzt bevor diese an den oben beschriebenen ProcessBuilder übergeben werden.

## Brute-Force Schutz

Als Schutzmassnahme gegen Brute-Force-Angriffe wird unter Anderem ein Filter eingesetzt.
Der Filter liegt vor der Login-Maske und speichert die IP-Adresse und die Zeit jedes Zugriffs.
Falls von einer IP zu viele Requests an die Login-Schnittstelle gesendet werden, werden die Request vom Filter zurückgewiesen und die Daten gelangen nie zum Controller.
Nach jedem erfolgreichen Login werden die Einträge der jeweiligen IP durch einen ``EventHandler`` wieder zurückgesetzt.

Der untenstehende Code zeigt unser Vorgehen:



## Log

Spring verfügt über vordefinierte Logebenen für verschiedene Applikationsteile.
Im Sourcecode kann mit einfachsten Mitteln in dieses Log geschriben werden.

```kotlin
        val LOGGER = LoggerFactory.getLogger(SystemCommandService::class.java)
        LOGGER.info("Executing system command: $command with parameters: ${argumentArray.joinToString(" ")}")
```
Dieses Codestück generiert einen Log-Eintrag der wie folgt aussehen könnte:
`2018-06-12 19:34:44.211  INFO 1632 --- [nio-8443-exec-7] m.m.g.m.s.service.SystemCommandService   : Executing system command: java with parameters: -version
`

Die folgende Konfiguration schreibt die gewünschten Logs an den entsprechenden Pfad:

```yaml
logging:
  file: /var/log/m183-security-app.log
  level:
    root: INFO
    org:
      springframework:
        web: INFO
    hibernate: ERROR
```

